package world.neptuns.permission.watchdog.rest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import world.neptuns.permission.api.group.PermissionGroupAdapter
import world.neptuns.permission.api.group.PermissionGroupService
import world.neptuns.permission.common.api.group.PermissionGroupImpl
import world.neptuns.permission.common.api.permission.PermissionInfoImpl

fun Routing.permissionGroupRouting(
    permissionGroupService: PermissionGroupService,
    permissionGroupAdapter: PermissionGroupAdapter,
) {

    route("/groups") {
        get {
            call.respond(permissionGroupService.getPermissionGroups())
        }

        get("/{name}") {
            val name = call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val group = permissionGroupService.getPermissionGroup(name)
                ?: return@get call.respondText("Group not found", status = HttpStatusCode.NotFound)
            call.respond(group)
        }

        post {
            val group = call.receive<PermissionGroupImpl>()

            if (permissionGroupService.getPermissionGroup(group.name) != null) {
                call.respond(HttpStatusCode.BadRequest, "Group already exists!")
                return@post
            }

            //TODO: permissionGroupService.createPermissionGroup(group)
            call.respond(HttpStatusCode.Created, group)
        }

        get("/{groupName}/permissions") {
            val groupName = call.parameters["groupName"]
            if (groupName == null) {
                call.respond(HttpStatusCode.BadRequest, "Group name is missing")
                return@get
            }

            val permissionGroup = permissionGroupService.getPermissionGroup(groupName)

            if (permissionGroup == null) {
                call.respond(HttpStatusCode.BadRequest, "Group doesn't exists!")
                return@get
            }

            call.respond(permissionGroup.permissions)
        }

        put("/{groupName}/permissions") {
            val groupName = call.parameters["groupName"]
            if (groupName == null) {
                call.respond(HttpStatusCode.BadRequest, "Group name is missing")
                return@put
            }

            val permissionGroup = permissionGroupService.getPermissionGroup(groupName)

            if (permissionGroup == null) {
                call.respond(HttpStatusCode.BadRequest, "Group doesn't exists!")
                return@put
            }

            val permission = call.receive<PermissionInfoImpl>()

            if (permissionGroup.permissions.any { it.name.equals(permission.name, true) }) {
                call.respond(HttpStatusCode.BadRequest, "Group already has the permission!")
                return@put
            }

            //TODO: permissionGroupAdapter.addPermission(groupName, permission)
            call.respond(HttpStatusCode.OK, "Permission added to group $groupName")
        }

        delete("/{name}") {
            val name = call.parameters["name"]
                ?: return@delete call.respondText("Missing group name", status = HttpStatusCode.BadRequest)

            if (permissionGroupService.getPermissionGroup(name) == null) {
                call.respond(HttpStatusCode.NotFound, "Group not found")
                return@delete
            }

            //TODO: permissionGroupService.deletePermissionGroup(name)
            call.respondText("Group deleted!", status = HttpStatusCode.OK)
        }
    }

}