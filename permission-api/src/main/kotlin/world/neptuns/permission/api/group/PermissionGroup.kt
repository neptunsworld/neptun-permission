package world.neptuns.permission.api.group

import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.streamline.api.StreamlineApi
import java.io.Serializable

interface PermissionGroup : Serializable {

    val name: String
    var priority: Int

    val hexColorCode: String
    val format: PermissionGroupFormat

    val permissions: MutableSet<PermissionInfo>
    val permissionBundles: MutableSet<PermissionBundleInfo>

    val parents: MutableSet<String>
    val metadata: MutableMap<String, Any>

    var isDefault: Boolean

    fun collectAllPermissions(): Set<PermissionInfo>
    suspend fun clearExpiredPermissions()
    suspend fun hasPermission(permission: String): Boolean

    fun <T> getMetadataValue(key: String, clazz: Class<T>): T? = StreamlineApi.GSON.fromJson(this.metadata[key].toString(), clazz)

    fun toGroupInfo(timeoutTimestamp: Long): PermissionGroupInfo

    enum class Update {
        PRIORITY,

        PERMISSION_ADD,
        PERMISSION_REMOVE,
        PERMISSION_CLEAR,
        PERMISSION_LIST_REMOVE,

        PERMISSION_BUNDLE_ADD,
        PERMISSION_BUNDLE_REMOVE,
        PERMISSION_BUNDLE_CLEAR,
        PERMISSION_BUNDLE_LIST_REMOVE,

        PARENT_ADD,
        PARENT_REMOVE,
        PARENT_CLEAR,

        METADATA_SET,
        METADATA_REMOVE,
        METADATA_CLEAR,

        IS_DEFAULT;
    }

}