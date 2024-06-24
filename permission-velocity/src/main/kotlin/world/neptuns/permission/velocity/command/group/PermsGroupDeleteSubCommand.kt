package world.neptuns.permission.velocity.command.group

import com.velocitypowered.api.proxy.Player
import world.neptuns.core.base.api.command.NeptunCommandSender
import world.neptuns.core.base.api.command.extension.findArgument
import world.neptuns.core.base.api.command.extension.generateSuggestions
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommand
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommandExecutor
import world.neptuns.core.base.api.language.LineKey
import world.neptuns.core.base.api.player.extension.sendMessage
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.velocity.util.CheckUtil

@NeptunSubCommand(length = 3, parts = "group <Name> delete")
class PermsGroupDeleteSubCommand : NeptunSubCommandExecutor {

    private val groupService = NeptunPermissionProvider.api.permissionGroupService

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val groupName = findArgument(player, "name", args, String::class.java) ?: return
        val permissionGroup = CheckUtil.checkPermissionGroup(player, groupName) ?: return

        if (permissionGroup.isDefault) {
            player.sendMessage(LineKey.key("permission.proxy.group.delete.default_group_not_deletable"))
            return
        }

        this.groupService.deletePermissionGroup(groupName)
        player.sendMessage(LineKey.key("permission.proxy.group.delete"))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "group"))) { add("delete") })
        }
    }

}