package world.neptuns.permission.watchdog

import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.permission.watchdog.rest.permissionGroupRouting

object WatchdogMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val app = WatchdogApp()
        app.start()

        // val webServer = startWebServer()

        Runtime.getRuntime().addShutdownHook(Thread {
            app.stop()
            // webServer.stop()
        })
    }

    private fun startWebServer(): NettyApplicationEngine {
        return embeddedServer(Netty, port = 80) {
            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                        .registerTypeAdapter(PermissionInfo::class.java, PermissionInfoAdapter())
                        .registerTypeAdapter(PermissionBundleInfo::class.java, PermissionBundleInfoAdapter())
                }
            }
            install(CORS) {
                allowMethod(HttpMethod.Options)
                allowMethod(HttpMethod.Post)
                allowMethod(HttpMethod.Get)
                allowMethod(HttpMethod.Delete)
                allowHeader(HttpHeaders.AccessControlAllowOrigin)
                allowHeader(HttpHeaders.ContentType)
                allowHeader(HttpHeaders.Authorization)
                allowSameOrigin = true
                anyHost()
            }
            routing {
                permissionGroupRouting(NeptunPermissionProvider.api.permissionGroupService, NeptunPermissionProvider.api.permissionGroupAdapter)
            }
        }.start(wait = true)
    }

}