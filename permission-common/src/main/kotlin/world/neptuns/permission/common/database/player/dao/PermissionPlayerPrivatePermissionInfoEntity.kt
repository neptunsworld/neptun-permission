package world.neptuns.permission.common.database.player.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.player.PermissionPlayerPrivatePermissionInfoTable

class PermissionPlayerPrivatePermissionInfoEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PermissionPlayerPrivatePermissionInfoEntity>(PermissionPlayerPrivatePermissionInfoTable)

    var uuid by PermissionPlayerPrivatePermissionInfoTable.uuid
    var permission by PermissionPlayerPrivatePermissionInfoTable.permission
    var timeoutTimestamp by PermissionPlayerPrivatePermissionInfoTable.timeoutTimestamp
}