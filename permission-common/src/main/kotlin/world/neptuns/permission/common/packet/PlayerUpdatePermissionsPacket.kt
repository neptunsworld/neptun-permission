package world.neptuns.permission.common.packet

import world.neptuns.streamline.api.packet.NetworkChannelRegistry
import world.neptuns.streamline.api.packet.NetworkPacket
import java.util.*

data class PlayerUpdatePermissionsPacket(val uuid: UUID) : NetworkPacket(NetworkChannelRegistry.SERVICE)