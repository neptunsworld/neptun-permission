package world.neptuns.permission.bukkit

import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import world.neptuns.base.bukkit.api.NeptunBukkitPluginAdapter
import world.neptuns.core.base.api.language.LangNamespace
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.bukkit.listener.PermissionPlayerListener
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
                player.updateCommands()
            }
        }

    }

    override suspend fun onDisableAsync() {

    }

    override fun initNamespace(): LangNamespace = LangNamespace.create("permission.bukkit", "Perms")

}