package world.neptuns.permission.common.database.player.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.player.PermissionPlayerGroupInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerPrivatePermissionBundleInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerPrivatePermissionInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerTable

class PermissionPlayerEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PermissionPlayerEntity>(PermissionPlayerTable)

    var uuid by PermissionPlayerTable.uuid
    var authSession by PermissionPlayerTable.authSession

    val groups by PermissionPlayerGroupInfoEntity referrersOn PermissionPlayerGroupInfoTable.uuid

    val privatePermissionBundles by PermissionPlayerPrivatePermissionBundleInfoEntity referrersOn PermissionPlayerPrivatePermissionBundleInfoTable.uuid
    val privatePermissions by PermissionPlayerPrivatePermissionInfoEntity referrersOn PermissionPlayerPrivatePermissionInfoTable.uuid
}