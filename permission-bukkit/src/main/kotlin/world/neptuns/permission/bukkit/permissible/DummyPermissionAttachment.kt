package world.neptuns.permission.bukkit.permissible

import org.bukkit.permissions.PermissibleBase
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.plugin.Plugin

class DummyPermissionAttachment(plugin: Plugin, permissible: PermissibleBase) : PermissionAttachment(plugin, permissible) {

    override fun setPermission(name: String, value: Boolean) {

    }

}