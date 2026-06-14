package kr.kjh9211.money.command

import kr.kjh9211.money.MoneyPlugin
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class WithdrawCommand(private val plugin: MoneyPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: run { sender.sendMessage("§c플레이어만 사용 가능합니다."); return true }

        if (!player.hasPermission("money.withdraw")) { player.sendMessage("§c출금 권한이 없습니다."); return true }
        if (args.isEmpty()) { player.sendMessage("§c사용법: /출금 <다이아 개수>"); return true }

        val amount = args[0].toIntOrNull()?.takeIf { it > 0 }
            ?: run { player.sendMessage("§c1 이상의 정수를 입력해주세요."); return true }

        val rate = plugin.config.getDouble("diamond-exchange-rate", 100.0)
        val cost = amount * rate
        val eco = plugin.economyManager

        if (!eco.has(player.uniqueId, cost)) {
            player.sendMessage("§c잔액이 부족합니다. 필요: §f${eco.format(cost)} §c| 보유: §f${eco.format(eco.getBalance(player.uniqueId))}")
            return true
        }
        if (!hasInventorySpace(player, amount)) {
            player.sendMessage("§c인벤토리 공간이 부족합니다. 다이아몬드 §f${amount}개§c를 받을 공간을 확보해주세요.")
            return true
        }

        eco.removeBalance(player.uniqueId, cost)

        // 남는 아이템은 발밑에 드롭 (공간 체크 후라서 거의 발생하지 않음)
        val leftover = player.inventory.addItem(ItemStack(Material.DIAMOND, amount))
        leftover.values.forEach { player.world.dropItemNaturally(player.location, it) }

        player.sendMessage("§a출금 완료! §f${eco.format(cost)} §a→ §f다이아몬드 ${amount}개")
        player.sendMessage("§a현재 잔액: §f${eco.format(eco.getBalance(player.uniqueId))}")
        return true
    }

    private fun hasInventorySpace(player: Player, amount: Int): Boolean {
        var freeSpace = 0
        for (item in player.inventory.storageContents) {
            freeSpace += when {
                item == null -> 64
                item.type == Material.DIAMOND && item.amount < 64 -> 64 - item.amount
                else -> 0
            }
            if (freeSpace >= amount) return true
        }
        return freeSpace >= amount
    }
}
