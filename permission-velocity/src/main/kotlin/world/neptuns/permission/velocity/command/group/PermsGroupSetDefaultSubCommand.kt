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

@NeptunSubCommand(length = 3, parts = "group <name> setDefault")
class PermsGroupSetDefaultSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val groupName = findArgument(player, "name", args, String::class.java) ?: return
        val permissionGroup = CheckUtil.checkPermissionGroup(player, groupName) ?: return

        if (permissionGroup.hasPermission("*")) {
            player.sendMessage(LineKey.key("permission.proxy.group.set_default.to_powerful"))
            return
        }

        val newIsDefaultState = !permissionGroup.isDefault
        NeptunPermissionProvider.api.permissionGroupAdapter.setDefault(groupName, newIsDefaultState)

        player.sendMessage(LineKey.key("permission.proxy.group.set_default"), Placeholder.parsed("name", permissionGroup.name))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "group"))) { add("setDefault") })
        }
    }

}