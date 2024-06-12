package world.neptuns.permission.common.database.group

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object PermissionGroupPermissionInfoTable : IntIdTable("permission_group_permission_infos") {
    val groupName = varchar("group_name", 20).references(PermissionGroupTable.name, onDelete = ReferenceOption.CASCADE)
    val permission = varchar("name", 30)
    val timeoutTimestamp = long("timeout_timestamp")
}