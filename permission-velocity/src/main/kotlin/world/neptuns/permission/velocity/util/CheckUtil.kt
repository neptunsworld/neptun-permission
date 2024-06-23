package world.neptuns.permission.velocity.util

import com.velocitypowered.api.proxy.Player
import world.neptuns.core.base.api.language.LineKey
import world.neptuns.core.base.api.player.extension.sendMessage
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.permission.PermissionBundle

object CheckUtil {

    suspend fun checkPermissionGroup(player: Player, groupName: String): PermissionGroup? {
        val permissionGroup = NeptunPermissionProvider.api.permissionGroupService.getPermissionGroup(groupName)
        if (permissionGroup == null) player.sendMessage(LineKey.key("permission.proxy.group.not_exist"))
        return permissionGroup
    }

    suspend fun checkPermissionBundle(player: Player, bundleName: String): PermissionBundle? {
        val permissionBundle = NeptunPermissionProvider.api.permissionBundleService.getPermissionBundle(bundleName)
        if (permissionBundle == null) player.sendMessage(LineKey.key("permission.proxy.bundle.not_exist"))
        return permissionBundle
    }

}