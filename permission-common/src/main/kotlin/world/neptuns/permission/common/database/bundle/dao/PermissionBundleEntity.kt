package world.neptuns.permission.common.database.bundle.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.bundle.PermissionBundleTable

class PermissionBundleEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PermissionBundleEntity>(PermissionBundleTable)

    var name by PermissionBundleTable.name
    var isAuthRequired by PermissionBundleTable.isAuthRequired
    var timeoutTimestamp by PermissionBundleTable.timeoutTimestamp
    val permissions by PermissionBundlePermissionInfoEntity referrersOn PermissionBundleTable.name
}