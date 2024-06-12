package world.neptuns.permission.common.database.group.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.group.PermissionGroupParentInfoTable

class PermissionGroupParentInfoEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PermissionGroupParentInfoEntity>(PermissionGroupParentInfoTable)

    var groupName by PermissionGroupParentInfoTable.groupName
    var parentName by PermissionGroupParentInfoTable.parentName
}