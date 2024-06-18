package world.neptuns.permission.common.database.player

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object PermissionPlayerPrivatePermissionInfoTable : IntIdTable("permission_player_private_permission_infos") {
    val uuid = uuid("uuid").references(PermissionPlayerTable.uuid, onDelete = ReferenceOption.CASCADE)
    val permission = varchar("name", 30)
    val timeoutTimestamp = long("timeout_timestamp")
}