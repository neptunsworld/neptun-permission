package world.neptuns.permission.common.database.bundle.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import world.neptuns.permission.common.database.bundle.PermissionBundlePermissionInfoTable

class PermissionBundlePermissionInfoEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PermissionBundlePermissionInfoEntity>(PermissionBundlePermissionInfoTable)

    var bundleName by PermissionBundlePermissionInfoTable.bundleName
    var permission by PermissionBundlePermissionInfoTable.permission
}