package kr.kjh9211.money.manager

import kr.kjh9211.money.MoneyPlugin
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID

class EconomyManager(private val plugin: MoneyPlugin) {

    private lateinit var connection: Connection

    fun load() {
        plugin.dataFolder.mkdirs()
        val dbPath = plugin.dataFolder.resolve("economy.db").absolutePath
        connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
        connection.createStatement().use { stmt ->
            // WAL 모드: 읽기/쓰기 동시 접근 성능 향상
            stmt.execute("PRAGMA journal_mode=WAL")
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS balances (
                    uuid    TEXT PRIMARY KEY,
                    balance REAL NOT NULL DEFAULT 0.0
                )
            """.trimIndent())
        }
    }

    fun close() {
        if (::connection.isInitialized && !connection.isClosed) {
            connection.close()
        }
    }

    @Synchronized
    fun getBalance(uuid: UUID): Double {
        connection.prepareStatement("SELECT balance FROM balances WHERE uuid = ?").use { stmt ->
            stmt.setString(1, uuid.toString())
            stmt.executeQuery().use { rs ->
                return if (rs.next()) rs.getDouble("balance")
                else plugin.config.getDouble("starting-balance", 0.0)
            }
        }
    }

    @Synchronized
    fun setBalance(uuid: UUID, amount: Double) {
        val clamped = maxOf(0.0, amount)
        connection.prepareStatement("""
            INSERT INTO balances (uuid, balance) VALUES (?, ?)
            ON CONFLICT(uuid) DO UPDATE SET balance = excluded.balance
        """.trimIndent()).use { stmt ->
            stmt.setString(1, uuid.toString())
            stmt.setDouble(2, clamped)
            stmt.executeUpdate()
        }
    }

    @Synchronized
    fun addBalance(uuid: UUID, amount: Double) =
        setBalance(uuid, getBalance(uuid) + amount)

    @Synchronized
    fun removeBalance(uuid: UUID, amount: Double): Boolean {
        val current = getBalance(uuid)
        if (current < amount) return false
        setBalance(uuid, current - amount)
        return true
    }

    @Synchronized
    fun has(uuid: UUID, amount: Double) = getBalance(uuid) >= amount

    @Synchronized
    fun getTopBalances(page: Int, pageSize: Int): List<Pair<UUID, Double>> {
        val offset = (page - 1) * pageSize
        connection.prepareStatement(
            "SELECT uuid, balance FROM balances ORDER BY balance DESC LIMIT ? OFFSET ?"
        ).use { stmt ->
            stmt.setInt(1, pageSize)
            stmt.setInt(2, offset)
            stmt.executeQuery().use { rs ->
                val result = mutableListOf<Pair<UUID, Double>>()
                while (rs.next()) {
                    result += UUID.fromString(rs.getString("uuid")) to rs.getDouble("balance")
                }
                return result
            }
        }
    }

    @Synchronized
    fun getRank(uuid: UUID): Int {
        connection.prepareStatement(
            "SELECT COUNT(*) + 1 FROM balances WHERE balance > (SELECT COALESCE((SELECT balance FROM balances WHERE uuid = ?), 0))"
        ).use { stmt ->
            stmt.setString(1, uuid.toString())
            stmt.executeQuery().use { rs ->
                return if (rs.next()) rs.getInt(1) else 1
            }
        }
    }

    @Synchronized
    fun getTotalPlayerCount(): Int {
        connection.createStatement().use { stmt ->
            stmt.executeQuery("SELECT COUNT(*) FROM balances").use { rs ->
                return if (rs.next()) rs.getInt(1) else 0
            }
        }
    }

    fun format(amount: Double): String {
        val symbol = plugin.config.getString("currency-symbol") ?: "$"
        val decimals = plugin.config.getInt("decimal-places", 2)
        return "$symbol${"%,.${decimals}f".format(amount)}"
    }
}
