package world.neptuns.permission.api.group

import world.neptuns.streamline.api.utils.Controller
import world.neptuns.streamline.api.utils.Updatable
import java.util.*

interface PermissionGroupController : Controller<UUID, PermissionGroup>, Updatable<UUID, PermissionGroup.Update, Any> {

    suspend fun getPermissionGroup(name: String): PermissionGroup?

    suspend fun getDefaultGroup(): PermissionGroup?

}