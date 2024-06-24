package world.neptuns.permission.velocity.command.group

import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import world.neptuns.core.base.api.command.NeptunCommandSender
import world.neptuns.core.base.api.command.extension.findArgument
import world.neptuns.core.base.api.command.extension.generateSuggestions
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommand
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommandExecutor
import world.neptuns.core.base.api.language.LineKey
import world.neptuns.core.base.api.player.extension.sendMessage
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.PermissionApi

@NeptunSubCommand(length = 6, parts = "group <name> create <priority> <color> <default>")
class PermsGroupCreateSubCommand : NeptunSubCommandExecutor {

    private val groupService = NeptunPermissionProvider.api.permissionGroupService

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val groupName = findArgument(player, "name", args, String::class.java) ?: return
        val priority = findArgument(player, "priority", args, Int::class.java) ?: return
        val color = findArgument(player, "color", args, TextColor::class.java) ?: return
        val default = findArgument(player, "default", args, Boolean::class.java) ?: return

        if (this.groupService.getPermissionGroups().any { it.name == groupName || it.priority == priority }) {
            player.sendMessage(LineKey.key("permission.proxy.group.create.already_exist"))
            return
        }

        this.groupService.createPermissionGroup(
            name = groupName,
            priority = priority,
            color = color,
            format = PermissionApi.defaultGroupFormat,
            permissions = mutableSetOf(),
            permissionBundles = mutableSetOf(),
            parents = mutableSetOf(),
            isDefault = default
        )

        player.sendMessage(LineKey.key("permission.proxy.group.create"),
            Placeholder.parsed("group_color", "<${color.asHexString()}>"),
            Placeholder.parsed("name", groupName),
            Placeholder.parsed("priority", priority.toString()),
            Placeholder.parsed("default", default.toString()))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "group"))) { add("create") })
            addAll(generateSuggestions(args, 6, listOf(Pair(0, "group"), Pair(2, "create"))) { addAll(listOf("true", "false")) })
        }
    }

}