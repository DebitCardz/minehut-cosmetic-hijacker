package me.tech

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption

fun getProfileFile(
    dataFolder: File,
    player: Player
): File {
    val f = File(dataFolder, "profiles/${player.uniqueId}.yml")
    if(!f.exists()) {
        f.createNewFile()
    }

    return f
}

fun setCosmetic(
    profileFile: File,
    type: String,
    id: String
) {
    val conf = YamlConfiguration.loadConfiguration(profileFile)

    val localCosmeticProfile = conf.get("profile") ?: return

    val equipped = localCosmeticProfile.javaClass
        .getDeclaredField("equipped").apply { isAccessible = true }
        .get(localCosmeticProfile) as MutableMap<String, Any>

    equipped[type.uppercase()] = id.uppercase()

    localCosmeticProfile.javaClass
        .getDeclaredField("equipped").apply { isAccessible = true }
        .set(localCosmeticProfile, equipped)

    val serialized = localCosmeticProfile.javaClass
        .getDeclaredMethod("serialize")
        .invoke(localCosmeticProfile) as Map<*, *>

    conf.set("profile", serialized)
    conf.save(profileFile)

    // why.
    Files.write(profileFile.toPath(), "  ==: CosmeticProfile".toByteArray(), StandardOpenOption.APPEND)
}