package world.neptuns.permission.velocity.command

import world.neptuns.core.base.api.command.NeptunCommand
import world.neptuns.core.base.api.command.NeptunCommandPlatform
import world.neptuns.core.base.api.command.NeptunCommandSender
import world.neptuns.core.base.api.command.NeptunMainCommandExecutor
import world.neptuns.core.base.api.command.extension.generateSimpleSuggestions
import world.neptuns.core.base.api.command.extension.generateSuggestions
import world.neptuns.core.base.api.command.extension.sendUsageFormatted
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommandExecutor

@NeptunCommand(platform = NeptunCommandPlatform.VELOCITY, name = "perms", title = "Permission System", permission = "neptun.perms")
class PermsCommand : NeptunMainCommandExecutor() {

    override suspend fun defaultExecute(sender: NeptunCommandSender) {
        sendUsageFormatted(sender)
    }

    override fun initSubCommands(subCommandExecutors: MutableList<NeptunSubCommandExecutor>) {
        subCommandExecutors.add(PermsGroupListSubCommand())
        subCommandExecutors.add(PermsGroupCreateSubCommand())
        subCommandExecutors.add(PermsGroupDeleteSubCommand())
        subCommandExecutors.add(PermsGroupSetDefaultSubCommand())
        subCommandExecutors.add(PermsGroupPermListSubCommand())
    }

    override suspend fun onDefaultTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSimpleSuggestions(args, 0) { add("groups") })
            addAll(generateSuggestions(args, 1) { add("groups") })
        }
    }

}