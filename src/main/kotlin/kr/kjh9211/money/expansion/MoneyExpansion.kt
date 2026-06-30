package kr.kjh9211.money.expansion

import kr.kjh9211.money.MoneyPlugin
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

class MoneyExpansion(private val plugin: MoneyPlugin) : PlaceholderExpansion() {

    override fun getIdentifier() = "money"
    override fun getAuthor() = "kjh9211"
    override fun getVersion(): String = plugin.description.version
    override fun persist() = true

    // %money_balance%                → 자신의 포맷된 잔액 (예: $1,000.00)
    // %money_balance_raw%            → 자신의 숫자 잔액 (예: 1000.0)
    // %money_rank%                   → 자신의 잔액 순위 (예: 3)
    // %money_balance_<player>%       → 지정 플레이어의 포맷된 잔액
    // %money_balance_raw_<player>%   → 지정 플레이어의 숫자 잔액
    override fun onRequest(player: OfflinePlayer?, identifier: String): String? {
        val eco = plugin.economyManager
        return when {
            identifier == "balance" -> {
                player ?: return ""
                eco.format(eco.getBalance(player.uniqueId))
            }
            identifier == "balance_raw" -> {
                player ?: return ""
                eco.getBalance(player.uniqueId).toString()
            }
            identifier == "rank" -> {
                player ?: return ""
                eco.getRank(player.uniqueId).toString()
            }
            identifier.startsWith("balance_raw_") -> {
                val target = Bukkit.getOfflinePlayer(identifier.removePrefix("balance_raw_"))
                eco.getBalance(target.uniqueId).toString()
            }
            identifier.startsWith("balance_") -> {
                val target = Bukkit.getOfflinePlayer(identifier.removePrefix("balance_"))
                eco.format(eco.getBalance(target.uniqueId))
            }
            else -> null
        }
    }
}
