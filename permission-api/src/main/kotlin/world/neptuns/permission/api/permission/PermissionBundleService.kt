package world.neptuns.permission.api.permission

import world.neptuns.streamline.api.utils.Updatable

interface PermissionBundleService : Updatable<String, PermissionBundle.Update, Any> {

    suspend fun getPermissionBundle(name: String): PermissionBundle?
    suspend fun getPermissionBundles(): Set<PermissionBundle>

    suspend fun createPermissionBundle(name: String, permissions: MutableSet<PermissionInfo>, isAuthRequired: Boolean)

    suspend fun deletePermissionBundle(name: String)

    suspend fun loadPermissionBundle(name: String)

    suspend fun unloadPermissionBundle(name: String)

    fun cachePermissionBundle(name: String, permissionBundle: PermissionBundle)

    fun removeCachedPermissionBundle(name: String)

}