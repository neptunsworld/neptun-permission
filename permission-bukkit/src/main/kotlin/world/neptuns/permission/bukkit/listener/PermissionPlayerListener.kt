package world.neptuns.permission.bukkit.listener

import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.bukkit.NeptunBukkitPlugin
import world.neptuns.permission.bukkit.permissible.BukkitPermissible
import world.neptuns.permission.bukkit.util.PermissibleInjector

class PermissionPlayerListener(private val plugin: NeptunBukkitPlugin) : Listener {

    private val permissionPlayerService = NeptunPermissionProvider.api.permissionPlayerService

    @EventHandler(priority = EventPriority.LOWEST)
    suspend fun onPlayerLogin(event: PlayerLoginEvent) {
        val player = event.player
        val permissionPlayer = this.permissionPlayerService.getPermissionPlayer(player.uniqueId)

        if (permissionPlayer == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text("No permission player found!", NamedTextColor.RED))
            return
        }

        this.permissionPlayerService.cachePermissionPlayer(permissionPlayer.uuid, permissionPlayer)

        PermissibleInjector.inject(player, BukkitPermissible(player, permissionPlayer))

//        val permissionAttachment = BukkitPermissionAttachment(this.plugin, player, permissionPlayer)
//        permissionAttachment.loadPermissions()

//        NeptunCoreBukkitProvider.api.metadataController.apply(player, "attachment", permissionAttachment)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    suspend fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val permissionPlayer = this.permissionPlayerService.getPermissionPlayer(player.uniqueId)

        if (permissionPlayer == null) {
            player.kick(Component.text("No permission player found!", NamedTextColor.RED))
            return
        }

        player.updateCommands()
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    suspend fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        withContext(this.plugin.minecraftDispatcher) {
            PermissibleInjector.uninject(player, true)
        }

        this.permissionPlayerService.removeCachedPermissionPlayer(player.uniqueId)

//        val permissionAttachment = NeptunCoreBukkitProvider.api.metadataController.get<BukkitPermissionAttachment>(player, "attachment")
//        permissionAttachment?.unloadPermissions()
    }

}