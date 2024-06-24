package world.neptuns.permission.velocity.command.group

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

@NeptunSubCommand(length = 5, parts = "group <name> bundle remove <bundleName>")
class PermsGroupBundleRemoveSubCommand : NeptunSubCommandExecutor {

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val groupName = findArgument(player, "name", args, String::class.java) ?: return
        val bundleName = findArgument(player, "bundleName", args, String::class.java) ?: return

        val permissionGroup = CheckUtil.checkPermissionGroup(player, groupName) ?: return

        if (permissionGroup.permissionBundles.none { it.name.equals(bundleName, true) }) {
            player.sendMessage(LineKey.key("permission.proxy.group.bundle.not_set"))
            return
        }

        NeptunPermissionProvider.api.permissionGroupAdapter.removePermissionBundle(groupName, bundleName)
        player.sendMessage(LineKey.key("permission.proxy.group.bundle.removed"),
            Placeholder.parsed("name", bundleName),
            Placeholder.parsed("group", permissionGroup.name))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        val player = sender.castTo(Player::class.java) ?: return emptyList()

        return buildList {
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "group"), Pair(2, "bundle"))) { add("remove") })
            addAll(generateSuggestions(args, 5, listOf(Pair(0, "group"), Pair(2, "bundle"), Pair(3, "remove"))) {
                val groupName = findArgument(player, "name", args, String::class.java)
                groupName?.let {
                    NeptunPermissionProvider.api.permissionGroupService.getPermissionGroup(it)?.let { group ->
                        addAll(group.permissionBundles.map { permissionBundleInfo -> permissionBundleInfo.name })
                    }
                }
            })
        }
    }

}