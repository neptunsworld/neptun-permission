package world.neptuns.permission.common.database.player

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import world.neptuns.permission.common.database.bundle.PermissionBundleTable

object PermissionPlayerPrivatePermissionBundleInfoTable : IntIdTable("permission_player_permission_bundle_infos") {
    val uuid = uuid("uuid").references(PermissionPlayerTable.id, onDelete = ReferenceOption.CASCADE)
    val bundleName = varchar("bundle_name", 30).references(PermissionBundleTable.name, onDelete = ReferenceOption.CASCADE)
    val timeoutTimestamp = long("timeout_timestamp")
}