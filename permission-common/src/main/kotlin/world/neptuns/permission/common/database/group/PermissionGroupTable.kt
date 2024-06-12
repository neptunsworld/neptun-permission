package world.neptuns.permission.common.database.group

import org.jetbrains.exposed.dao.id.IntIdTable

object PermissionGroupTable : IntIdTable("permission_groups") {
    val name = varchar("name", 20).uniqueIndex()
    val priority = integer("priority")
    val color = varchar("color", 7)
    val format = text("format")
    val isDefault = bool("is_default")
}