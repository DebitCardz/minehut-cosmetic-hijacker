package me.tech

import me.tech.commands.SpoofCosmeticsCommand
import me.tech.listeners.ResourcePackListener
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

const val COSMETIC_PLUGIN_NAME = "MinehutCosmetics"

class CosmeticHijacker : JavaPlugin() {
    lateinit var hijackerConf: HijackerConfig
        private set

    override fun onEnable() {
        saveDefaultConfig()
        hijackerConf = loadHijackerConfig(config)

        val pm = server.pluginManager

        val mhCosmeticPlugin = pm.getPlugin(COSMETIC_PLUGIN_NAME)

        if(hijackerConf.disableCosmetics) {
            if(mhCosmeticPlugin != null) {
                server.commandMap.knownCommands
                    .filter {
                        it.value.isRegistered
                        && it.value.label.equals(mhCosmeticPlugin.name, true)
                    }
                    .forEach { (_, cmd) -> cmd.unregister(server.commandMap) }

                // Doesn't work?
                pm.plugins.toMutableSet().remove(mhCosmeticPlugin)
                pm.disablePlugin(mhCosmeticPlugin)
            }
        } else {
            if(hijackerConf.spoofCosmetics && mhCosmeticPlugin != null) {
                val cmdInst = SpoofCosmeticsCommand(mhCosmeticPlugin)
                getCommand("spoofcosmetic")?.setExecutor(cmdInst)
                getCommand("spoofcosmetic")?.tabCompleter = cmdInst
            }
        }

        // Custom resource pack enabled.
        if(hijackerConf.pack.enabled) {
            // Cosmetics are enabled but we need our own resource pack thing
            // bye, bye minehut :)
            if(!hijackerConf.disableCosmetics && mhCosmeticPlugin != null) {
                logger.info("Disabled Minehut's ResourcePackListener.")
                HandlerList.getRegisteredListeners(mhCosmeticPlugin)
                    .firstOrNull { it.listener.javaClass.simpleName.equals("ResourcePackListener", true) }
                    ?.run { HandlerList.unregisterAll(this.listener) }
            }

            pm.registerEvents(ResourcePackListener(this), this)
        }
    }
}