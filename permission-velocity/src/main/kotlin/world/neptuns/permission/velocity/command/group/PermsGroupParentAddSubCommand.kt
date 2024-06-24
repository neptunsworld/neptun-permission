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

@NeptunSubCommand(length = 5, parts = "group <name> parent add <parent>")
class PermsGroupParentAddSubCommand : NeptunSubCommandExecutor {

    private val groupService = NeptunPermissionProvider.api.permissionGroupService

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val groupName = findArgument(player, "name", args, String::class.java) ?: return
        val parentGroupName = findArgument(player, "parent", args, String::class.java) ?: return

        val permissionGroup = CheckUtil.checkPermissionGroup(player, groupName) ?: return
        val parentPermissionGroup = CheckUtil.checkPermissionGroup(player, parentGroupName) ?: return

        NeptunPermissionProvider.api.permissionGroupAdapter.addParentGroup(groupName, parentGroupName)
        player.sendMessage(LineKey.key("permission.proxy.group.parent.added"),
            Placeholder.parsed("name", permissionGroup.name),
            Placeholder.parsed("parent", parentPermissionGroup.name))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "group"), Pair(2, "parent"))) { add("add") })
            addAll(generateSuggestions(args, 5, listOf(Pair(0, "group"), Pair(2, "parent"), Pair(3, "add"))) { addAll(groupService.getPermissionGroups().map { it.name }) })
        }
    }

}