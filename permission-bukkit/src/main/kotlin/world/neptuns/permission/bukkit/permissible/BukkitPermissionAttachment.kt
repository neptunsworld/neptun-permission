package world.neptuns.permission.bukkit.permissible

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import world.neptuns.permission.api.player.PermissionPlayer

data class BukkitPermissionAttachment(val plugin: JavaPlugin, val player: Player, val permissionPlayer: PermissionPlayer) {

    private val permissionAttachment = this.player.addAttachment(this.plugin)

    suspend fun loadPermissions() {
        for (permission in this.permissionPlayer.collectAllPermissions()) {
            if (this.permissionAttachment.permissions.containsKey(permission.name)) continue
            if (permission.isExpired()) continue

            this.permissionAttachment.setPermission(permission.name, true)
        }

        this.player.updateCommands()
    }

    fun unloadPermissions() {
        this.permissionAttachment.permissions.keys.forEach(this.permissionAttachment::unsetPermission)
        this.player.updateCommands()
    }

    suspend fun reloadPermissions() {
        unloadPermissions()
        loadPermissions()
        println("RELOADED PERMISSION!")
    }

}