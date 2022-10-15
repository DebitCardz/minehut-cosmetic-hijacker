package me.tech.listeners

import me.tech.CosmeticHijacker
import me.tech.HijackerConfig
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ResourcePackListener(
    private val plugin: CosmeticHijacker
): Listener {
    private val hijackerConf: HijackerConfig
        get() = plugin.hijackerConf

    @EventHandler
    fun onPlayerJoin(ev: PlayerJoinEvent) {
        Bukkit.getScheduler().runTaskLater(
            plugin,
            Runnable {
                ev.player.setResourcePack(
                    hijackerConf.pack.url,
                    hijackerConf.pack.sha1,
                    hijackerConf.pack.required
                )
            },
            10L
        )
    }
}