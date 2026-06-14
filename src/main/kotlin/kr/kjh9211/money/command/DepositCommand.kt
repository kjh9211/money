package kr.kjh9211.money.command

import kr.kjh9211.money.MoneyPlugin
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class DepositCommand(private val plugin: MoneyPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: run { sender.sendMessage("§c플레이어만 사용 가능합니다."); return true }

        if (!player.hasPermission("money.deposit")) { player.sendMessage("§c입금 권한이 없습니다."); return true }
        if (args.isEmpty()) { player.sendMessage("§c사용법: /입금 <다이아 개수>"); return true }

        val amount = args[0].toIntOrNull()?.takeIf { it > 0 }
            ?: run { player.sendMessage("§c1 이상의 정수를 입력해주세요."); return true }

        val held = countDiamonds(player)
        if (held < amount) {
            player.sendMessage("§c다이아몬드가 부족합니다. 보유: §f${held}개")
            return true
        }

        val rate = plugin.config.getDouble("diamond-exchange-rate", 100.0)
        val earned = amount * rate
        val eco = plugin.economyManager

        removeDiamonds(player, amount)
        eco.addBalance(player.uniqueId, earned)

        player.sendMessage("§a입금 완료! §f다이아몬드 ${amount}개 §a→ §f${eco.format(earned)}")
        player.sendMessage("§a현재 잔액: §f${eco.format(eco.getBalance(player.uniqueId))}")
        return true
    }

    private fun countDiamonds(player: Player): Int =
        player.inventory.contents
            .filterNotNull()
            .filter { it.type == Material.DIAMOND }
            .sumOf { it.amount }

    private fun removeDiamonds(player: Player, amount: Int) {
        var remaining = amount
        val contents = player.inventory.contents
        for (i in contents.indices) {
            if (remaining <= 0) break
            val item = contents[i] ?: continue
            if (item.type != Material.DIAMOND) continue
            if (item.amount <= remaining) {
                remaining -= item.amount
                contents[i] = null
            } else {
                item.amount -= remaining
                remaining = 0
            }
        }
        player.inventory.contents = contents
    }
}
