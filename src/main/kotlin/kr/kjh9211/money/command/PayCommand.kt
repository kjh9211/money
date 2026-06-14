package kr.kjh9211.money.command

import kr.kjh9211.money.MoneyPlugin
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PayCommand(private val plugin: MoneyPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val payer = sender as? Player ?: run { sender.sendMessage("§c플레이어만 사용 가능합니다."); return true }

        if (!payer.hasPermission("money.pay")) { payer.sendMessage("§c송금 권한이 없습니다."); return true }
        if (args.size < 2) { payer.sendMessage("§c사용법: /pay <플레이어> <금액>"); return true }

        val target = Bukkit.getPlayerExact(args[0])
        if (target == null || !target.isOnline) {
            payer.sendMessage("§c§f${args[0]}§c 플레이어가 온라인 상태가 아닙니다.")
            return true
        }
        if (target == payer) { payer.sendMessage("§c자기 자신에게 송금할 수 없습니다."); return true }

        val amount = args[1].toDoubleOrNull() ?: run { payer.sendMessage("§c올바른 금액을 입력해주세요."); return true }

        val eco = plugin.economyManager
        val minPay = plugin.config.getDouble("min-pay-amount", 0.01)
        if (amount < minPay) {
            payer.sendMessage("§c최소 송금 금액은 §f${eco.format(minPay)} §c입니다.")
            return true
        }
        val maxPay = plugin.config.getDouble("max-pay-amount", 0.0)
        if (maxPay > 0 && amount > maxPay) {
            payer.sendMessage("§c최대 송금 금액은 §f${eco.format(maxPay)} §c입니다.")
            return true
        }
        if (!eco.has(payer.uniqueId, amount)) {
            payer.sendMessage("§c잔액이 부족합니다. 현재 잔액: §f${eco.format(eco.getBalance(payer.uniqueId))}")
            return true
        }

        eco.removeBalance(payer.uniqueId, amount)
        eco.addBalance(target.uniqueId, amount)

        payer.sendMessage("§a§f${target.name}§a에게 §f${eco.format(amount)} §a송금 완료. 잔액: §f${eco.format(eco.getBalance(payer.uniqueId))}")
        target.sendMessage("§a§f${payer.name}§a에게서 §f${eco.format(amount)} §a수령. 잔액: §f${eco.format(eco.getBalance(target.uniqueId))}")

        return true
    }
}
