package world.neptuns.permission.common.database.bundle

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object PermissionBundlePermissionInfoTable : IntIdTable("permission_bundle_permission_infos") {
    val bundleName = varchar("bundle_name", 30).references(PermissionBundleTable.name, onDelete = ReferenceOption.CASCADE)
    val permission = varchar("name", 30)
}