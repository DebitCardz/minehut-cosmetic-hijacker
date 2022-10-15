package me.tech

import org.bukkit.configuration.file.FileConfiguration

data class HijackerConfig(
    val disableCosmetics: Boolean,
    val spoofCosmetics: Boolean,

    val pack: ResourcePack
) {
    data class ResourcePack(
        val enabled: Boolean,
        val url: String,
        val sha1: String,
        val required: Boolean
    )
}

fun loadHijackerConfig(conf: FileConfiguration): HijackerConfig {
    return HijackerConfig(
        conf.getBoolean("disable_cosmetics"),
        conf.getBoolean("spoof_cosmetics"),
        HijackerConfig.ResourcePack(
            conf.getBoolean("resource_pack.enabled"),
            conf.getString("resource_pack.url") ?: "Undefined",
            conf.getString("resource_pack.sha1") ?: "Undefined",
            conf.getBoolean("resource_pack.required")
        )
    )
}
