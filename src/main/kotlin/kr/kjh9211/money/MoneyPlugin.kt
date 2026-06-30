package kr.kjh9211.money

import kr.kjh9211.money.command.*
import kr.kjh9211.money.expansion.MoneyExpansion
import kr.kjh9211.money.manager.EconomyManager
import kr.kjh9211.money.vault.VaultEconomy
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

class MoneyPlugin : JavaPlugin() {

    lateinit var economyManager: EconomyManager
        private set

    override fun onEnable() {
        saveDefaultConfig()

        economyManager = EconomyManager(this)
        economyManager.load()

        getCommand("money")?.setExecutor(MoneyCommand(this))
        getCommand("pay")?.setExecutor(PayCommand(this))
        getCommand("balancetop")?.setExecutor(BalanceTopCommand(this))
        getCommand("deposit")?.setExecutor(DepositCommand(this))
        getCommand("withdraw")?.setExecutor(WithdrawCommand(this))
        getCommand("moneyreload")?.setExecutor(ReloadCommand(this))

        if (server.pluginManager.getPlugin("PlaceholderAPI") != null) {
            MoneyExpansion(this).register()
            logger.info("PlaceholderAPI 연동 완료.")
        }

        if (server.pluginManager.getPlugin("Vault") != null) {
            server.servicesManager.register(Economy::class.java, VaultEconomy(this), this, ServicePriority.Highest)
            logger.info("Vault 연동 완료.")
        }

        logger.info("Money plugin enabled.")
    }

    override fun onDisable() {
        if (::economyManager.isInitialized) {
            economyManager.close()
        }
        logger.info("Money plugin disabled.")
    }
}
