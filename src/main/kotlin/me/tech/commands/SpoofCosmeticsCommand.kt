package me.tech.commands

import com.google.common.cache.Cache
import me.tech.getProfileFile
import me.tech.setCosmetic
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

class SpoofCosmeticsCommand(
    private val mhCosmeticPlugin: Plugin
) : CommandExecutor, TabCompleter {
    private val validTypes = listOf("particle", "companion", "balloon", "hat", "wing", "trinket")

    private val cosmeticManagerInst
        get() = mhCosmeticPlugin.javaClass
            .getDeclaredField("manager").apply { isAccessible = true }
            .get(mhCosmeticPlugin)

    private val dataFolder
        get() = mhCosmeticPlugin.dataFolder

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        if(sender !is Player) {
            sender.sendMessage("Only players can execute this command.")
            return true
        }

        if(args == null || args.size != 2) {
            sender.sendMessage(Component.text("Usage: /spoofcosmetic <${validTypes.joinToString("/")}> <id>.", NamedTextColor.RED))
            return true
        }

        val type = args[0].lowercase()
        val cosmeticId = args[1].uppercase()

        if(validTypes.contains(type)) {
            setEquippedInTheAmazingLocalStorageThing(sender, type, cosmeticId)
            sender.sendMessage(Component.text("Applied $type $cosmeticId.", NamedTextColor.AQUA))
        } else {
            sender.sendMessage(Component.text("Invalid type $type.", NamedTextColor.RED))
        }

        return true
    }

    private fun setEquippedInTheAmazingLocalStorageThing(player: Player, type: String, id: String) {
        setCosmetic(
            getProfileFile(dataFolder, player),
            type,
            id
        )

        invalidateAndReloadCurrentCache(player)
    }

    private fun invalidateAndReloadCurrentCache(player: Player) {
        // unequip their stuff
        cosmeticManagerInst.javaClass
            .getMethod("unEquipAll", UUID::class.java)
            .invoke(cosmeticManagerInst, player.uniqueId)

        // profile res cache
        val epicCache1 = cosmeticManagerInst.javaClass
            .getDeclaredField("cosmeticsCache").apply { isAccessible = true }
            .get(cosmeticManagerInst) as MutableMap<UUID, Any>
        epicCache1.remove(player.uniqueId)

        // actual cosmetic cache thingy
        val epicCache2 = cosmeticManagerInst.javaClass
            .getDeclaredField("cache").apply { isAccessible = true }
            .get(cosmeticManagerInst) as Cache<UUID, Any>
        epicCache2.invalidate(player.uniqueId)

        // 'connect' the player
        cosmeticManagerInst.javaClass
            .getMethod("handleConnect", UUID::class.java)
            .invoke(cosmeticManagerInst, player.uniqueId)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String> {
        if(args == null) {
            return mutableListOf()
        }

        var returnList: MutableList<String> = mutableListOf()

        if(args.size == 1) {
            returnList = mutableListOf("balloon", "companion", "hat", "item", "particle", "wing", "trinket")
        }

        if(args.size == 2) {
            returnList = when(args[0].lowercase()) {
                "particle" -> PARTICLE_IDS.map { it.name }
                "companion" -> COMPANION_IDS.map { it.name }
                "balloon" -> BALLOON_IDS.map { it.name }
                "hat" -> HAT_IDS.map { it.name }
                "wing" -> WING_IDS.map { it.name }
                "trinket" -> TRINKET_IDS.map { it.name }
                else -> listOf()
            }.toMutableList()
        }

        val arg = args.lastOrNull()
        return if(arg != null && arg.isNotBlank()) {
            returnList.filter { it.startsWith(arg, true) }.toMutableList()
        } else {
            returnList
        }
    }

    companion object {
        private const val BASE_PACKAGE = "com.minehut.cosmetics"

        private val PARTICLE_IDS by lazyOf(yoinkEnumValues("${BASE_PACKAGE}.cosmetics.groups.particle.Particle"))
        private val COMPANION_IDS by lazyOf(yoinkEnumValues("${BASE_PACKAGE}.cosmetics.groups.companion.Companion"))
        private val BALLOON_IDS by lazyOf(yoinkEnumValues("${BASE_PACKAGE}.cosmetics.groups.balloon.Balloon"))
        private val HAT_IDS by lazyOf(yoinkEnumValues("${BASE_PACKAGE}.cosmetics.groups.hat.Hat"))
        private val WING_IDS by lazyOf(yoinkEnumValues("${BASE_PACKAGE}.cosmetics.groups.wing.Wing"))
        private val TRINKET_IDS by lazyOf(yoinkEnumValues("${BASE_PACKAGE}.cosmetics.groups.trinket.Trinket"))
    }
}

private fun <T : Enum<*>> yoinkEnumValues(packageName: String): Array<T> {
    return Class.forName(packageName)
        .getDeclaredField("\$VALUES").apply { isAccessible = true }
        .get(null) as Array<T>
}