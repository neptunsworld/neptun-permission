package world.neptuns.permission.velocity.command

import com.velocitypowered.api.proxy.Player
import world.neptuns.core.base.api.command.NeptunCommandSender
import world.neptuns.core.base.api.command.extension.findArgument
import world.neptuns.core.base.api.command.extension.generateSuggestions
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommand
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommandExecutor
import world.neptuns.core.base.api.player.extension.sendElementList
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.permission.velocity.util.CheckUtil

@NeptunSubCommand(length = 4, parts = "group <name> perm list")
class PermsGroupPermListSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val groupName = findArgument(player, "name", args, String::class.java) ?: return
        val permissionGroup = CheckUtil.checkPermissionGroup(player, groupName) ?: return

        player.sendElementList(permissionGroup.permissions) { permissionInfo: PermissionInfo ->
            val durationLeftString = if (permissionInfo.timeoutTimestamp == -1L) "permanently" else permissionInfo.durationLeft().toComponents { days, hours, minutes, seconds, _ -> "${days}d, ${hours}h, ${minutes}m, ${seconds}s" }
            "<white>${permissionInfo.name} <gray>(Lasts: <white>$durationLeftString<gray>)"
        }
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "group"))) { add("perm") })
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "group"), Pair(2, "perm"))) { add("list") })
        }
    }

}