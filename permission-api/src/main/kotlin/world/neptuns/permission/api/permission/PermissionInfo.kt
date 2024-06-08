package world.neptuns.permission.api.permission

import world.neptuns.permission.api.utils.Expirable
import java.io.Serializable

interface PermissionInfo : Expirable, Serializable {

    val name: String

}