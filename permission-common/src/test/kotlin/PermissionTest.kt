import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.format.NamedTextColor
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.codec.SerializationCodec
import org.redisson.config.Config
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.api.PermissionApi
import world.neptuns.permission.api.group.PermissionGroup
import world.neptuns.permission.common.PermissionApiImpl
import world.neptuns.permission.common.api.permission.PermissionInfoImpl
import world.neptuns.permission.common.repository.PermissionBundleRepository
import world.neptuns.permission.common.repository.PermissionGroupRepository
import world.neptuns.permission.common.repository.PermissionPlayerRepository
import world.neptuns.streamline.api.NeptunStreamlineProvider
import world.neptuns.streamline.common.NeptunStreamlineConnector
import java.util.*
import kotlin.test.Test

object PermissionTest {

    private lateinit var redissonClient: RedissonClient

    @BeforeEach
    fun init() = runBlocking {
        val redisHostname = "37.114.34.73"
        val redisPort = 6379
        val redisPassword = "FxEjrfDu3Z6wP9dza8AU5b"

        val config = Config()
        config.codec = SerializationCodec()
        config.nettyThreads = 4
        config.useSingleServer()
            .setAddress("redis://$redisHostname:$redisPort")
            .setPassword(redisPassword)

        redissonClient = Redisson.create(config)

        val password = "K{T4?f,AmV${"$"}${"p"}/U3Q+_%g^@"

        Database.connect(
            url = "jdbc:mariadb://37.114.34.73:3306/neptunsworld_core",
            driver = "org.mariadb.jdbc.Driver",
            user = "root",
            password = password
        )

        NeptunStreamlineConnector.init(redissonClient)
        val api = PermissionApiImpl()
        NeptunPermissionProvider.api = api

        api.loadContents()
    }

    // @Test
    fun createBundle() = runBlocking {
        NeptunPermissionProvider.api.permissionBundleService.createPermissionBundle(
            name = "moderation_bundle",
            isAuthRequired = true,
        )

        delay(1000L)

        NeptunPermissionProvider.api.permissionBundleAdapter.addPermission(
            bundleName = "moderation_bundle",
            permission = PermissionInfoImpl("neptun.ban", -1L)
        )

        NeptunPermissionProvider.api.permissionBundleAdapter.addPermission(
            bundleName = "moderation_bundle",
            permission = PermissionInfoImpl("neptun.kick", -1L)
        )

        NeptunPermissionProvider.api.permissionBundleAdapter.addPermission(
            bundleName = "moderation_bundle",
            permission = PermissionInfoImpl("neptun.tempban", -1L)
        )
    }

    @Test
    fun testPermissionPlayer() = runBlocking {
        val playerUuid = UUID.fromString("7bcaba2a-0fdf-49ab-b235-e5821d04c367")

        val permissionPlayer = NeptunPermissionProvider.api.permissionPlayerService.loadPermissionPlayer(playerUuid).await()
            ?: return@runBlocking

        println("Found permission player ${permissionPlayer.uuid}")

        val playerAdapter = NeptunPermissionProvider.api.permissionPlayerAdapter

        val adminGroup = NeptunPermissionProvider.api.permissionGroupService.getPermissionGroup("player")
            ?: return@runBlocking

        val groupInfo = adminGroup.toGroupInfo(-1L)

        playerAdapter.addPermissionGroup(playerUuid, groupInfo)

        assertTrue(true)
    }

    // @Test
    fun createGroups() = runBlocking {
        val groupService = NeptunPermissionProvider.api.permissionGroupService

        groupService.createPermissionGroup(
            name = "admin",
            priority = 0,
            color = NamedTextColor.RED,
            format = PermissionApi.defaultGroupFormat,
            permissions = mutableSetOf(
                PermissionInfoImpl("minecraft.command.gamemode", PermissionApi.PERMANENT_DURATION)
            ),
            permissionBundles = mutableSetOf(),
            parents = mutableSetOf(),
            isDefault = false
        )

        groupService.createPermissionGroup(
            name = "player",
            priority = 99,
            color = NamedTextColor.GRAY,
            format = PermissionApi.defaultGroupFormat,
            permissions = mutableSetOf(
                PermissionInfoImpl("test.player", PermissionApi.PERMANENT_DURATION)
            ),
            permissionBundles = mutableSetOf(),
            parents = mutableSetOf(),
            isDefault = true
        )

        assertTrue(true)
    }

    // @Test
    fun addParentGroupToAdmin() = runBlocking {
        val groupService = NeptunPermissionProvider.api.permissionGroupService

        groupService.bulkUpdateEntry(
            updateType = PermissionGroup.Update.PARENT_ADD,
            key = "admin",
            newValue = "player",
            updateCache = true
        )

        assertTrue(true)
    }

    // @Test
    fun checkPermissions() = runBlocking {
        val adminGroup = NeptunPermissionProvider.api.permissionGroupService.getPermissionGroup("admin")
            ?: throw NullPointerException("No admin group found!")

        println("All permissions of group admin:")

        for (permissionInfo in adminGroup.collectAllPermissions()) {
            println("- (permission: ${permissionInfo.name}, timeoutTimestamp: ${permissionInfo.timeoutTimestamp})")
        }
    }

    @AfterEach
    fun clearPermissionGroups() = runBlocking {
        val groupRepository = NeptunStreamlineProvider.api.repositoryLoader.get(PermissionGroupRepository::class.java)!!
        val groups = ArrayList(groupRepository.getAll().await())

        for (permissionGroup in groups) {
            groupRepository.delete(permissionGroup.name)
        }

        val playerRepository = NeptunStreamlineProvider.api.repositoryLoader.get(PermissionPlayerRepository::class.java)!!
        val players = ArrayList(playerRepository.getAll().await())

        for (permissionPlayer in players) {
            playerRepository.delete(permissionPlayer.uuid)
        }

        val bundleRepository = NeptunStreamlineProvider.api.repositoryLoader.get(PermissionBundleRepository::class.java)!!
        val bundles = ArrayList(bundleRepository.getAll().await())

        for (permissionBundle in bundles) {
            bundleRepository.delete(permissionBundle.name)
        }
    }

}