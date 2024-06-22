package world.neptuns.permission.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import world.neptuns.core.base.api.NeptunCoreProvider
import world.neptuns.core.base.api.language.LangNamespace
import world.neptuns.core.base.api.util.NeptunPlugin
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.common.PermissionApiImpl
import world.neptuns.permission.velocity.command.PermsCommand
import world.neptuns.permission.velocity.listener.PermissionPlayerListener

@Plugin(
    id = "neptun_permission",
    name = "neptun_permission",
    version = "1.0.0",
    authors = ["TGamings"],
    dependencies = [Dependency(id = "neptun_core_base")]
)
class NeptunVelocityPlugin @Inject constructor(
    suspendingPluginContainer: SuspendingPluginContainer,
    val proxyServer: ProxyServer,
) : NeptunPlugin {

    override lateinit var namespace: LangNamespace

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Subscribe
    suspend fun onProxyInitialization(event: ProxyInitializeEvent) {
        NeptunCoreProvider.api.languageController.addContentToLanguage(this::class.java)

        this.namespace = LangNamespace.create("permission.proxy", "Perms")

        val permissionApi = PermissionApiImpl()
        NeptunPermissionProvider.api = permissionApi

        permissionApi.loadContents()

        this.proxyServer.eventManager.registerSuspend(this, PermissionPlayerListener())

        registerCommand(PermsCommand())
    }

}