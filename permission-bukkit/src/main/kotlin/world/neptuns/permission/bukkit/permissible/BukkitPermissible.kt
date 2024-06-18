package world.neptuns.permission.bukkit.permissible

import org.bukkit.entity.Player
import org.bukkit.permissions.PermissibleBase
import org.bukkit.permissions.Permission
import world.neptuns.permission.api.player.PermissionPlayer
import world.neptuns.permission.bukkit.NeptunBukkitPlugin
import java.util.concurrent.atomic.AtomicBoolean

class BukkitPermissible(player: Player, private val permissionPlayer: PermissionPlayer, private val plugin: NeptunBukkitPlugin) : PermissibleBase(player) {

    var oldPermissible: PermissibleBase? = null
    val active: AtomicBoolean = AtomicBoolean(false)

    override fun isPermissionSet(permission: String): Boolean {
        return permissionPlayer.hasPermission(permission);
    }

    override fun isPermissionSet(permission: Permission): Boolean {
        return permissionPlayer.hasPermission(permission.name);
    }

    override fun hasPermission(permission: String): Boolean {
        return permissionPlayer.hasPermission(permission);
    }

    override fun hasPermission(permission: Permission): Boolean {
        return permissionPlayer.hasPermission(permission.name);
    }

    override fun isOp(): Boolean {
        return permissionPlayer.hasPermission("*")
    }

}