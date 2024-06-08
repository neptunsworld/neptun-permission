package world.neptuns.permission.api.permission

import world.neptuns.permission.api.utils.Expirable
import java.io.Serializable

interface PermissionBundle : Expirable, Serializable {
    val name: String

    val permissions: MutableSet<PermissionInfo>
    val isAuthRequired: Boolean

    enum class Update {
        ALL,
        PERMISSIONS,
        IS_AUTH_REQUIRED;
    }

}