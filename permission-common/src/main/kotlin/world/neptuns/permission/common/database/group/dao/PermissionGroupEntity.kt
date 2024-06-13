package world.neptuns.permission.common.database.group.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.group.*

class PermissionGroupEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PermissionGroupEntity>(PermissionGroupTable)

    var name by PermissionGroupTable.name
    var priority by PermissionGroupTable.priority
    var color by PermissionGroupTable.color
    var format by PermissionGroupTable.format
    var isDefault by PermissionGroupTable.isDefault

    val permissions by PermissionGroupPermissionInfoEntity referrersOn PermissionGroupPermissionInfoTable.groupName
    val permissionBundles by PermissionGroupPermissionBundleInfoEntity referrersOn PermissionGroupPermissionBundleInfoTable.groupName
    val parents by PermissionGroupParentInfoEntity referrersOn PermissionGroupParentInfoTable.groupName
    val metadata by PermissionGroupMetadataEntity referrersOn PermissionGroupMetadataTable.groupName

}