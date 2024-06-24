package world.neptuns.permission.velocity.command.bundle

import com.velocitypowered.api.proxy.Player
import world.neptuns.core.base.api.command.NeptunCommandSender
import world.neptuns.core.base.api.command.extension.findArgument
import world.neptuns.core.base.api.command.extension.generateSuggestions
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommand
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommandExecutor
import world.neptuns.core.base.api.player.extension.sendElementList
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.permission.velocity.util.CheckUtil

@NeptunSubCommand(length = 4, parts = "bundle <name> perm list")
class PermsBundlePermListSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val bundleName = findArgument(player, "name", args, String::class.java) ?: return
        val permissionBundle = CheckUtil.checkPermissionBundle(player, bundleName) ?: return

        player.sendElementList(permissionBundle.permissions, PermissionInfo::name)
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "bundle"))) { add("perm") })
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "bundle"), Pair(2, "perm"))) { add("list") })
        }
    }

}