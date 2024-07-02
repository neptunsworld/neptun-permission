package world.neptuns.permission.watchdog.rest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import world.neptuns.permission.api.group.PermissionGroupService
import world.neptuns.permission.common.api.group.PermissionGroupImpl

fun Routing.permissionGroupRouting(permissionGroupService: PermissionGroupService) {

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