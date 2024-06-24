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
import world.neptuns.permission.velocity.util.CheckUtil

@NeptunSubCommand(length = 3, parts = "bundle <name> delete")
class PermsBundleDeleteSubCommand : NeptunSubCommandExecutor {

    private val bundleService = NeptunPermissionProvider.api.permissionBundleService

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val bundleName = findArgument(player, "name", args, String::class.java) ?: return
        val permissionBundle = CheckUtil.checkPermissionBundle(player, bundleName) ?: return

        NeptunPermissionProvider.api.permissionBundleAdapter.deletePermissionBundle(bundleName)
        player.sendMessage(LineKey.key("permission.proxy.bundle.delete"), Placeholder.parsed("name", permissionBundle.name))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "bundle"))) { add("delete") })
        }
    }

}