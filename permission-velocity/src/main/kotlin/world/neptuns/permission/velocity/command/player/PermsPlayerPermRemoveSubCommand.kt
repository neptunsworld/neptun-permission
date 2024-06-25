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

@NeptunSubCommand(length = 5, parts = "player <name> perm remove <permission>")
class PermsPlayerPermRemoveSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val playerName = findArgument(player, "name", args, String::class.java) ?: return
        val permission = findArgument(player, "permission", args, String::class.java) ?: return

        val permissionPlayerData = CheckUtil.checkPermissionPlayer(player, playerName) ?: return
        val onlinePlayer = permissionPlayerData.first
        val permissionPlayer = permissionPlayerData.second

        if (permissionPlayer.privatePermissions.none { it.name.equals(permission, true) }) {
            player.sendMessage(LineKey.key("permission.proxy.player.permission.not_set"))
            return
        }

        NeptunPermissionProvider.api.permissionPlayerAdapter.removePrivatePermission(permissionPlayer.uuid, permission)
        player.sendMessage(LineKey.key("permission.proxy.player.permission.removed"),
            Placeholder.parsed("name", permission),
            Placeholder.parsed("player", onlinePlayer.name))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        val player = sender.castTo(Player::class.java) ?: return emptyList()

        return buildList {
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "player"), Pair(2, "perm"))) { add("remove") })
            addAll(generateSuggestions(args, 5, listOf(Pair(0, "player"), Pair(2, "perm"), Pair(3, "remove"))) {
                val playerName = findArgument(player, "name", args, String::class.java) ?:return@generateSuggestions
                val permissionPlayerData = CheckUtil.checkPermissionPlayer(player, playerName) ?: return@generateSuggestions
                val permissionPlayer = permissionPlayerData.second
                addAll(permissionPlayer.privatePermissions.map { it.name })
            })
        }
    }

}