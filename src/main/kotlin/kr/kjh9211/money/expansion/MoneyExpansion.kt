package kr.kjh9211.money.expansion

import kr.kjh9211.money.MoneyPlugin
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class MoneyExpansion(private val plugin: MoneyPlugin) : PlaceholderExpansion() {

    override fun getIdentifier() = "money"
    override fun getAuthor() = "kjh9211"
    override fun getVersion(): String = plugin.description.version
    override fun persist() = true

    // %money_balance%       → 포맷된 잔액 (예: $1,000.00)
    // %money_balance_raw%   → 숫자만 (예: 1000.0)
    // %money_rank%          → 잔액 순위 (예: 3)
    override fun onRequest(player: OfflinePlayer?, identifier: String): String? {
        player ?: return ""
        val eco = plugin.economyManager
        return when (identifier) {
            "balance"     -> eco.format(eco.getBalance(player.uniqueId))
            "balance_raw" -> eco.getBalance(player.uniqueId).toString()
            "rank"        -> eco.getRank(player.uniqueId).toString()
            else          -> null
        }
    }
}
