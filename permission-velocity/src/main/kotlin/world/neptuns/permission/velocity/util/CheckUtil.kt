package world.neptuns.permission.velocity.util

import com.velocitypowered.api.proxy.Player
import world.neptuns.core.base.api.NeptunCoreProvider
import world.neptuns.core.base.api.language.LineKey
import world.neptuns.core.base.api.player.NeptunOfflinePlayer
import world.neptuns.core.base.api.player.extension.sendMessage
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.player.PermissionPlayer

object CheckUtil {

    suspend fun checkPermissionBundle(player: Player, bundleName: String): PermissionBundle? {
        val permissionBundle = NeptunPermissionProvider.api.permissionBundleService.getPermissionBundle(bundleName)

        if (permissionBundle == null) {
            player.sendMessage(LineKey.key("permission.proxy.bundle.not_exist"))
            return null
        }

        return permissionBundle
    }

    suspend fun checkPermissionGroup(player: Player, groupName: String): PermissionGroup? {
        val permissionGroup = NeptunPermissionProvider.api.permissionGroupService.getPermissionGroup(groupName)

        if (permissionGroup == null) {
            player.sendMessage(LineKey.key("permission.proxy.group.not_exist"))
            return null
        }

        return permissionGroup
    }

    suspend fun checkPermissionPlayer(player: Player, playerName: String): Pair<NeptunOfflinePlayer, PermissionPlayer>? {
        val offlinePlayer = NeptunCoreProvider.api.playerService.getOfflinePlayerAsync(playerName).await()

        if (offlinePlayer == null) {
            player.sendMessage(LineKey.key("permission.proxy.player.not_exist"))
            return null
        }

        val permissionPlayer = NeptunPermissionProvider.api.permissionPlayerService.getPermissionPlayer(offlinePlayer.uuid)

        if (permissionPlayer == null) {
            player.sendMessage(LineKey.key("permission.proxy.player.not_exist"))
            return null
        }

        return offlinePlayer to permissionPlayer
    }

}