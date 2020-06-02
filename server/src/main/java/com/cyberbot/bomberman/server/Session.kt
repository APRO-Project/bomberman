package com.cyberbot.bomberman.server

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.cyberbot.bomberman.core.controllers.GameStateController
import com.cyberbot.bomberman.core.models.net.data.PlayerData
import com.cyberbot.bomberman.core.models.net.packets.GameSnapshotPacket
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket
import com.cyberbot.bomberman.core.models.tiles.TileMap
import com.cyberbot.bomberman.core.utils.Constants
import java.io.IOException
import java.net.DatagramPacket
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class Session(private val socket: GameSocket) {
    private val clientSessions = HashMap<ClientConnection, PlayerSession>()
    private val gameStateController: GameStateController
    private val world: World = World(Vector2(0F, 0F), false)
    private val simulationService: ScheduledExecutorService
    private val tickService: ScheduledExecutorService
    private var lastUpdate: Long
    var gameStarted: Boolean
        private set

    init {
        val map = TileMap(world, "./map/bomberman_main.tmx")
        gameStateController = GameStateController(world, map)
        gameStarted = false
        lastUpdate = System.currentTimeMillis()
        simulationService = ScheduledThreadPoolExecutor(1)
        tickService = ScheduledThreadPoolExecutor(1)
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
        clientSessions[connection] = PlayerSession(playerEntity)
    }

    fun removeClient(connection: ClientConnection): Boolean {
        check(!gameStarted) { "The game has already started, cannot remove clients" }
        return clientSessions.remove(connection) != null
    }

    fun hasClient(connection: ClientConnection): Boolean {
        return clientSessions.containsKey(connection)
    }

    private fun tick() {
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
            val payload = packet.toByteArray()
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
                val t0 = System.currentTimeMillis()
                update((t0 - lastUpdate) / 1000f)
                lastUpdate = t0
            },
            0,
            1000000 / Constants.SIM_RATE.toLong(),
            TimeUnit.MICROSECONDS
        )
    }

    private fun scheduleTickUpdates() {
        simulationService.scheduleAtFixedRate(
            { tick() },
            0,
            1000000 / Constants.TICK_RATE.toLong(),
            TimeUnit.MICROSECONDS
        )
    }

    @Synchronized
    private fun update(delta: Float) {
        world.step(delta, 6, 2)
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