package world.neptuns.permission.common.database.group.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.group.PermissionGroupMetadataTable

class PermissionGroupMetadataEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PermissionGroupMetadataEntity>(PermissionGroupMetadataTable)

    var groupName by PermissionGroupMetadataTable.groupName
    var key by PermissionGroupMetadataTable.key
    var value by PermissionGroupMetadataTable.value
}