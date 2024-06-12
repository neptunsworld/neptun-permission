package world.neptuns.permission.common.database.group

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import world.neptuns.permission.common.database.bundle.PermissionBundleTable

object PermissionGroupBundleInfoTable : IntIdTable("permission_group_bundle_infos") {
    val groupName = varchar("group_name", 20).references(PermissionGroupTable.name, onDelete = ReferenceOption.CASCADE)
    val bundleName = varchar("bundle_name", 30).references(PermissionBundleTable.name, onDelete = ReferenceOption.CASCADE)
}