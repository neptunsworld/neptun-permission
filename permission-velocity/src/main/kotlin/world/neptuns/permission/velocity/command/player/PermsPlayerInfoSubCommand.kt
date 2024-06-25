package world.neptuns.permission.velocity.command.player

import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import world.neptuns.core.base.api.NeptunCoreProvider
import world.neptuns.core.base.api.command.NeptunCommandSender
import world.neptuns.core.base.api.command.extension.findArgument
import world.neptuns.core.base.api.command.extension.generateSuggestions
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommand
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommandExecutor
import world.neptuns.core.base.api.language.LineKey
import world.neptuns.core.base.api.player.extension.sendMessage
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.velocity.util.CheckUtil

@NeptunSubCommand(length = 3, parts = "player <name> info")
class PermsPlayerInfoSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val playerName = findArgument(player, "name", args, String::class.java) ?: return
        val permissionPlayerData = CheckUtil.checkPermissionPlayer(player, playerName) ?: return

        val onlinePlayer = permissionPlayerData.first
        val permissionPlayer = permissionPlayerData.second

        val highestPermissionGroup = NeptunPermissionProvider.api.permissionPlayerAdapter.getHighestPermissionGroup(permissionPlayer) ?: return

        player.sendMessage(LineKey.key("permission.proxy.player.info.title"),
            Placeholder.parsed("name", onlinePlayer.name))

        val groupCount = permissionPlayer.groups.size

        player.sendMessage(LineKey.key("permission.proxy.player.info.groups.sub_title"),
            Placeholder.parsed("group_count", groupCount.toString()),
            Placeholder.parsed("highest_group", highestPermissionGroup.first.name))

        if (groupCount > 0) {
            for (permissionGroupInfo in permissionPlayer.groups) {
                val durationLeftString = if (permissionGroupInfo.timeoutTimestamp == -1L) "permanently" else permissionGroupInfo.durationLeft().toComponents { days, hours, minutes, seconds, _ -> "${days}d, ${hours}h, ${minutes}m, ${seconds}s" }
                player.sendMessage(LineKey.key("permission.proxy.player.info.groups"),
                    Placeholder.parsed("group", permissionGroupInfo.name),
                    Placeholder.parsed("duration", durationLeftString))
            }
        }

        val privatePermissionCount = permissionPlayer.privatePermissions.size

        player.sendMessage(LineKey.key("permission.proxy.player.info.private_permissions.sub_title"),
            Placeholder.parsed("permission_count", privatePermissionCount.toString()))

        if (privatePermissionCount > 0) {
            for (permissionInfo in permissionPlayer.privatePermissions) {
                val durationLeftString = if (permissionInfo.timeoutTimestamp == -1L) "permanently" else permissionInfo.durationLeft().toComponents { days, hours, minutes, seconds, _ -> "${days}d, ${hours}h, ${minutes}m, ${seconds}s" }
                player.sendMessage(LineKey.key("permission.proxy.player.info.private_permissions"),
                    Placeholder.parsed("permission", permissionInfo.name),
                    Placeholder.parsed("duration", durationLeftString))
            }
        }

        val privateBundleCount = permissionPlayer.privatePermissionBundles.size

        player.sendMessage(LineKey.key("permission.proxy.player.info.private_bundles.sub_title"),
            Placeholder.parsed("bundle_count", privateBundleCount.toString()))

        if (privateBundleCount > 0) {
            for (permissionBundle in permissionPlayer.privatePermissions) {
                val durationLeftString = if (permissionBundle.timeoutTimestamp == -1L) "permanently" else permissionBundle.durationLeft().toComponents { days, hours, minutes, seconds, _ -> "${days}d, ${hours}h, ${minutes}m, ${seconds}s" }
                player.sendMessage(LineKey.key("permission.proxy.player.info.private_bundles"),
                    Placeholder.parsed("bundle", permissionBundle.name),
                    Placeholder.parsed("duration", durationLeftString))
            }
        }
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 2, listOf(Pair(0, "player"))) { addAll(NeptunCoreProvider.api.playerService.getOnlinePlayerNames()) })
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "player"))) { add("info") })
        }
    }

}