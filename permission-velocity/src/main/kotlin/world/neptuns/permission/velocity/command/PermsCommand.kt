package world.neptuns.permission.velocity.command

import com.velocitypowered.api.proxy.Player
import world.neptuns.core.base.api.NeptunCoreProvider
import world.neptuns.core.base.api.command.NeptunCommand
import world.neptuns.core.base.api.command.NeptunCommandExecutor
import world.neptuns.core.base.api.command.NeptunCommandPlatform
import world.neptuns.core.base.api.command.NeptunCommandSender

@NeptunCommand(type = NeptunCommandPlatform.VELOCITY, name = "perms", permission = "neptun.perms")
class PermsCommand(channelName: String) : NeptunCommandExecutor {

    private val playerAdapter = NeptunCoreProvider.api.getPlayerAdapter(Player::class.java)

    override suspend fun execute(sender: NeptunCommandSender, args: List<String>) {
        if (!sender.isPlayer()) return
        val player = sender.castTo(Player::class.java)

    }

    override fun sendUsage(sender: NeptunCommandSender) {
        // /perms
    }

    override suspend fun onTabComplete(sender: NeptunCommandSender, args: List<String>): List<String> {
        TODO("Not yet implemented")
    }

}