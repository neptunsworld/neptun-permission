package world.neptuns.permission.common.api.bundle

import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.permission.api.permission.PermissionBundleService
import world.neptuns.permission.api.permission.PermissionInfo

class PermissionBundleServiceImpl : PermissionBundleService {

    override suspend fun getPermissionBundle(name: String): PermissionBundle? {
        TODO("Not yet implemented")
    }

    override suspend fun getPermissionBundles(): Set<PermissionBundle> {
        TODO("Not yet implemented")
    }

    override suspend fun createPermissionBundle(name: String, permissions: MutableSet<PermissionInfo>, isAuthRequired: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePermissionBundle(name: String) {
        TODO("Not yet implemented")
    }

    override suspend fun loadPermissionBundle(name: String) {
        TODO("Not yet implemented")
    }

    override suspend fun unloadPermissionBundle(name: String) {
        TODO("Not yet implemented")
    }

    override fun cachePermissionBundle(name: String, permissionBundle: PermissionBundle) {
        TODO("Not yet implemented")
    }

    override fun removeCachedPermissionBundle(name: String) {
        TODO("Not yet implemented")
    }

    override suspend fun bulkUpdateEntry(updateType: PermissionBundle.Update, key: String, newValue: Any, updateCache: Boolean, result: (Unit) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun updateCachedEntry(updateType: PermissionBundle.Update, key: String, newValue: Any, result: (Unit) -> Unit) {
        TODO("Not yet implemented")
    }

}