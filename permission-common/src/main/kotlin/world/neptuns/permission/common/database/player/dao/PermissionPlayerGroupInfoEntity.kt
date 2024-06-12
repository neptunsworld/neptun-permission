package world.neptuns.permission.common.database.player.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.player.PermissionPlayerGroupInfoTable

class PermissionPlayerGroupInfoEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PermissionPlayerGroupInfoEntity>(PermissionPlayerGroupInfoTable)

    var uuid by PermissionPlayerGroupInfoTable.uuid
    var groupName by PermissionPlayerGroupInfoTable.groupName
    var timeoutTimestamp by PermissionPlayerGroupInfoTable.timeoutTimestamp
}