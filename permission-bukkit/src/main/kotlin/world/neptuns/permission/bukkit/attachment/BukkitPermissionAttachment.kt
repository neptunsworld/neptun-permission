package world.neptuns.permission.bukkit.attachment

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import world.neptuns.permission.api.player.PermissionPlayer

class BukkitPermissionAttachment(private val player: Player, private val permissionPlayer: PermissionPlayer, private val plugin: JavaPlugin) {

    private val attachment = this.player.addAttachment(this.plugin)

    suspend fun loadPermissions() {
        for (permissionInfo in this.permissionPlayer.collectAllPermissions()) {
            if (this.attachment.permissions.containsKey(permissionInfo.name)) continue
            this.attachment.setPermission(permissionInfo.name, true)
        }

        this.player.updateCommands()
    }

    fun unloadPermissions() {
        this.attachment.permissions.keys.forEach(this.attachment::unsetPermission)
        this.player.updateCommands()
    }

    fun reloadPermissions() {

    }

}