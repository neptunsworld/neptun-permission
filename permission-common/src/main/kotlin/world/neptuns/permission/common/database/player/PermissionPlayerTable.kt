package world.neptuns.permission.common.database.player

import org.jetbrains.exposed.dao.id.UUIDTable

object PermissionPlayerTable : UUIDTable("permission_players") {
    val authSession = text("auth_session").nullable()
}