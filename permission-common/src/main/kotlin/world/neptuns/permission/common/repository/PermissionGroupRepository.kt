package world.neptuns.permission.common.repository

import org.redisson.api.RedissonClient
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.streamline.api.repository.RMapRepository

class PermissionGroupRepository(redissonClient: RedissonClient) : RMapRepository<String, PermissionGroup>(redissonClient, "permission_groups")