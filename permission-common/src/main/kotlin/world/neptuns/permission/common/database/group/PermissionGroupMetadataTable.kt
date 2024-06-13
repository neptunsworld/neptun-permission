package world.neptuns.permission.common.database.group

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object PermissionGroupMetadataTable : IntIdTable("permission_group_metadata") {
    val groupName = varchar("group_name", 20).references(PermissionGroupTable.name, onDelete = ReferenceOption.CASCADE)
    val key = varchar("key", 30).uniqueIndex()
    val value = text("value")
}