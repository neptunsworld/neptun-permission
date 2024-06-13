package world.neptuns.permission.api.permission

import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.utils.Expirable
import java.io.Serializable

interface PermissionBundleInfo : Expirable, Serializable {

    val name: String

    suspend fun toBundle() = NeptunPermissionProvider.api.permissionBundleService.getPermissionBundle(this.name)

}