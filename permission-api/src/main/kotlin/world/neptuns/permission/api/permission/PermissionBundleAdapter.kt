package world.neptuns.permission.api.permission

interface PermissionBundleAdapter {

    suspend fun addPermission(bundleName: String, permission: PermissionInfo)
    suspend fun removePermission(bundleName: String, permission: String)

}