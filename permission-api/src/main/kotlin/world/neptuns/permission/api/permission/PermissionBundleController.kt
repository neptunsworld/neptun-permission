package world.neptuns.permission.api.permission

import world.neptuns.streamline.api.utils.Controller
import world.neptuns.streamline.api.utils.Updatable

interface PermissionBundleController : Controller<String, PermissionBundle>, Updatable<String, PermissionBundle.Update, Any> {

    suspend fun getPermissionBundle(name: String): PermissionBundle?
    suspend fun getPermissionBundles(): Set<PermissionBundle>

    suspend fun deletePermissionBundle(name: String)

}