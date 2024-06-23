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

@NeptunSubCommand(length = 3, parts = "group <Name> setDefault")
class PermsGroupSetDefaultSubCommand : NeptunSubCommandExecutor {

    private val groupService = NeptunPermissionProvider.api.permissionGroupService

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val groupName = findArgument(player, "name", args, String::class.java) ?: return
        val permissionGroup = this.groupService.getPermissionGroup(groupName)

        if (permissionGroup == null) {
            player.sendMessage(LineKey.key("permission.proxy.group.not_exist"))
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