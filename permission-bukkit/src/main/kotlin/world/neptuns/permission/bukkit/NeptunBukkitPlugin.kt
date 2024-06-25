package world.neptuns.permission.bukkit

import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import world.neptuns.base.bukkit.api.NeptunBukkitPluginAdapter
import world.neptuns.core.base.api.language.LangNamespace
import world.neptuns.core.base.api.player.extension.uuid
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.bukkit.listener.PermissionPlayerListener
import world.neptuns.permission.bukkit.permissible.BukkitPermissible
import world.neptuns.permission.bukkit.util.PermissibleInjector
import world.neptuns.permission.common.PermissionApiImpl
import world.neptuns.permission.common.packet.PlayerUpdatePermissionsPacket
import world.neptuns.streamline.api.NeptunStreamlineProvider
import world.neptuns.streamline.api.packet.NetworkChannelRegistry

class NeptunBukkitPlugin : NeptunBukkitPluginAdapter(null, false) {

    override suspend fun onEnableAsync() {
        val permissionApi = PermissionApiImpl()
        NeptunPermissionProvider.api = permissionApi

        permissionApi.loadContents()

        server.pluginManager.registerSuspendingEvents(PermissionPlayerListener(this), this)

        NeptunStreamlineProvider.api.packetController.listenForPacket(NetworkChannelRegistry.SERVICE, PlayerUpdatePermissionsPacket::class.java) { packet ->
            withContext(minecraftDispatcher) {
                val player = Bukkit.getPlayer(packet.uuid) ?: return@withContext
                val permissionPlayer = NeptunPermissionProvider.api.permissionPlayerService.getPermissionPlayer(player.uuid)
                    ?: return@withContext

                Bukkit.getScheduler().runTask(this@NeptunBukkitPlugin, Runnable {
                    PermissibleInjector.uninject(player, true)
                    PermissibleInjector.inject(player, BukkitPermissible(player, permissionPlayer))
                    runBlocking {
                        player.updateCommands()
                    }
                })
            }
        }

    }

    override suspend fun onDisableAsync() {

    }

    override fun initNamespace(): LangNamespace = LangNamespace.create("permission.bukkit", "Perms")

}