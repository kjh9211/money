package kr.kjh9211.money.vault

import kr.kjh9211.money.MoneyPlugin
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import net.milkbowl.vault.economy.EconomyResponse.ResponseType
import org.bukkit.OfflinePlayer

class VaultEconomy(private val plugin: MoneyPlugin) : Economy {

    override fun isEnabled() = plugin.isEnabled

    override fun getName() = "Money"

    override fun hasBankSupport() = false

    override fun fractionalDigits() = plugin.config.getInt("decimal-places", 2)

    override fun format(amount: Double) = plugin.economyManager.format(amount)

    override fun currencyNamePlural(): String = plugin.config.getString("currency-name-plural") ?: "dollars"

    override fun currencyNameSingular(): String = plugin.config.getString("currency-name-singular") ?: "dollar"

    override fun hasAccount(player: OfflinePlayer) = true

    @Deprecated("Use OfflinePlayer variant")
    override fun hasAccount(playerName: String) = true

    override fun hasAccount(player: OfflinePlayer, worldName: String) = hasAccount(player)

    @Deprecated("Use OfflinePlayer variant")
    override fun hasAccount(playerName: String, worldName: String) = true

    override fun getBalance(player: OfflinePlayer) =
        plugin.economyManager.getBalance(player.uniqueId)

    @Deprecated("Use OfflinePlayer variant")
    override fun getBalance(playerName: String): Double {
        val player = plugin.server.getOfflinePlayer(playerName)
        return plugin.economyManager.getBalance(player.uniqueId)
    }

    override fun getBalance(player: OfflinePlayer, world: String) = getBalance(player)

    @Deprecated("Use OfflinePlayer variant")
    override fun getBalance(playerName: String, world: String) = getBalance(playerName)

    override fun has(player: OfflinePlayer, amount: Double) =
        plugin.economyManager.has(player.uniqueId, amount)

    @Deprecated("Use OfflinePlayer variant")
    override fun has(playerName: String, amount: Double): Boolean {
        val player = plugin.server.getOfflinePlayer(playerName)
        return plugin.economyManager.has(player.uniqueId, amount)
    }

    override fun has(player: OfflinePlayer, worldName: String, amount: Double) = has(player, amount)

    @Deprecated("Use OfflinePlayer variant")
    override fun has(playerName: String, worldName: String, amount: Double) = has(playerName, amount)

    override fun withdrawPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        if (amount < 0) return EconomyResponse(0.0, getBalance(player), ResponseType.FAILURE, "Cannot withdraw negative amount")
        return if (plugin.economyManager.removeBalance(player.uniqueId, amount)) {
            EconomyResponse(amount, getBalance(player), ResponseType.SUCCESS, null)
        } else {
            EconomyResponse(0.0, getBalance(player), ResponseType.FAILURE, "Insufficient funds")
        }
    }

    @Deprecated("Use OfflinePlayer variant")
    override fun withdrawPlayer(playerName: String, amount: Double): EconomyResponse {
        val player = plugin.server.getOfflinePlayer(playerName)
        return withdrawPlayer(player, amount)
    }

    override fun withdrawPlayer(player: OfflinePlayer, worldName: String, amount: Double) =
        withdrawPlayer(player, amount)

    @Deprecated("Use OfflinePlayer variant")
    override fun withdrawPlayer(playerName: String, worldName: String, amount: Double) =
        withdrawPlayer(playerName, amount)

    override fun depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        if (amount < 0) return EconomyResponse(0.0, getBalance(player), ResponseType.FAILURE, "Cannot deposit negative amount")
        plugin.economyManager.addBalance(player.uniqueId, amount)
        return EconomyResponse(amount, getBalance(player), ResponseType.SUCCESS, null)
    }

    @Deprecated("Use OfflinePlayer variant")
    override fun depositPlayer(playerName: String, amount: Double): EconomyResponse {
        val player = plugin.server.getOfflinePlayer(playerName)
        return depositPlayer(player, amount)
    }

    override fun depositPlayer(player: OfflinePlayer, worldName: String, amount: Double) =
        depositPlayer(player, amount)

    @Deprecated("Use OfflinePlayer variant")
    override fun depositPlayer(playerName: String, worldName: String, amount: Double) =
        depositPlayer(playerName, amount)

    override fun createPlayerAccount(player: OfflinePlayer) = true

    @Deprecated("Use OfflinePlayer variant")
    override fun createPlayerAccount(playerName: String) = true

    override fun createPlayerAccount(player: OfflinePlayer, worldName: String) = true

    @Deprecated("Use OfflinePlayer variant")
    override fun createPlayerAccount(playerName: String, worldName: String) = true

    // Bank methods - not supported
    private fun notSupported() = EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Bank support is not enabled")

    override fun createBank(name: String, player: OfflinePlayer) = notSupported()
    override fun createBank(name: String, playerName: String) = notSupported()
    override fun deleteBank(name: String) = notSupported()
    override fun bankBalance(name: String) = notSupported()
    override fun bankHas(name: String, amount: Double) = notSupported()
    override fun bankWithdraw(name: String, amount: Double) = notSupported()
    override fun bankDeposit(name: String, amount: Double) = notSupported()
    override fun isBankOwner(name: String, player: OfflinePlayer) = notSupported()
    override fun isBankOwner(name: String, playerName: String) = notSupported()
    override fun isBankMember(name: String, player: OfflinePlayer) = notSupported()
    override fun isBankMember(name: String, playerName: String) = notSupported()
    override fun getBanks(): MutableList<String> = mutableListOf()
}
