package world.neptuns.permission.watchdog.rest

import com.google.gson.GsonBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.api.group.PermissionGroupAdapter
import world.neptuns.permission.api.group.PermissionGroupService
import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.permission.common.api.group.PermissionGroupImpl
import world.neptuns.permission.common.api.permission.PermissionInfoImpl
import world.neptuns.permission.watchdog.PermissionBundleInfoAdapter
import world.neptuns.permission.watchdog.PermissionInfoAdapter

fun Routing.permissionGroupRouting(
    groupService: PermissionGroupService,
    permissionGroupAdapter: PermissionGroupAdapter,
) {

    route("/groups") {
        get {
            call.respond(groupService.getPermissionGroups())
        }
        post {

            val gson = GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(PermissionInfo::class.java, PermissionInfoAdapter())
                .registerTypeAdapter(PermissionBundleInfo::class.java, PermissionBundleInfoAdapter())
                .create()
//            try {
//                val permissionGroup: PermissionGroupImpl = gson.fromJson(call.receiveText(), PermissionGroupImpl::class.java)
//
//                val requestBody = call.receiveText()
//                println("Received request body: $requestBody")
//            } catch (exception: Exception) {
//                println("Error: ${exception.message}")
//            }

//            val group = call.receive<PermissionGroupImpl>()
            val group = gson.fromJson(call.receiveText(), PermissionGroupImpl::class.java)
//            println("${gson.toJson(group)}")

            if (groupService.getPermissionGroup(group.name) != null) {
                println("Group already exists!")
                //call.respond(HttpStatusCode.BadRequest, "Group already exists!")
//                call.respond(HttpStatusCode.BadRequest, ErrorResponse(400, "Ungültige ID"))
                call.respondText("{\"message\": \"Group already exists!\"}", status = HttpStatusCode.BadRequest)
                return@post
            }

            //TODO: groupService.createPermissionGroup(group)
            try {
                println("123")
                groupService.createPermissionGroup(group)
                println("Group created!")
                call.respond(HttpStatusCode.OK, group)
            } catch (exception: Exception) {
                println(exception.message)
            }
        }

        route("/{groupName}") {
            get {
                val groupName = call.parameters["groupName"]
                val permissionGroup = validatePermissionGroup(groupService, groupName, call) ?: return@get

                call.respond(permissionGroup)
            }
            delete {
                val groupName = call.parameters["groupName"]
                val permissionGroup = validatePermissionGroup(groupService, groupName, call) ?: return@delete

                //TODO: permissionGroupService.deletePermissionGroup(name)
                if (groupName == null) {
                    call.respondText("Group not exists!", status = HttpStatusCode.BadRequest)
                    return@delete
                }

                groupService.deletePermissionGroup(groupName)
                println("Group deleted!")
                call.respond(HttpStatusCode.OK, "Group ${permissionGroup.name} deleted!")
            }

            // permissions (get, add, delete)
            get("/permissions") {
                val groupName = call.parameters["groupName"]
                val permissionGroup = validatePermissionGroup(groupService, groupName, call) ?: return@get

                call.respond(permissionGroup.permissions)
            }
            post("/permissions") {
                val groupName = call.parameters["groupName"]
                val permissionGroup = validatePermissionGroup(groupService, groupName, call) ?: return@post

                val permission = call.receive<PermissionInfoImpl>()

                if (permissionGroup.permissions.any { it.name.equals(permission.name, true) }) {
                    call.respond(HttpStatusCode.BadRequest, "Group already has the permission!")
                    return@post
                }

                //TODO: permissionGroupAdapter.addPermission(groupName!!, permission)
                call.respond(HttpStatusCode.OK, "Permission ${permission.name} added to group $groupName")
            }
            delete("/permissions") {
                val groupName = call.parameters["groupName"]
                val permissionGroup = validatePermissionGroup(groupService, groupName, call) ?: return@delete

                val permissionName = call.receiveText()

                if (permissionGroup.permissions.none { it.name.equals(permissionName, true) }) {
                    call.respond(HttpStatusCode.BadRequest, "Group does not have this permission!")
                    return@delete
                }

                //TODO: permissionGroupAdapter.removePermission(groupName!!, permissionName)
                call.respond(HttpStatusCode.OK, "Permission $permissionName deleted from group $groupName")
            }

            // bundles (get, add, delete)
            get("/bundles") {
                val groupName = call.parameters["groupName"]
                val permissionGroup = validatePermissionGroup(groupService, groupName, call) ?: return@get

                call.respond(permissionGroup.permissionBundles)
            }
            post("/bundles") {
                val groupName = call.parameters["groupName"]
                val permissionGroup = validatePermissionGroup(groupService, groupName, call) ?: return@post

                val bundle = call.receive<PermissionBundleInfo>()

                if (permissionGroup.permissionBundles.any { it.name.equals(bundle.name, true) }) {
                    call.respond(HttpStatusCode.BadRequest, "Group already has this permission bundle!")
                    return@post
                }

                //TODO: permissionGroupAdapter.addPermissionBundle(groupName!!, bundle)
                call.respond(HttpStatusCode.OK, "Permission bundle ${bundle.name} added to group $groupName")
            }
            delete("/bundles") {
                val groupName = call.parameters["groupName"]
                val permissionGroup = validatePermissionGroup(groupService, groupName, call) ?: return@delete

                val bundleName = call.receiveText()

                if (permissionGroup.permissionBundles.none { it.name.equals(bundleName, true) }) {
                    call.respond(HttpStatusCode.BadRequest, "Group does not have this permission bundle!")
                    return@delete
                }

                //TODO: permissionGroupAdapter.removePermissionBundle(groupName!!, bundleName)
                call.respond(HttpStatusCode.OK, "Permission bundle $bundleName removed from group $groupName")
            }

            // parents (get, add, delete)
            get("/parents") {
                val groupName = call.parameters["groupName"]
                val permissionGroup = validatePermissionGroup(groupService, groupName, call) ?: return@get

                call.respond(permissionGroup.parents)
            }
            post("/parents") {
                val groupName = call.parameters["groupName"]
                val permissionGroup = validatePermissionGroup(groupService, groupName, call) ?: return@post

                val parentName = call.receiveText()

                if (permissionGroup.permissionBundles.any { it.name.equals(parentName, true) }) {
                    call.respond(HttpStatusCode.BadRequest, "Group already has this parent group!")
                    return@post
                }

                //TODO: permissionGroupAdapter.addParentGroup(groupName!!, parentName)
                call.respond(HttpStatusCode.OK, "Parent group $parentName added to group $groupName")
            }
            delete("/parents") {
                val groupName = call.parameters["groupName"]
                val permissionGroup = validatePermissionGroup(groupService, groupName, call) ?: return@delete

                val parentName = call.receiveText()

                if (permissionGroup.permissionBundles.none { it.name.equals(parentName, true) }) {
                    call.respond(HttpStatusCode.BadRequest, "Group does not have this parent group!")
                    return@delete
                }

                //TODO: permissionGroupAdapter.removeParentGroup(groupName!!, parentName)
                call.respond(HttpStatusCode.OK, "Parent group $parentName removed from group $groupName")
            }
        }


//
//        get("/{groupName}/bundles") {
//            val groupName = call.parameters["groupName"]
//            val permissionGroup = validatePermissionGroup(groupService, groupName, call) ?: return@get
//
//            call.respond(permissionGroup.permissionBundles)
//        }
//        put("/{groupName}/bundles") {
//            val groupName = call.parameters["groupName"]
//            val permissionGroup = validatePermissionGroup(groupService, groupName, call) ?: return@put
//
//            val bundleInfo = call.receive<PermissionBundleInfoImpl>()
//
//            if (permissionGroup.permissionBundles.any { it.name.equals(bundleInfo.name, true) }) {
//                call.respond(HttpStatusCode.BadRequest, "Permission already has the permission!")
//                return@put
//            }
//
//            //TODO: permissionGroupAdapter.addPermissionBundle(groupName!!, bundleInfo)
//            call.respond(HttpStatusCode.OK, "Permission bundles added to group $groupName")
//        }
//
    }

}

private suspend fun validatePermissionGroup(
    groupService: PermissionGroupService,
    groupName: String?,
    call: ApplicationCall
): PermissionGroup? {
    if (groupName == null) {
        call.respond(HttpStatusCode.BadRequest, "Group name is missing")
        return null
    }

    val permissionGroup = groupService.getPermissionGroup(groupName)

    if (permissionGroup == null) {
        call.respond(HttpStatusCode.BadRequest, "Group doesn't exists!")
        return null
    }

    return permissionGroup
}