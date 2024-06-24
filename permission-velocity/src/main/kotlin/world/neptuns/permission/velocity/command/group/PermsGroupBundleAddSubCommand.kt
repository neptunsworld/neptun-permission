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
import world.neptuns.permission.api.PermissionApi
import world.neptuns.permission.velocity.util.CheckUtil
import java.util.concurrent.TimeUnit

@NeptunSubCommand(minLength = 5, maxLength = 7, parts = "group <name> bundle add <bundleName> [time] [unit]")
class PermsGroupBundleAddSubCommand : NeptunSubCommandExecutor {

    private val availableTimeUnits: List<TimeUnit> = listOf(TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS)

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val groupName = findArgument(player, "name", args, String::class.java) ?: return
        val bundleName = findArgument(player, "bundleName", args, String::class.java) ?: return

        val permissionGroup = CheckUtil.checkPermissionGroup(player, groupName) ?: return
        val permissionBundle = CheckUtil.checkPermissionBundle(player, bundleName) ?: return

        if (permissionGroup.permissionBundles.any { it.name.equals(bundleName, true) }) {
            player.sendMessage(LineKey.key("permission.proxy.group.bundle.already_set"))
            return
        }

        val time = findArgument(player, "time", args, Int::class.java)
        val timeUnitString = findArgument(player, "unit", args, String::class.java)

        var timeUnit: TimeUnit? = null

        if (time != null && timeUnitString != null) {
            val unit = this.availableTimeUnits.find { it.name.equals(timeUnitString, true) }

            if (unit == null) {
                player.sendMessage(LineKey.key("permission.proxy.invalid_time_unit"), Placeholder.parsed("unit", this.availableTimeUnits.joinToString(", ")))
                return
            }

            timeUnit = unit
        }

        val timeoutTimestamp = if (time == null || timeUnit == null) PermissionApi.PERMANENT_DURATION else System.currentTimeMillis() + java.time.Duration.ofNanos(timeUnit.toNanos(time.toLong())).toMillis()
        val permissionBundleInfo = permissionBundle.toBundleInfo(timeoutTimestamp)

        NeptunPermissionProvider.api.permissionGroupAdapter.addPermissionBundle(groupName, permissionBundleInfo)

        val durationLeftString = if (permissionBundleInfo.timeoutTimestamp == PermissionApi.PERMANENT_DURATION) "permanently" else permissionBundleInfo.durationLeft().toComponents { days, hours, minutes, seconds, _ -> "${days}d, ${hours}h, ${minutes}m, ${seconds}s" }
        player.sendMessage(LineKey.key("permission.proxy.group.bundle.added"),
            Placeholder.parsed("name", permissionBundleInfo.name),
            Placeholder.parsed("group", permissionGroup.name),
            Placeholder.parsed("duration", durationLeftString))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "group"))) { add("bundle") })
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "group"), Pair(2, "bundle"))) { add("add") })
            addAll(generateSuggestions(args, 5, listOf(Pair(0, "group"), Pair(2, "bundle"), Pair(3, "add"))) { addAll(NeptunPermissionProvider.api.permissionBundleService.getPermissionBundles().map { it.name }) })
            addAll(generateSuggestions(args, 7, listOf(Pair(0, "group"), Pair(2, "bundle"), Pair(3, "add"))) { addAll(availableTimeUnits.map { it.name }) })
        }
    }

}