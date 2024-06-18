package world.neptuns.permission.bukkit.permissible

import org.bukkit.permissions.PermissibleBase
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.plugin.Plugin
import java.lang.reflect.Field
import java.util.*

class DummyPermissibleBase : PermissibleBase(null) {

    init {
        try {
            ATTACHMENTS_FIELD[this] = Collections.EMPTY_LIST
            PERMISSIONS_FIELD[this] = Collections.EMPTY_MAP
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun isOp(): Boolean {
        return false
    }

    override fun setOp(value: Boolean) {}
    override fun isPermissionSet(name: String): Boolean {
        return false
    }

    override fun isPermissionSet(perm: Permission): Boolean {
        return false
    }

    override fun hasPermission(inName: String): Boolean {
        return false
    }

    override fun hasPermission(perm: Permission): Boolean {
        return false
    }

    @Synchronized
    override fun addAttachment(plugin: Plugin, name: String, value: Boolean): PermissionAttachment {
        return DummyPermissionAttachment(plugin, this)
    }

    @Synchronized
    override fun addAttachment(plugin: Plugin): PermissionAttachment {
        return DummyPermissionAttachment(plugin, this)
    }

    override fun removeAttachment(attachment: PermissionAttachment) {}
    override fun recalculatePermissions() {}
    override fun clearPermissions() {}
    override fun addAttachment(plugin: Plugin, name: String, value: Boolean, ticks: Int): PermissionAttachment? {
        return null
    }

    override fun addAttachment(plugin: Plugin, ticks: Int): PermissionAttachment? {
        return null
    }

    override fun getEffectivePermissions(): Set<PermissionAttachmentInfo> {
        return emptySet()
    }

    companion object {
        private val ATTACHMENTS_FIELD: Field
        private var PERMISSIONS_FIELD: Field

        init {
            try {
                ATTACHMENTS_FIELD = PermissibleBase::class.java.getDeclaredField("attachments")
                ATTACHMENTS_FIELD.setAccessible(true)

                PERMISSIONS_FIELD = PermissibleBase::class.java.getDeclaredField("permissions")
                PERMISSIONS_FIELD.setAccessible(true)
            } catch (e: NoSuchFieldException) {
                throw ExceptionInInitializerError(e)
            }
        }

        fun copyFields(from: PermissibleBase?, to: PermissibleBase?) {
            try {
                ATTACHMENTS_FIELD[to] = ATTACHMENTS_FIELD[from]
                PERMISSIONS_FIELD[to] = PERMISSIONS_FIELD[from]
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            }
        }

        val INSTANCE: DummyPermissibleBase = DummyPermissibleBase()
    }
}