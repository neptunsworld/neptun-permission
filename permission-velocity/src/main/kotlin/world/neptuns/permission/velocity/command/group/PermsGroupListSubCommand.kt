package world.neptuns.permission.velocity.command.group

import com.velocitypowered.api.proxy.Player
import world.neptuns.core.base.api.command.NeptunCommandSender
import world.neptuns.core.base.api.command.extension.generateSuggestions
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommand
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommandExecutor
import world.neptuns.core.base.api.player.extension.sendElementList
import world.neptuns.permission.api.NeptunPermissionProvider

@NeptunSubCommand(length = 1, parts = "groups")
class PermsGroupListSubCommand : NeptunSubCommandExecutor {

    private val groupService = NeptunPermissionProvider.api.permissionGroupService

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        player.sendElementList(this.groupService.getPermissionGroups().sortedBy { it.priority }) { permissionGroup ->
            "${permissionGroup.name} <gray>(Priority: <white>${permissionGroup.priority}<gray>, IsDefault: <white>${permissionGroup.isDefault}<gray>)"
        }
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 2, listOf(Pair(0, "group"))) { addAll(groupService.getPermissionGroups().sortedBy { it.priority }.map { it.name }) })
        }
    }

}