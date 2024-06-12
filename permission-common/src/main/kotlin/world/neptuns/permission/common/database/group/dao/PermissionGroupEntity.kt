package world.neptuns.permission.common.database.group.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.group.PermissionGroupBundleInfoTable
import world.neptuns.permission.common.database.group.PermissionGroupParentInfoTable
import world.neptuns.permission.common.database.group.PermissionGroupPermissionInfoTable
import world.neptuns.permission.common.database.group.PermissionGroupTable

class PermissionGroupEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PermissionGroupEntity>(PermissionGroupTable)

    var name by PermissionGroupTable.name
    var priority by PermissionGroupTable.priority
    var color by PermissionGroupTable.color
    var format by PermissionGroupTable.format
    var isDefault by PermissionGroupTable.isDefault

    val permissions by PermissionGroupPermissionInfoEntity referrersOn PermissionGroupPermissionInfoTable.groupName
    val permissionBundles by PermissionGroupBundleInfoEntity referrersOn PermissionGroupBundleInfoTable.groupName
    val parents by PermissionGroupParentInfoEntity referrersOn PermissionGroupParentInfoTable.groupName

}