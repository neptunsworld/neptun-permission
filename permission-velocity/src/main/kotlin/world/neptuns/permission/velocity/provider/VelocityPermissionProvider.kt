package world.neptuns.permission.velocity.provider

import com.velocitypowered.api.permission.PermissionFunction
import com.velocitypowered.api.permission.PermissionProvider
import com.velocitypowered.api.permission.PermissionSubject
import com.velocitypowered.api.permission.Tristate
import com.velocitypowered.api.proxy.Player
import kotlinx.coroutines.runBlocking
import world.neptuns.permission.api.NeptunPermissionProvider

class VelocityPermissionProvider(private val player: Player) : PermissionProvider, PermissionFunction {

    override fun createFunction(subject: PermissionSubject): PermissionFunction {
        return this
    }

    override fun getPermissionValue(permission: String): Tristate = runBlocking {
        val permissionPlayer = NeptunPermissionProvider.api.permissionPlayerService.getPermissionPlayer(player.uniqueId)
        Tristate.fromBoolean(permissionPlayer?.hasPermission(permission) ?: false)
    }

}