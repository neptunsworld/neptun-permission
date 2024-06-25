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
import world.neptuns.permission.api.PermissionApi
import world.neptuns.permission.common.api.permission.PermissionBundleInfoImpl
import world.neptuns.permission.velocity.util.CheckUtil
import java.util.concurrent.TimeUnit

@NeptunSubCommand(minLength = 5, maxLength = 7, parts = "player <name> bundle add <bundlename> [time] [unit]")
class PermsPlayerBundleAddSubCommand : NeptunSubCommandExecutor {

    private val availableTimeUnits: List<TimeUnit> = listOf(TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS)

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val playerName = findArgument(player, "name", args, String::class.java) ?: return
        val bundleName = findArgument(player, "bundlename", args, String::class.java) ?: return

        val permissionPlayerData = CheckUtil.checkPermissionPlayer(player, playerName) ?: return
        val onlinePlayer = permissionPlayerData.first
        val permissionPlayer = permissionPlayerData.second

        val permissionBundle = CheckUtil.checkPermissionBundle(player, bundleName) ?: return

        if (permissionPlayer.privatePermissionBundles.any { it.name.equals(bundleName, true) }) {
            player.sendMessage(LineKey.key("permission.proxy.player.bundle.already_set"))
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
        val permissionBundleInfo = PermissionBundleInfoImpl(permissionBundle.name, timeoutTimestamp)

        NeptunPermissionProvider.api.permissionPlayerAdapter.addPermissionBundle(permissionPlayer.uuid, permissionBundleInfo)

        val durationLeftString = if (permissionBundleInfo.timeoutTimestamp == -1L) "permanently" else permissionBundleInfo.durationLeft().toComponents { days, hours, minutes, seconds, _ -> "${days}d, ${hours}h, ${minutes}m, ${seconds}s" }
        player.sendMessage(LineKey.key("permission.proxy.player.bundle.added"),
            Placeholder.parsed("name", permissionBundle.name),
            Placeholder.parsed("player", onlinePlayer.name),
            Placeholder.parsed("duration", durationLeftString))
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "player"))) { add("bundle") })
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "player"), Pair(2, "bundle"))) { add("add") })
            addAll(generateSuggestions(args, 7, listOf(Pair(0, "player"), Pair(2, "bundle"), Pair(3, "add"))) { addAll(availableTimeUnits.map { it.name }) })
        }
    }

}