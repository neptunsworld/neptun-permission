package world.neptuns.permission.api.permission

import java.io.Serializable

interface PermissionBundle : Serializable {
    val name: String

    val permissions: MutableSet<PermissionInfo>
    var isAuthRequired: Boolean

    fun toBundleInfo(timeoutTimestamp: Long): PermissionBundleInfo

    enum class Update {
        PERMISSION_ADD,
        PERMISSION_REMOVE,
        PERMISSION_LIST_REMOVE,
        IS_AUTH_REQUIRED;
    }

}