package world.neptuns.permission.velocity.command

import world.neptuns.core.base.api.command.NeptunCommand
import world.neptuns.core.base.api.command.NeptunCommandPlatform
import world.neptuns.core.base.api.command.NeptunCommandSender
import world.neptuns.core.base.api.command.NeptunMainCommandExecutor
import world.neptuns.core.base.api.command.extension.generateSimpleSuggestions
import world.neptuns.core.base.api.command.extension.generateSuggestions
import world.neptuns.core.base.api.command.extension.sendUsageFormatted
import world.neptuns.core.base.api.command.subcommand.NeptunSubCommandExecutor
import world.neptuns.permission.velocity.command.bundle.*
import world.neptuns.permission.velocity.command.group.*

@NeptunCommand(platform = NeptunCommandPlatform.VELOCITY, name = "perms", title = "Permission System", permission = "neptun.perms")
class PermsCommand : NeptunMainCommandExecutor() {

    override suspend fun defaultExecute(sender: NeptunCommandSender) {
        sendUsageFormatted(sender)
    }

    override fun initSubCommands(subCommandExecutors: MutableList<NeptunSubCommandExecutor>) {
        subCommandExecutors.add(PermsBundleListSubCommand())
        subCommandExecutors.add(PermsBundleCreateSubCommand())
        subCommandExecutors.add(PermsBundleDeleteSubCommand())
        subCommandExecutors.add(PermsBundleListSubCommand())
        subCommandExecutors.add(PermsBundlePermListSubCommand())
        subCommandExecutors.add(PermsBundlePermAddSubCommand())
        subCommandExecutors.add(PermsBundlePermRemoveSubCommand())

        subCommandExecutors.add(PermsGroupListSubCommand())
        subCommandExecutors.add(PermsGroupInfoSubCommand())
        subCommandExecutors.add(PermsGroupCreateSubCommand())
        subCommandExecutors.add(PermsGroupDeleteSubCommand())
        subCommandExecutors.add(PermsGroupSetDefaultSubCommand())

        subCommandExecutors.add(PermsGroupPermListSubCommand())
        subCommandExecutors.add(PermsGroupPermAddSubCommand())
        subCommandExecutors.add(PermsGroupPermRemoveSubCommand())

        subCommandExecutors.add(PermsGroupParentListSubCommand())
        subCommandExecutors.add(PermsGroupParentAddSubCommand())
        subCommandExecutors.add(PermsGroupParentRemoveSubCommand())

        subCommandExecutors.add(PermsGroupBundleListSubCommand())
        subCommandExecutors.add(PermsGroupBundleAddSubCommand())
        subCommandExecutors.add(PermsGroupBundleRemoveSubCommand())

        subCommandExecutors.add(PermsGroupMetadataList())
        subCommandExecutors.add(PermsGroupMetadataSetSubCommand())
        subCommandExecutors.add(PermsGroupMetadataRemoveSubCommand())
    }

    override suspend fun onDefaultTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        return buildList {
            addAll(generateSimpleSuggestions(args, 0) { add("bundles") })
            addAll(generateSuggestions(args, 1) { add("bundles") })

            addAll(generateSimpleSuggestions(args, 0) { add("bundle") })
            addAll(generateSuggestions(args, 1) { add("bundle") })

            addAll(generateSimpleSuggestions(args, 0) { add("groups") })
            addAll(generateSuggestions(args, 1) { add("groups") })

            addAll(generateSimpleSuggestions(args, 0) { add("group") })
            addAll(generateSuggestions(args, 1) { add("group") })
        }
    }

}