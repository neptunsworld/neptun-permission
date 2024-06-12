package world.neptuns.permission.common.database.bundle

import org.jetbrains.exposed.dao.id.IntIdTable

object PermissionBundleTable : IntIdTable("permission_bundles") {
    val name = varchar("name", 30).uniqueIndex()
    val isAuthRequired = bool("is_auth_required")
    val timeoutTimestamp = long("timeout_timestamp")
}