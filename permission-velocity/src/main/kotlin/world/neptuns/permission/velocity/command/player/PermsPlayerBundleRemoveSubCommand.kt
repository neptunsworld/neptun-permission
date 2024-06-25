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

@NeptunSubCommand(length = 5, parts = "player <name> bundle remove <bundlename>")
class PermsPlayerBundleRemoveSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val playerName = findArgument(player, "name", args, String::class.java) ?: return
        val bundleName = findArgument(player, "bundlename", args, String::class.java) ?: return

        val permissionPlayerData = CheckUtil.checkPermissionPlayer(player, playerName) ?: return
        val onlinePlayer = permissionPlayerData.first
        val permissionPlayer = permissionPlayerData.second

        val permissionBundle = CheckUtil.checkPermissionBundle(player, bundleName) ?: return

        if (permissionPlayer.privatePermissionBundles.none { it.name.equals(bundleName, true) }) {
            player.sendMessage(LineKey.key("permission.proxy.player.bundle.not_set"))
            return
        }

        NeptunPermissionProvider.api.permissionPlayerAdapter.removePermissionBundle(permissionPlayer.uuid, bundleName)
        player.sendMessage(LineKey.key("permission.proxy.player.bundle.removed"),
            Placeholder.parsed("name", permissionBundle.name),
            Placeholder.parsed("player", onlinePlayer.name))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        val player = sender.castTo(Player::class.java) ?: return emptyList()

        return buildList {
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "player"), Pair(2, "bundle"))) { add("remove") })
            addAll(generateSuggestions(args, 5, listOf(Pair(0, "player"), Pair(2, "bundle"), Pair(3, "remove"))) {
                val playerName = findArgument(player, "name", args, String::class.java) ?: return@generateSuggestions
                val permissionPlayerData = CheckUtil.checkPermissionPlayer(player, playerName) ?: return@generateSuggestions
                val permissionPlayer = permissionPlayerData.second
                addAll(permissionPlayer.privatePermissionBundles.map { it.name })
            })
        }
    }

}