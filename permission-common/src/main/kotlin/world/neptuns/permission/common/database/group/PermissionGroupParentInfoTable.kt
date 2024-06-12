package world.neptuns.permission.common.database.group

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object PermissionGroupParentInfoTable : IntIdTable("permission_group_parent_infos") {
    val groupName = varchar("group_name", 20).references(PermissionGroupTable.name, onDelete = ReferenceOption.CASCADE)
    val parentName = varchar("parent_name", 20).references(PermissionGroupTable.name, onDelete = ReferenceOption.CASCADE)
}