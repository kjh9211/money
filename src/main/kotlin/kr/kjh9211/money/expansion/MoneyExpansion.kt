package kr.kjh9211.money.expansion

import kr.kjh9211.money.MoneyPlugin
import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class MoneyExpansion(private val plugin: MoneyPlugin) : PlaceholderExpansion() {

    override fun getIdentifier() = "money"
    override fun getAuthor() = "kjh9211"
    override fun getVersion(): String = plugin.description.version
    override fun persist() = true

    // %money_balance%                        → 자신의 포맷된 잔액 (예: $1,000.00)
    // %money_balance_raw%                    → 자신의 숫자 잔액 (예: 1000.0)
    // %money_rank%                           → 자신의 잔액 순위 (예: 3)
    // %money_balance_<player>%               → 지정 플레이어의 포맷된 잔액
    // %money_balance_raw_<player>%           → 지정 플레이어의 숫자 잔액
    // %money_balance_{papi_identifier}%      → PAPI로 해석된 플레이어명의 포맷된 잔액
    // %money_balance_raw_{papi_identifier}%  → PAPI로 해석된 플레이어명의 숫자 잔액
    // 예) %money_balance_{player_name}%  ← player_name은 PAPI Player 익스팬션 필요
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
                val name = resolveName(identifier.removePrefix("balance_raw_"), player) ?: return ""
                eco.getBalance(Bukkit.getOfflinePlayer(name).uniqueId).toString()
            }
            identifier.startsWith("balance_") -> {
                val name = resolveName(identifier.removePrefix("balance_"), player) ?: return ""
                eco.format(eco.getBalance(Bukkit.getOfflinePlayer(name).uniqueId))
            }
            else -> null
        }
    }

    // {identifier} 형식이면 PAPI로 해석하여 플레이어명을 동적으로 반환, 아니면 리터럴 반환
    // PAPI가 해석하지 못해 %가 남아 있으면 null 반환 → Mojang API 잘못된 요청 방지
    private fun resolveName(raw: String, viewer: OfflinePlayer?): String? {
        if (raw.startsWith("{") && raw.endsWith("}")) {
            val inner = raw.removeSurrounding("{", "}")
            val resolved = PlaceholderAPI.setPlaceholders(viewer as? Player, "%$inner%")
            if (resolved.contains('%')) return null
            return resolved
        }
        return raw
    }
}
