package dev.jsinco.simplequests.storage

import com.google.gson.Gson
import dev.jsinco.simplequests.SimpleQuests
import dev.jsinco.simplequests.Util
import dev.jsinco.simplequests.enums.StorageMethod
import dev.jsinco.simplequests.objects.ActiveQuest
import dev.jsinco.simplequests.objects.QuestPlayer
import dev.jsinco.simplequests.objects.StorableQuest
import java.io.File
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.logging.Level

class SQLiteStorage : DataManager {

    private val plugin: SimpleQuests = SimpleQuests.getInstance()
    private val gson = Gson()
    private lateinit var connection: Connection

    init {
        val file = File(plugin.dataFolder, "saves.db")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:$file")
        } catch (ex: SQLException) {
            plugin.logger.log(Level.SEVERE, "SQLite exception on initialize", ex)
        }

        try {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS questPlayers (uuid VARCHAR(36) PRIMARY KEY, completedQuestIds TEXT, activeQuests TEXT, showActionBarProgress BOOLEAN);")
                .use { statement -> statement.executeUpdate(); statement.close() }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun getCompletedQuestIds(uuid: UUID): List<String> {
        try {
            connection.prepareStatement("SELECT * FROM questPlayers WHERE uuid=?;").use { statement ->
                statement.setString(1, uuid.toString())
                val jsonStringList = statement.executeQuery().getString("completedQuestIds")
                statement.close()

                return gson.fromJson(jsonStringList, List::class.java) as? List<String> ?: emptyList()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return emptyList()
    }

    override fun setCompletedQuestIds(uuid: UUID, questIds: List<String>) {
        try {
            connection.prepareStatement("INSERT OR REPLACE INTO questPlayers (uuid, completedQuestIds) VALUES (?, ?);").use { statement ->
                statement.setString(1, uuid.toString())
                statement.setString(2, gson.toJson(questIds))
                statement.executeUpdate()
                statement.close()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    @Suppress("DuplicatedCode")
    override fun getActiveQuests(uuid: UUID): ConcurrentLinkedQueue<ActiveQuest> {
        try {

            connection.prepareStatement("SELECT * FROM questPlayers WHERE uuid=?;").use { statement ->
                statement.setString(1, uuid.toString())
                val jsonStringList = statement.executeQuery().getString("activeQuests")
                statement.close()

                val list = gson.fromJson(jsonStringList, List::class.java)
                return DataUtil.getActiveQuestsList(list, uuid)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return ConcurrentLinkedQueue()
    }

    override fun setActiveQuests(uuid: UUID, activeQuests: ConcurrentLinkedQueue<ActiveQuest>) {
        try {
            connection.prepareStatement("INSERT OR REPLACE INTO questPlayers (uuid, activeQuests) VALUES (?, ?);").use { statement ->
                statement.setString(1, uuid.toString())
                statement.setString(2, gson.toJson(StorableQuest.serializeToStorableQuests(activeQuests)))
                statement.executeUpdate()
                statement.close()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun showActionBarProgress(uuid: UUID): Boolean {
        try {
            connection.prepareStatement("SELECT * FROM questPlayers WHERE uuid=?;").use { statement ->
                statement.setString(1, uuid.toString())
                val showActionBarProgress = statement.executeQuery().getBoolean("showActionBarProgress")
                statement.close()

                return showActionBarProgress
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }

    override fun setShowActionBarProgress(uuid: UUID, showActionBarProgress: Boolean) {
        try {
            connection.prepareStatement("INSERT OR REPLACE INTO questPlayers (uuid, showActionBarProgress) VALUES (?, ?);").use { statement ->
                statement.setString(1, uuid.toString())
                statement.setBoolean(2, showActionBarProgress)
                statement.executeUpdate()
                statement.close()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun loadQuestPlayer(uuid: UUID): QuestPlayer {
        Util.debugLog("Loading QuestPlayer: $uuid")
        try {
            connection.prepareStatement("SELECT * FROM questPlayers WHERE uuid=?;").use { statement ->
                statement.setString(1, uuid.toString())
                val completedQuestIds: List<String> = gson.fromJson(statement.executeQuery().getString("completedQuestIds"), List::class.java) as? List<String> ?: emptyList()
                val activeQuests: ConcurrentLinkedQueue<ActiveQuest> = DataUtil.getActiveQuestsList(gson.fromJson(statement.executeQuery().getString("activeQuests"), List::class.java), uuid)
                val showActionBarProgress: Boolean = statement.executeQuery().getBoolean("showActionBarProgress")
                statement.close()

                return QuestPlayer(uuid, completedQuestIds, activeQuests, showActionBarProgress)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return QuestPlayer(uuid, emptyList(), ConcurrentLinkedQueue(), false)
    }

    override fun saveQuestPlayer(questPlayer: QuestPlayer) {
        try {
            connection.prepareStatement("INSERT OR REPLACE INTO questPlayers (uuid, completedQuestIds, activeQuests) VALUES (?, ?, ?);").use { statement ->
                statement.setString(1, questPlayer.uuid.toString())
                statement.setString(2, gson.toJson(questPlayer.completedQuests))
                statement.setString(3, gson.toJson(StorableQuest.serializeToStorableQuests(questPlayer.activeQuests)))
                statement.executeUpdate()
                statement.close()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        Util.debugLog("Saved QuestPlayer: ${questPlayer.uuid}")
    }

    override fun getStorageMethod(): StorageMethod {
        return StorageMethod.SQLITE
    }

    fun closeConnection() {
        try {
            connection.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

}