package world.neptuns.permission.velocity.command.bundle

import com.velocitypowered.api.proxy.Player
import world.neptuns.core.base.api.command.NeptunCommandSender
import world.neptuns.core.base.api.command.extension.generateSuggestions
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommand
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommandExecutor
import world.neptuns.core.base.api.player.extension.sendElementList
import world.neptuns.permission.api.NeptunPermissionProvider

@NeptunSubCommand(length = 1, parts = "bundles")
class PermsBundleListSubCommand : NeptunSubCommandExecutor {

    private val bundleService = NeptunPermissionProvider.api.permissionBundleService

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        player.sendElementList(this.bundleService.getPermissionBundles()) { permissionBundle ->
            "${permissionBundle.name} <gray>(2FA: <white>${permissionBundle.isAuthRequired}<gray>)"
        }
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 2, listOf(Pair(0, "bundle"))) { addAll(bundleService.getPermissionBundles().map { it.name }) })
        }
    }

}