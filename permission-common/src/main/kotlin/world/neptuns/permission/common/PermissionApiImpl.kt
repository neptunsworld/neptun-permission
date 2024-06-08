package world.neptuns.permission.common

import world.neptuns.permission.api.PermissionApi
import world.neptuns.permission.api.group.PermissionGroupController
import world.neptuns.permission.common.api.group.PermissionGroupControllerImpl
import world.neptuns.permission.common.repository.PermissionGroupCache
import world.neptuns.permission.common.repository.PermissionGroupRepository
import world.neptuns.streamline.api.NeptunStreamlineProvider

class PermissionApiImpl : PermissionApi {

    override val groupController: PermissionGroupController = PermissionGroupControllerImpl("network:message-service:permission_group_updates")

    init {
        val redissonClient = NeptunStreamlineProvider.api.redissonClient

        val repositoryLoader = NeptunStreamlineProvider.api.repositoryLoader
        repositoryLoader.register(PermissionGroupRepository(redissonClient))

        val cacheLoader = NeptunStreamlineProvider.api.cacheLoader
        cacheLoader.register(PermissionGroupCache())
    }

}