package world.neptuns.permission.common.database.group.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.group.PermissionGroupPermissionInfoTable

class PermissionGroupPermissionInfoEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PermissionGroupPermissionInfoEntity>(PermissionGroupPermissionInfoTable)

    var groupName by PermissionGroupPermissionInfoTable.groupName
    var permission by PermissionGroupPermissionInfoTable.permission
    var timeoutTimestamp by PermissionGroupPermissionInfoTable.timeoutTimestamp
}