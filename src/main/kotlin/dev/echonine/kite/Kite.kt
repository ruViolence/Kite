package dev.echonine.kite

import dev.echonine.kite.commands.KiteCommands
import dev.echonine.kite.scripting.ScriptManager
import dev.echonine.kite.scripting.cache.ImportsCache
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

@Suppress("unused")
class Kite : JavaPlugin() {

    internal lateinit var scriptManager: ScriptManager

    companion object {
        var INSTANCE: Kite? = null
            private set
        // Global instance of ImportsCache for ease of access.
        var IMPORTS_CACHE: ImportsCache? = null
    }

    object Structure {
        // This property includes a fallback to not throw in case it was accessed in non-server environment.
        // E.g., when Kite's running inside IDEA.
        val KITE_DIR: File
            get() = INSTANCE?.dataFolder ?: File(System.getProperty("user.dir"))
        val SCRIPTS_DIR: File
            get() = KITE_DIR.resolve("scripts")
        val CACHE_DIR: File
            get() = KITE_DIR.resolve("cache")
        val LIBS_DIR: File
            get() = KITE_DIR.resolve("libs")
    }

    object Environment {
        val IS_SERVER_AVAILABLE by lazy {
            try {
                return@lazy Class.forName("org.bukkit.Server") != null
            } catch (e: ClassNotFoundException) {
                return@lazy false
            }
        }
        // Bump this if cache is no longer compatible between releases.
        const val CACHE_VERSION = "3"
    }

    override fun onEnable() {
        INSTANCE = this
        // Initializing ImportsCache.
        IMPORTS_CACHE = ImportsCache()
        // Initializing ScriptManager and loading all scripts.
        this.scriptManager = ScriptManager(this)
        this.scriptManager.loadAll()
        // Registering command(s).
        this.server.commandMap.register("kite", KiteCommands(this))
    }

    override fun onDisable() {
    }

}