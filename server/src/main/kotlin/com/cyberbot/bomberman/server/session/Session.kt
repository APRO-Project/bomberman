package com.cyberbot.bomberman.server.session

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.cyberbot.bomberman.core.controllers.GameStateController
import com.cyberbot.bomberman.core.controllers.WorldChangeListener
import com.cyberbot.bomberman.core.models.entities.Entity
import com.cyberbot.bomberman.core.models.entities.PlayerEntity
import com.cyberbot.bomberman.core.models.net.SerializationUtils
import com.cyberbot.bomberman.core.models.net.data.PlayerData
import com.cyberbot.bomberman.core.models.net.packets.GameSnapshotPacket
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket
import com.cyberbot.bomberman.core.models.tiles.loader.TileMapFactory
import com.cyberbot.bomberman.core.utils.Constants
import com.cyberbot.bomberman.core.utils.scheduleAtFixedRate
import com.cyberbot.bomberman.server.models.ClientConnection
import java.io.IOException
import java.net.DatagramPacket
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashSet
import kotlin.concurrent.schedule
import kotlin.concurrent.withLock

class Session(private val socket: GameSocket, private val gameStopDelay: Long = 100) : WorldChangeListener {
    private val clientSessions = HashMap<ClientConnection, PlayerSession>()
    private val gameStateController: GameStateController
    private val world: World = World(Vector2(0F, 0F), false)
    private val simulationService = ScheduledThreadPoolExecutor(1)
    private val tickService = ScheduledThreadPoolExecutor(1)
    private var lastUpdate = System.currentTimeMillis()
    private val worldUpdateLock = ReentrantLock()
    private val leaderboard = LinkedHashSet<Long>()

    private val worldUpdatedCondition = worldUpdateLock.newCondition()

    var gameFinished = false
        private set
    var gameStarted: Boolean = false
        private set

    init {
        val mapPath = "map/bomberman_main.tmx"
        val map = TileMapFactory.createTileMap(world, mapPath)
        gameStateController = GameStateController(world, map)
        gameStateController.addListener(this)
    }

    override fun onEntityRemoved(entity: Entity?) {
        if (entity is PlayerEntity) {
            leaderboard.add(entity.id)
        }
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
            while(world.isLocked) {
                worldUpdatedCondition.await()
            }
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

    private fun stopGame() {
        check(gameStarted) { "The game has not yet been started" }

        tickService.shutdown()
        tickService.awaitTermination(500, TimeUnit.MILLISECONDS)
        simulationService.awaitTermination(500, TimeUnit.MILLISECONDS)

        gameStateController.dispose()
        world.dispose()

        socket.gameStopped(LinkedHashSet(leaderboard.reversed()))
    }

    private fun scheduleSimulationUpdates() {
        simulationService.scheduleAtFixedRate(1000000L / Constants.SIM_RATE, 0, TimeUnit.MICROSECONDS) {
            try {
                val t0 = System.currentTimeMillis()
                update((t0 - lastUpdate) / 1000f)
                lastUpdate = t0
            } catch (e: Exception) {
                // Exceptions thrown in the ScheduledExecutorService are caught
                // and returned in a Future only when the executor service is stopped.
                // This is the simplest way to prevent the service from halting and
                // getting any debugging information from the exceptions
                e.printStackTrace()
            }
        }
    }

    private fun scheduleTickUpdates() {
        tickService.scheduleAtFixedRate(1000000L / Constants.TICK_RATE, 0, TimeUnit.MICROSECONDS) {
            try {
                tick()
            } catch (e: Exception) {
                // Exceptions thrown in the ScheduledExecutorService are caught
                // and returned in a Future only when the executor service is stopped.
                // This is the simplest way to prevent the service from halting and
                // getting any debugging information from the exceptions
                e.printStackTrace()
            }
        }
    }

    private fun update(delta: Float) {
        worldUpdateLock.withLock {
            world.step(delta, 6, 2)
            worldUpdatedCondition.signalAll()
        }

        if (leaderboard.size >= clientSessions.size - 1) {
            simulationService.shutdown()
            gameFinished = true
            Timer().schedule(gameStopDelay) { stopGame() }

            // Add remaining players to the leaderboard in ambiguous order
            leaderboard.addAll(clientSessions.values.map { it.id })
        }

        clientSessions.values.forEach { it.update(delta) }
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