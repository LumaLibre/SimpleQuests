package dev.jsinco.simplequests.storage

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
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
import java.util.logging.Level

class SQLiteStorage : DataManager {

    private val plugin: SimpleQuests = SimpleQuests.getInstance()
    private val gson = Gson()
    private lateinit var connection: Connection

    init {
        val file = File(plugin.dataFolder, "data.db")
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
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS questPlayers (uuid VARCHAR(36) PRIMARY KEY, completedQuestIds TEXT, activeQuests TEXT);")
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

    @Suppress("UNCHECKED_CAST", "DuplicatedCode")
    override fun getActiveQuests(uuid: UUID): List<ActiveQuest> {
        try {
            val activeQuests: MutableList<ActiveQuest> = mutableListOf()

            connection.prepareStatement("SELECT * FROM questPlayers WHERE uuid=?;").use { statement ->
                statement.setString(1, uuid.toString())
                val jsonStringList = statement.executeQuery().getString("activeQuests")
                statement.close()

                val list: List<LinkedTreeMap<*, *>> = gson.fromJson(jsonStringList, List::class.java) as? List<LinkedTreeMap<*, *>> ?: emptyList()

                for (linkedTreeMap in list) {
                    activeQuests.add(ActiveQuest(linkedTreeMap["category"] as String, linkedTreeMap["id"] as String, (linkedTreeMap["progression"] as Double).toInt()))
                }

                return activeQuests
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return emptyList()
    }

    override fun setActiveQuests(uuid: UUID, activeQuests: List<ActiveQuest>) {
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

    override fun loadQuestPlayer(uuid: UUID): QuestPlayer {
        Util.debugLog("Loading QuestPlayer: $uuid")
        return QuestPlayer(uuid, this.getCompletedQuestIds(uuid), this.getActiveQuests(uuid))
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