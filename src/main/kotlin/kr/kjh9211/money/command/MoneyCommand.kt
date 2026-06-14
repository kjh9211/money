package kr.kjh9211.money.command

import kr.kjh9211.money.MoneyPlugin
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MoneyCommand(private val plugin: MoneyPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val eco = plugin.economyManager

        if (args.isEmpty()) {
            val player = sender as? Player ?: run {
                sender.sendMessage("§cConsole must specify a player: /money <player>")
                return true
            }
            if (!player.hasPermission("money.check")) {
                player.sendMessage("§c잔액 확인 권한이 없습니다.")
                return true
            }
            player.sendMessage("§a잔액: §f${eco.format(eco.getBalance(player.uniqueId))}")
            return true
        }

        when (args[0].lowercase()) {
            "add" -> {
                if (!sender.hasPermission("money.add")) { sender.sendMessage("§c돈 추가 권한이 없습니다."); return true }
                if (args.size < 3) { sender.sendMessage("§c사용법: /money add <플레이어> <금액>"); return true }
                val target = resolveOfflinePlayer(args[1]) ?: run { sender.sendMessage("§c플레이어를 찾을 수 없습니다: ${args[1]}"); return true }
                val amount = parsePositiveDouble(sender, args[2]) ?: return true
                eco.addBalance(target.uniqueId, amount)
                sender.sendMessage("§a${target.name}에게 §f${eco.format(amount)} §a추가 → 잔액: §f${eco.format(eco.getBalance(target.uniqueId))}")
                target.player?.sendMessage("§a관리자가 §f${eco.format(amount)} §a을(를) 지급했습니다.")
            }
            "remove" -> {
                if (!sender.hasPermission("money.remove")) { sender.sendMessage("§c돈 차감 권한이 없습니다."); return true }
                if (args.size < 3) { sender.sendMessage("§c사용법: /money remove <플레이어> <금액>"); return true }
                val target = resolveOfflinePlayer(args[1]) ?: run { sender.sendMessage("§c플레이어를 찾을 수 없습니다: ${args[1]}"); return true }
                val amount = parsePositiveDouble(sender, args[2]) ?: return true
                if (!eco.removeBalance(target.uniqueId, amount)) {
                    sender.sendMessage("§c${target.name}의 잔액이 부족합니다. (보유: §f${eco.format(eco.getBalance(target.uniqueId))}§c)")
                    return true
                }
                sender.sendMessage("§a${target.name}에게서 §f${eco.format(amount)} §a차감 → 잔액: §f${eco.format(eco.getBalance(target.uniqueId))}")
                target.player?.sendMessage("§c관리자가 §f${eco.format(amount)} §c을(를) 차감했습니다.")
            }
            "set" -> {
                if (!sender.hasPermission("money.set")) { sender.sendMessage("§c잔액 설정 권한이 없습니다."); return true }
                if (args.size < 3) { sender.sendMessage("§c사용법: /money set <플레이어> <금액>"); return true }
                val target = resolveOfflinePlayer(args[1]) ?: run { sender.sendMessage("§c플레이어를 찾을 수 없습니다: ${args[1]}"); return true }
                val amount = parsePositiveDouble(sender, args[2]) ?: return true
                eco.setBalance(target.uniqueId, amount)
                sender.sendMessage("§a${target.name}의 잔액을 §f${eco.format(amount)} §a으로 설정했습니다.")
                target.player?.sendMessage("§a관리자가 잔액을 §f${eco.format(amount)} §a으로 설정했습니다.")
            }
            else -> {
                if (!sender.hasPermission("money.check.others")) {
                    sender.sendMessage("§c다른 플레이어 잔액 확인 권한이 없습니다.")
                    return true
                }
                val target = resolveOfflinePlayer(args[0]) ?: run { sender.sendMessage("§c플레이어를 찾을 수 없습니다: ${args[0]}"); return true }
                sender.sendMessage("§a${target.name}의 잔액: §f${eco.format(eco.getBalance(target.uniqueId))}")
            }
        }
        return true
    }

    @Suppress("DEPRECATION")
    private fun resolveOfflinePlayer(name: String): OfflinePlayer? {
        Bukkit.getPlayerExact(name)?.let { return it }
        val offline = Bukkit.getOfflinePlayer(name)
        return if (offline.hasPlayedBefore()) offline else null
    }

    private fun parsePositiveDouble(sender: CommandSender, raw: String): Double? {
        val value = raw.toDoubleOrNull() ?: run { sender.sendMessage("§c올바른 금액을 입력해주세요: $raw"); return null }
        if (value < 0) { sender.sendMessage("§c금액은 0 이상이어야 합니다."); return null }
        return value
    }
}
