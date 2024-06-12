package world.neptuns.permission.common.database.player

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import world.neptuns.permission.common.database.group.PermissionGroupTable

object PermissionPlayerGroupInfoTable : IntIdTable("permission_player_group_infos") {
    val uuid = uuid("uuid").references(PermissionPlayerTable.id, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val groupName = varchar("group_name", 20).references(PermissionGroupTable.name, onDelete = ReferenceOption.CASCADE)
    val timeoutTimestamp = long("timeout_timestamp")
}