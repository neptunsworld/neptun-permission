package world.neptuns.permission.common.database.player.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.player.PermissionPlayerPrivatePermissionBundleInfoTable

class PermissionPlayerPrivatePermissionBundleInfoEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PermissionPlayerPrivatePermissionBundleInfoEntity>(PermissionPlayerPrivatePermissionBundleInfoTable)

    var uuid by PermissionPlayerPrivatePermissionBundleInfoTable.uuid
    var name by PermissionPlayerPrivatePermissionBundleInfoTable.bundleName
    var timeoutTimestamp by PermissionPlayerPrivatePermissionBundleInfoTable.timeoutTimestamp
}