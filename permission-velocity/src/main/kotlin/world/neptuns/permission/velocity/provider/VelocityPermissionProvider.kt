package world.neptuns.permission.velocity.provider

import com.velocitypowered.api.permission.PermissionFunction
import com.velocitypowered.api.permission.PermissionProvider
import com.velocitypowered.api.permission.PermissionSubject
import com.velocitypowered.api.permission.Tristate
import com.velocitypowered.api.proxy.Player
import kotlinx.coroutines.runBlocking
import world.neptuns.permission.api.NeptunPermissionProvider

class VelocityPermissionProvider : PermissionProvider {

    override fun createFunction(subject: PermissionSubject): PermissionFunction {
        if (subject !is Player) return PermissionFunction { null }
        return VelocityPermissionFunction(subject)
    }

    private data class VelocityPermissionFunction(val player: Player) : PermissionFunction {
        override fun getPermissionValue(permission: String): Tristate = runBlocking {
            val permissionPlayer = NeptunPermissionProvider.api.permissionPlayerService.getPermissionPlayer(player.uniqueId)
            Tristate.fromBoolean(permissionPlayer?.hasPermission(permission) ?: false)
        }
    }

}