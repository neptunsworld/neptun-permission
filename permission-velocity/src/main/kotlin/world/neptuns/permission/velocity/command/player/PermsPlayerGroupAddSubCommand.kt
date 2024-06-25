package world.neptuns.permission.velocity.command.player

import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import world.neptuns.core.base.api.NeptunCoreProvider
import world.neptuns.core.base.api.command.NeptunCommandSender
import world.neptuns.core.base.api.command.extension.findArgument
import world.neptuns.core.base.api.command.extension.generateSuggestions
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommand
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommandExecutor
import world.neptuns.core.base.api.language.LineKey
import world.neptuns.core.base.api.player.extension.sendMessage
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.PermissionApi
import world.neptuns.permission.common.api.group.PermissionGroupInfoImpl
import world.neptuns.permission.velocity.util.CheckUtil
import java.util.concurrent.TimeUnit

@NeptunSubCommand(minLength = 5, maxLength = 7, parts = "player <name> group add <groupname> [time] [unit]")
class PermsPlayerGroupAddSubCommand : NeptunSubCommandExecutor {

    private val availableTimeUnits: List<TimeUnit> = listOf(TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS)

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        val player = sender.castTo(Player::class.java) ?: return

        val playerName = findArgument(player, "name", args, String::class.java) ?: return
        val groupName = findArgument(player, "groupname", args, String::class.java) ?: return

        CheckUtil.checkPermissionGroup(player, groupName) ?: return

        val permissionPlayerData = CheckUtil.checkPermissionPlayer(player, playerName) ?: return
        val onlinePlayer = permissionPlayerData.first
        val permissionPlayer = permissionPlayerData.second

        if (permissionPlayer.groups.any { it.name.equals(groupName, true) }) {
            player.sendMessage(LineKey.key("permission.proxy.player.group.already_set"))
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
        val permissionGroupInfo = PermissionGroupInfoImpl(groupName, timeoutTimestamp)

        NeptunPermissionProvider.api.permissionPlayerAdapter.addPermissionGroup(permissionPlayer.uuid, permissionGroupInfo)

        val durationLeftString = if (permissionGroupInfo.timeoutTimestamp == -1L) "permanently" else permissionGroupInfo.durationLeft().toComponents { days, hours, minutes, seconds, _ -> "${days}d, ${hours}h, ${minutes}m, ${seconds}s" }
        player.sendMessage(LineKey.key("permission.proxy.player.group.added"),
            Placeholder.parsed("group", permissionGroupInfo.name),
            Placeholder.parsed("player", onlinePlayer.name),
            Placeholder.parsed("duration", durationLeftString))

        NeptunCoreProvider.api.getPlayerAdapter().sendGlobalMessage(
            uuid = onlinePlayer.uuid,
            key = LineKey.key("permission.proxy.player.group.received"),
            toReplace = listOf(Pair("group", permissionGroupInfo.name), Pair("duration", durationLeftString))
        )
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSuggestions(args, 3, listOf(Pair(0, "player"))) { add("group") })
            addAll(generateSuggestions(args, 4, listOf(Pair(0, "player"), Pair(2, "group"))) { add("add") })
            addAll(generateSuggestions(args, 5, listOf(Pair(0, "player"), Pair(2, "group"), Pair(3, "add"))) { addAll(NeptunPermissionProvider.api.permissionGroupService.getPermissionGroups().map { it.name }) })
            addAll(generateSuggestions(args, 7, listOf(Pair(0, "player"), Pair(2, "group"), Pair(3, "add"))) { addAll(availableTimeUnits.map { it.name }) })
        }
    }

}