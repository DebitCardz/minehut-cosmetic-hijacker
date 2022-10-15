plugins {
    kotlin("jvm") version "1.7.20"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.tech"
version = "0.0.1"

repositories {
    mavenCentral()

    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

bukkit {
    name = "mh-cosmetic-hijacker"
    description = "Bring back Resource Packs."
    authors = listOf("Tech")
    main = "me.tech.CosmeticHijacker"
    apiVersion = "1.17"
    softDepend = listOf("MinehutCosmetics")
    commands {
        register("spoofcosmetic")
    }
}