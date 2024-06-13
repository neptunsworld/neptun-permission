package world.neptuns.permission.common.repository

import org.redisson.api.RedissonClient
import world.neptuns.permission.api.player.PermissionPlayer
import world.neptuns.streamline.api.cache.MapCache
import world.neptuns.streamline.api.repository.RMapRepository
import java.util.*

class PermissionPlayerRepository(redissonClient: RedissonClient) : RMapRepository<UUID, PermissionPlayer>(redissonClient, "permission_players")
class PermissionPlayerCache : MapCache<UUID, PermissionPlayer>()