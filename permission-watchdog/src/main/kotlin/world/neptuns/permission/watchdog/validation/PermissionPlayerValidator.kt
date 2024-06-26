package world.neptuns.permission.watchdog.validation

import world.neptuns.permission.api.player.PermissionPlayer
import world.neptuns.permission.api.player.PermissionPlayerAdapter
import world.neptuns.permission.api.player.PermissionPlayerService

class PermissionPlayerValidator(private val permissionPlayerService: PermissionPlayerService, private val permissionPlayerAdapter: PermissionPlayerAdapter) {

    suspend fun validatePermissionPlayers() {
        for (permissionPlayer in this.permissionPlayerService.getPermissionPlayers().await()) {
            checkPlayersForExpiredGroups(permissionPlayer)
            checkPlayersForExpiredPrivatePermissions(permissionPlayer)
            checkPermsForExpiredPrivateBundles(permissionPlayer)
        }
    }

    private suspend fun checkPlayersForExpiredGroups(permissionPlayer: PermissionPlayer) {
        val expiredGroups = permissionPlayer.groups.filter { it.isExpired() }
        if (expiredGroups.isEmpty()) return

        this.permissionPlayerAdapter.removePermissionGroups(permissionPlayer.uuid, expiredGroups.map { it.name })
    }

    private suspend fun checkPermsForExpiredPrivateBundles(permissionPlayer: PermissionPlayer) {
        val expiredPrivatePermissionBundles = permissionPlayer.privatePermissionBundles.filter { it.isExpired() }
        if (expiredPrivatePermissionBundles.isEmpty()) return

        this.permissionPlayerAdapter.removePermissionBundles(permissionPlayer.uuid, expiredPrivatePermissionBundles.map { it.name })
    }

    private suspend fun checkPlayersForExpiredPrivatePermissions(permissionPlayer: PermissionPlayer) {
        val expiredPrivatePermissions = permissionPlayer.privatePermissions.filter { it.isExpired() }
        if (expiredPrivatePermissions.isEmpty()) return

        this.permissionPlayerAdapter.removePrivatePermissions(permissionPlayer.uuid, expiredPrivatePermissions.map { it.name })
    }

}