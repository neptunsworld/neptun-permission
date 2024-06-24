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
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.velocity.util.CheckUtil

@NeptunSubCommand(length = 6, parts = "group <name> metadata set <key> <value>")
class PermsGroupMetadataSetSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val groupName = findArgument(player, "name", args, String::class.java) ?: return
        val key = findArgument(player, "key", args, String::class.java) ?: return
        val value = findArgument(player, "value", args, String::class.java) ?: return

        val permissionGroup = CheckUtil.checkPermissionGroup(player, groupName) ?: return

        if (permissionGroup.metadata.containsKey(key)) {
            player.sendMessage(LineKey.key("permission.proxy.group.metadata.already_set"), Placeholder.parsed("key", key))
            return
        }

        NeptunPermissionProvider.api.permissionGroupAdapter.setMetadata(groupName, key, value)
        player.sendMessage(LineKey.key("permission.proxy.group.metadata.set"),
            Placeholder.parsed("key", key),
            Placeholder.parsed("value", value),
            Placeholder.parsed("group", permissionGroup.name))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "group"), Pair(2, "metadata"))) { add("set") })
        }
    }

}