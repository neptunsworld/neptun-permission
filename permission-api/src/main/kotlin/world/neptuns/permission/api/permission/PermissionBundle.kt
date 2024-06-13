package world.neptuns.permission.api.permission

import world.neptuns.permission.api.utils.Expirable
import java.io.Serializable

interface PermissionBundle : Expirable, Serializable {
    val name: String

    val permissions: MutableSet<PermissionInfo>
    var isAuthRequired: Boolean

    enum class Update {
        PERMISSION_ADD,
        PERMISSION_REMOVE,
        IS_AUTH_REQUIRED;
    }

}