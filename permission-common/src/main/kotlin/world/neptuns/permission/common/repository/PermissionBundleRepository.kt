package world.neptuns.permission.common.repository

import org.redisson.api.RedissonClient
import world.neptuns.permission.api.permission.PermissionBundle
import world.neptuns.streamline.api.cache.MapCache
import world.neptuns.streamline.api.repository.RMapRepository

class PermissionBundleRepository(redissonClient: RedissonClient) : RMapRepository<String, PermissionBundle>(redissonClient, "permission_bundles")
class PermissionBundleCache : MapCache<String, PermissionBundle>()