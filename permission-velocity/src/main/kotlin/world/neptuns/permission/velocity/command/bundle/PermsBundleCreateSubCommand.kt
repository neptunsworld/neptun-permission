package world.neptuns.permission.velocity.command.bundle

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

@NeptunSubCommand(length = 4, parts = "bundle <name> create <2FA>")
class PermsBundleCreateSubCommand : NeptunSubCommandExecutor {

    private val bundleService = NeptunPermissionProvider.api.permissionBundleService

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val bundleName = findArgument(player, "name", args, String::class.java) ?: return
        val isAuthRequired = findArgument(player, "2FA", args, Boolean::class.java) ?: return

        if (this.bundleService.getPermissionBundles().any { it.name.equals(bundleName, true) }) {
            player.sendMessage(LineKey.key("permission.proxy.bundle.create.already_exist"))
            return
        }

        this.bundleService.createPermissionBundle(bundleName, isAuthRequired)
        player.sendMessage(LineKey.key("permission.proxy.bundle.create"), Placeholder.parsed("name", bundleName))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "bundle"))) { add("create") })
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "bundle"), Pair(2, "create"))) { addAll(listOf("true", "false")) })
        }
    }

}