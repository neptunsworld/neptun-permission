package world.neptuns.permission.velocity.command

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

@NeptunSubCommand(length = 5, parts = "group <name> parent remove <parent>")
class PermsGroupParentRemoveSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val groupName = findArgument(player, "name", args, String::class.java) ?: return
        val parentGroupName = findArgument(player, "parent", args, String::class.java) ?: return

        val permissionGroup = CheckUtil.checkPermissionGroup(player, groupName) ?: return
        val parentPermissionGroup = CheckUtil.checkPermissionGroup(player, parentGroupName) ?: return

        if (permissionGroup.parents.none { it.equals(groupName, true) }) {
            player.sendMessage(LineKey.key("permission.proxy.group.parent.not_set"), Placeholder.parsed("parent", parentPermissionGroup.name))
            return
        }

        NeptunPermissionProvider.api.permissionGroupAdapter.removeParentGroup(groupName, parentGroupName)
        player.sendMessage(LineKey.key("permission.proxy.group.parent.removed"),
            Placeholder.parsed("name", permissionGroup.name),
            Placeholder.parsed("parent", parentPermissionGroup.name))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        val player = sender.castTo(Player::class.java) ?: return emptyList()

        return buildList {
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "group"), Pair(2, "parent"))) { add("remove") })
            addAll(generateSuggestions(args, 5, listOf(Pair(0, "group"), Pair(2, "parent"), Pair(3, "remove"))) {
                val groupName = findArgument(player, "name", args, String::class.java)
                groupName?.let {
                    NeptunPermissionProvider.api.permissionGroupService.getPermissionGroup(it)?.let { group ->
                        addAll(group.parents)
                    }
                }
            })
        }
    }

}