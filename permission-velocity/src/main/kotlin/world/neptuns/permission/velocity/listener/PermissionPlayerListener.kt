package world.neptuns.permission.velocity.listener

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.permission.PermissionsSetupEvent
import com.velocitypowered.api.proxy.Player
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.player.PermissionPlayer
import world.neptuns.permission.velocity.provider.VelocityPermissionProvider

class PermissionPlayerListener {

    private val permissionPlayerService = NeptunPermissionProvider.api.permissionPlayerService

    @Subscribe(order = PostOrder.FIRST)
    suspend fun onPermissionSetup(event: PermissionsSetupEvent) {
        val subject = event.subject
        if (subject !is Player) return

        val permissionPlayer: PermissionPlayer? = this.permissionPlayerService.loadPermissionPlayer(subject.uniqueId).await()

        if (permissionPlayer == null)
            this.permissionPlayerService.createPermissionPlayer(subject.uniqueId)

        event.provider = VelocityPermissionProvider(subject)
    }

    @Subscribe(order = PostOrder.LAST)
    suspend fun onDisconnect(event: DisconnectEvent) {
        val player = event.player

        this.permissionPlayerService.unloadPermissionPlayer(player.uniqueId)
    }

}