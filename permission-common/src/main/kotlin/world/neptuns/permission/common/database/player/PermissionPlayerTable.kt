package world.neptuns.permission.common.database.player

import org.jetbrains.exposed.dao.id.IntIdTable

object PermissionPlayerTable : IntIdTable("permission_players") {
    val uuid = uuid("uuid").uniqueIndex()
    val authSession = text("auth_session").nullable()
}