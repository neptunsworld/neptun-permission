package world.neptuns.permission.common.database.player.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.player.PermissionPlayerGroupInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerPrivatePermissionInfoTable
import world.neptuns.permission.common.database.player.PermissionPlayerTable
import java.util.*

class PermissionPlayerEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PermissionPlayerEntity>(PermissionPlayerTable)

    var authSession by PermissionPlayerTable.authSession

    val groups by PermissionPlayerGroupInfoEntity referrersOn PermissionPlayerGroupInfoTable.uuid
    val privatePermissions by PermissionPlayerPrivatePermissionInfoEntity referrersOn PermissionPlayerPrivatePermissionInfoTable.uuid
}