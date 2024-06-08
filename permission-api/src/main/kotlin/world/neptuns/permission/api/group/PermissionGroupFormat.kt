package world.neptuns.permission.api.group

import java.io.Serializable

data class PermissionGroupFormat(val chatFormat: String, val tabFormat: String, val sidebarFormat: String) : Serializable