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

@NeptunSubCommand(length = 5, parts = "group <name> metadata remove <key>")
class PermsGroupMetadataRemoveSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val groupName = findArgument(player, "name", args, String::class.java) ?: return
        val key = findArgument(player, "key", args, String::class.java) ?: return

        val permissionGroup = CheckUtil.checkPermissionGroup(player, groupName) ?: return

        if (!permissionGroup.metadata.containsKey(key)) {
            player.sendMessage(LineKey.key("permission.proxy.group.metadata.not_set"), Placeholder.parsed("key", key))
            return
        }

        NeptunPermissionProvider.api.permissionGroupAdapter.removeMetadata(groupName, key)
        player.sendMessage(LineKey.key("permission.proxy.group.metadata.removed"),
            Placeholder.parsed("key", key),
            Placeholder.parsed("group", permissionGroup.name))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        val player = sender.castTo(Player::class.java) ?: return emptyList()

        return buildList {
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "group"), Pair(2, "metadata"))) { add("remove") })
            addAll(generateSuggestions(args, 5, listOf(Pair(0, "group"), Pair(2, "metadata"), Pair(3, "set"))) {
                val groupName = findArgument(player, "name", args, String::class.java)
                groupName?.let {
                    NeptunPermissionProvider.api.permissionGroupService.getPermissionGroup(it)?.let { group ->
                        addAll(group.metadata.keys)
                    }
                }
            })
        }
    }

}