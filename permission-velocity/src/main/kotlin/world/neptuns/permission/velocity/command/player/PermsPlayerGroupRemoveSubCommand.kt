package world.neptuns.permission.velocity.command.player

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

@NeptunSubCommand(length = 5, parts = "player <name> group remove <groupname>")
class PermsPlayerGroupRemoveSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val playerName = findArgument(player, "name", args, String::class.java) ?: return
        val groupName = findArgument(player, "groupname", args, String::class.java) ?: return

        val permissionGroup = CheckUtil.checkPermissionGroup(player, groupName) ?: return

        val permissionPlayerData = CheckUtil.checkPermissionPlayer(player, playerName) ?: return
        val onlinePlayer = permissionPlayerData.first
        val permissionPlayer = permissionPlayerData.second

        if (permissionPlayer.groups.none { it.name.equals(groupName, true) }) {
            player.sendMessage(LineKey.key("permission.proxy.player.group.not_set"))
            return
        }

        if (permissionPlayer.groups.size == 1 && permissionPlayer.groups.all { it.toGroup()!!.isDefault }) {
            player.sendMessage(LineKey.key("permission.proxy.player.group.delete.already_on_default_group"), Placeholder.parsed("name", onlinePlayer.name))
            return
        }

        NeptunPermissionProvider.api.permissionPlayerAdapter.removePermissionGroup(permissionPlayer.uuid, groupName)
        player.sendMessage(LineKey.key("permission.proxy.player.group.removed"),
            Placeholder.parsed("group", permissionGroup.name),
            Placeholder.parsed("player", onlinePlayer.name))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        val player = sender.castTo(Player::class.java) ?: return emptyList()

        return buildList {
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "player"))) { add("group") })
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "player"), Pair(2, "group"))) { add("remove") })
            addAll(generateSuggestions(args, 5, listOf(Pair(0, "player"), Pair(2, "group"), Pair(3, "remove"))) {
                val playerName = findArgument(player, "name", args, String::class.java) ?: return@generateSuggestions
                val permissionPlayerData = CheckUtil.checkPermissionPlayer(player, playerName) ?: return@generateSuggestions
                val permissionPlayer = permissionPlayerData.second
                addAll(permissionPlayer.groups.map { it.name })
            })
        }
    }

}