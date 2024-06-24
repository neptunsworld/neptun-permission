package world.neptuns.permission.velocity.command.group

import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import world.neptuns.core.base.api.command.NeptunCommandSender
import world.neptuns.core.base.api.command.extension.findArgument
import world.neptuns.core.base.api.command.extension.generateSuggestions
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommand
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommandExecutor
import world.neptuns.core.base.api.language.LineKey
import world.neptuns.core.base.api.player.extension.sendMessage
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.velocity.util.CheckUtil

@NeptunSubCommand(length = 3, parts = "group <name> info")
class PermsGroupInfoSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val groupName = findArgument(player, "name", args, String::class.java) ?: return
        val permissionGroup: PermissionGroup = CheckUtil.checkPermissionGroup(player, groupName) ?: return

        player.sendMessage(LineKey.key("permission.proxy.group.info.title"),
            Placeholder.parsed("group_color", "<${permissionGroup.hexColorCode}>"),
            Placeholder.parsed("group_name", permissionGroup.name.replaceFirstChar { it.uppercase() }))

        val permissionCount = permissionGroup.permissions.size

        player.sendMessage(LineKey.key("permission.proxy.group.info.permissions.sub_title"),
            Placeholder.parsed("permission_count", permissionCount.toString()))

        if (permissionCount > 0) {
            for (permissionInfo in permissionGroup.permissions) {
                val durationLeftString = if (permissionInfo.timeoutTimestamp == -1L) "permanently" else permissionInfo.durationLeft().toComponents { days, hours, minutes, seconds, _ -> "${days}d, ${hours}h, ${minutes}m, ${seconds}s" }
                player.sendMessage(LineKey.key("permission.proxy.group.info.permissions"),
                    Placeholder.parsed("permission", permissionInfo.name),
                    Placeholder.parsed("duration", durationLeftString))
            }
        }

        val parentCount = permissionGroup.parents.size

        player.sendMessage(LineKey.key("permission.proxy.group.info.parents.sub_title"),
            Placeholder.parsed("parent_count", parentCount.toString()))

        if (parentCount > 0) {
            for (parent in permissionGroup.parents) {
                player.sendMessage(LineKey.key("permission.proxy.group.info.parents"),
                    Placeholder.parsed("parent", parent))
            }
        }

        val bundleCount = permissionGroup.permissionBundles.size

        player.sendMessage(LineKey.key("permission.proxy.group.info.bundles.sub_title"),
            Placeholder.parsed("bundle_count", bundleCount.toString()))

        if (bundleCount > 0) {
            for (permissionBundle in permissionGroup.permissionBundles) {
                val durationLeftString = if (permissionBundle.timeoutTimestamp == -1L) "permanently" else permissionBundle.durationLeft().toComponents { days, hours, minutes, seconds, _ -> "${days}d, ${hours}h, ${minutes}m, ${seconds}s" }
                player.sendMessage(LineKey.key("permission.proxy.group.info.bundles"),
                    Placeholder.parsed("bundle", permissionBundle.name),
                    Placeholder.parsed("duration", durationLeftString))
            }
        }
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "group"))) { add("info") })
        }
    }

}