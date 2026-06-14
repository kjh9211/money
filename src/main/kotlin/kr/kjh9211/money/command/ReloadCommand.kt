package kr.kjh9211.money.command

import kr.kjh9211.money.MoneyPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ReloadCommand(private val plugin: MoneyPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("money.reload")) {
            sender.sendMessage("§c리로드 권한이 없습니다.")
            return true
        }
        plugin.reloadConfig()
        sender.sendMessage("§aMoney 플러그인 설정을 리로드했습니다.")
        return true
    }
}
