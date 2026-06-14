package kr.kjh9211.money.command

import kr.kjh9211.money.MoneyPlugin
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.util.UUID
import kotlin.math.ceil

class BalanceTopCommand(private val plugin: MoneyPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("money.balancetop")) {
            sender.sendMessage("§c잔액 순위 확인 권한이 없습니다.")
            return true
        }

        val page = args.getOrNull(0)?.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val eco = plugin.economyManager
        val pageSize = plugin.config.getInt("baltop-page-size", 10)
        val total = eco.getTotalPlayerCount()
        val totalPages = maxOf(1, ceil(total.toDouble() / pageSize).toInt())

        if (page > totalPages) {
            sender.sendMessage("§c페이지 §f$page§c 는 존재하지 않습니다. (총 §f$totalPages§c 페이지)")
            return true
        }

        val entries = eco.getTopBalances(page, pageSize)
        val startRank = (page - 1) * pageSize + 1

        sender.sendMessage("§6--- 잔액 순위 (${page}/${totalPages} 페이지) ---")
        if (entries.isEmpty()) {
            sender.sendMessage("§7아직 기록된 잔액이 없습니다.")
            return true
        }
        entries.forEachIndexed { index, (uuid, balance) ->
            val name = getPlayerName(uuid)
            sender.sendMessage("§e${startRank + index}. §f$name §7- §a${eco.format(balance)}")
        }
        return true
    }

    @Suppress("DEPRECATION")
    private fun getPlayerName(uuid: UUID): String =
        Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
}
