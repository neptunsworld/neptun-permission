package world.neptuns.permission.common.repository

import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.streamline.api.cache.MapCache

class PermissionGroupCache : MapCache<String, PermissionGroup>()