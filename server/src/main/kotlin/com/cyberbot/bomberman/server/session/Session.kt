package com.cyberbot.bomberman.server.session

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.cyberbot.bomberman.core.controllers.GameStateController
import com.cyberbot.bomberman.core.models.net.SerializationUtils
import com.cyberbot.bomberman.core.models.net.data.PlayerData
import com.cyberbot.bomberman.core.models.net.packets.GameSnapshotPacket
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket
import com.cyberbot.bomberman.core.models.tiles.loader.TileMapFactory
import com.cyberbot.bomberman.core.utils.Constants
import com.cyberbot.bomberman.server.models.ClientConnection
import java.io.FileNotFoundException
import java.io.IOException
import java.net.DatagramPacket
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Session(private val socket: GameSocket) {
    private val clientSessions = HashMap<ClientConnection, PlayerSession>()
    private val gameStateController: GameStateController
    private val world: World = World(Vector2(0F, 0F), false)
    private val simulationService = ScheduledThreadPoolExecutor(1)
    private val tickService = ScheduledThreadPoolExecutor(1)
    private var lastUpdate = System.currentTimeMillis()
    var gameStarted: Boolean = false
        private set

    private val worldUpdateLock = ReentrantLock()
    private val worldUpdatedCondition = worldUpdateLock.newCondition()

    init {
        val mapPath = Thread.currentThread().contextClassLoader.getResource("map/bomberman_main.tmx")
            ?: throw FileNotFoundException("Map file not found")
        val map = TileMapFactory.createTileMap(world, mapPath.path)
        gameStateController = GameStateController(world, map)
    }

    fun onSnapshot(connection: ClientConnection, packet: PlayerSnapshotPacket): Boolean {
        return clientSessions[connection]?.run {
            onNewSnapshot(packet)
            return true
        } ?: false
    }

    fun addClient(connection: ClientConnection, player: PlayerData) {
        check(!gameStarted) { "The game has already started, cannot add clients" }
        val playerEntity = player.createEntity(world)
        gameStateController.addPlayer(playerEntity)
        val playerSession = PlayerSession(playerEntity)
        clientSessions[connection] = playerSession
        playerSession.addListener(gameStateController)
    }

    fun removeClient(connection: ClientConnection): Boolean {
        check(!gameStarted) { "The game has already started, cannot remove clients" }
        return clientSessions.remove(connection) != null
    }

    fun hasClient(connection: ClientConnection): Boolean {
        return clientSessions.containsKey(connection)
    }

    private fun tick() {
        worldUpdateLock.withLock {
            worldUpdatedCondition.await()
        }

        for ((session, packet) in getUpdatePackets()) {
            try {
                socket.send(packet)
                session.clearErrors()
            } catch (e: IOException) {
                session.onError()
            }
        }
    }

    private fun getUpdatePackets(): Map<PlayerSession, DatagramPacket> {
        val snapshot = gameStateController.createSnapshot()

        return clientSessions.entries.associateBy({ it.value }, {
            val packet = GameSnapshotPacket(it.value.sequence, snapshot)
            val payload = SerializationUtils.serialize(packet)
            DatagramPacket(payload, payload.size, it.key.address, it.key.port)
        })
    }

    fun startGame() {
        check(!gameStarted) { "The game has already been started" }
        gameStarted = true

        scheduleSimulationUpdates()
        scheduleTickUpdates()
    }

    private fun pauseGame() {
        check(gameStarted) { "The game has not yet been started" }
        simulationService.shutdown()
        tickService.shutdown()
    }

    private fun scheduleSimulationUpdates() {
        simulationService.scheduleAtFixedRate(
            {
                try {
                    val t0 = System.currentTimeMillis()
                    update((t0 - lastUpdate) / 1000f)
                    lastUpdate = t0
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            0,
            1000000 / Constants.SIM_RATE.toLong(),
            TimeUnit.MICROSECONDS
        )
    }

    private fun scheduleTickUpdates() {
        tickService.scheduleAtFixedRate(
            this::tick,
            0,
            1000000 / Constants.TICK_RATE.toLong(),
            TimeUnit.MICROSECONDS
        )
    }

    @Synchronized
    private fun update(delta: Float) {
        worldUpdateLock.withLock {
            world.step(delta, 6, 2)
            worldUpdatedCondition.signalAll()
        }

        for (session in clientSessions.values) {
            session.update(delta)
        }
        gameStateController.update(delta)
    }

    companion object {
        fun getPlayerSpawnPosition(playerIndex: Int, mapSize: Int = 15): Vector2 {
            // TODO: Store spawn information in the map file
            return when (playerIndex) {
                0 -> Vector2(1.5f, mapSize - 1.5f)
                1 -> Vector2(mapSize - 1.5f, 1.5f)
                2 -> Vector2(mapSize - 1.5f, mapSize - 1.5f)
                3 -> Vector2(1.5f, 1.5f)
                else -> throw IllegalArgumentException("Invalid player index")
            }
        }
    }
}