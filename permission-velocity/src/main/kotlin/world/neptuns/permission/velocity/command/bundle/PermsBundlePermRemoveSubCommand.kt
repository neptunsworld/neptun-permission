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

@NeptunSubCommand(length = 5, parts = "bundle <name> perm remove <permission>")
class PermsBundlePermRemoveSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val bundleName = findArgument(player, "name", args, String::class.java) ?: return
        val permission = findArgument(player, "permission", args, String::class.java) ?: return

        val permissionBundle = CheckUtil.checkPermissionBundle(player, bundleName) ?: return

        if (permissionBundle.permissions.none { it.name.equals(permission, true) }) {
            player.sendMessage(LineKey.key("permission.proxy.bundle.permission.not_set"))
            return
        }

        NeptunPermissionProvider.api.permissionBundleAdapter.removePermission(bundleName, permission)
        player.sendMessage(LineKey.key("permission.proxy.bundle.permission.removed"),
            Placeholder.parsed("name", permission),
            Placeholder.parsed("bundle", permissionBundle.name))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        val player = sender.castTo(Player::class.java) ?: return emptyList()

        return buildList {
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "bundle"), Pair(2, "perm"))) { add("remove") })
            addAll(generateSuggestions(args, 5, listOf(Pair(0, "bundle"), Pair(2, "perm"), Pair(3, "remove"))) {
                val bundleName = findArgument(player, "name", args, String::class.java)
                bundleName?.let {
                    NeptunPermissionProvider.api.permissionBundleService.getPermissionBundle(it)?.let { bundle ->
                        addAll(bundle.permissions.map { permission -> permission.name })
                    }
                }
            })
        }
    }

}