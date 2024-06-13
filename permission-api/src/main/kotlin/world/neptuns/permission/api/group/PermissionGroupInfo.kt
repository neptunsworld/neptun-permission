package world.neptuns.permission.api.group

import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.utils.Expirable
import java.io.Serializable

interface PermissionGroupInfo : Expirable, Serializable {

    val name: String

    suspend fun toGroup() = NeptunPermissionProvider.api.permissionGroupService.getPermissionGroup(this.name)

}