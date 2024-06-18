package world.neptuns.permission.bukkit.util

import org.bukkit.Bukkit
import java.util.regex.Pattern

object CraftBukkitImplementation {

    private val SERVER_PACKAGE_VERSION: String

    init {
        val server = Bukkit.getServer().javaClass
        val matcher = Pattern.compile("^org\\.bukkit\\.craftbukkit\\.(\\w+)\\.CraftServer$").matcher(server.name)
        SERVER_PACKAGE_VERSION = if (matcher.matches()) {
            ".${matcher.group(1)}."
        } else {
            "."
        }
    }

    fun obc(className: String): String {
        return "org.bukkit.craftbukkit$SERVER_PACKAGE_VERSION$className"
    }

    @Throws(ClassNotFoundException::class)
    fun obcClass(className: String): Class<*> {
        return Class.forName(obc(className))
    }

}