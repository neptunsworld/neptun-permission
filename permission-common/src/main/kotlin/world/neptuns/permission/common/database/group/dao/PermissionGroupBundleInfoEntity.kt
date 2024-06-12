package world.neptuns.permission.common.database.group.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.group.PermissionGroupBundleInfoTable

class PermissionGroupBundleInfoEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<PermissionGroupBundleInfoEntity>(PermissionGroupBundleInfoTable)

    var groupName by PermissionGroupBundleInfoTable.groupName
    var name by PermissionGroupBundleInfoTable.bundleName
}