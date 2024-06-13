package world.neptuns.permission.common.database.group.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.group.PermissionGroupPermissionBundleInfoTable

class PermissionGroupPermissionBundleInfoEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<PermissionGroupPermissionBundleInfoEntity>(PermissionGroupPermissionBundleInfoTable)

    var groupName by PermissionGroupPermissionBundleInfoTable.groupName
    var name by PermissionGroupPermissionBundleInfoTable.bundleName
    var timeoutTimestamp by PermissionGroupPermissionBundleInfoTable.timeoutTimestamp
}