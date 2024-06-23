package world.neptuns.permission.velocity.command

import com.velocitypowered.api.proxy.Player
import world.neptuns.core.base.api.command.NeptunCommandSender
import world.neptuns.core.base.api.command.extension.findArgument
import world.neptuns.core.base.api.command.extension.generateSuggestions
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommand
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommandExecutor
import world.neptuns.core.base.api.player.extension.sendElementList
import world.neptuns.permission.velocity.util.CheckUtil

@NeptunSubCommand(length = 4, parts = "group <name> parent list")
class PermsGroupParentListSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val groupName = findArgument(player, "name", args, String::class.java) ?: return
        val permissionGroup = CheckUtil.checkPermissionGroup(player, groupName) ?: return

        player.sendElementList(permissionGroup.parents) { it }
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "group"))) { add("parent") })
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "group"), Pair(2, "parent"))) { add("list") })
        }
    }

}