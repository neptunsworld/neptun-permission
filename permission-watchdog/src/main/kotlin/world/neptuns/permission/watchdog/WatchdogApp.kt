package world.neptuns.permission.watchdog

import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.Database
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.codec.SerializationCodec
import org.redisson.config.Config
import world.neptuns.permission.api.NeptunPermissionProvider
import world.neptuns.permission.common.PermissionApiImpl
import world.neptuns.permission.watchdog.validation.PermissionBundleValidator
import world.neptuns.permission.watchdog.validation.PermissionGroupValidator
import world.neptuns.permission.watchdog.validation.PermissionPlayerValidator
import world.neptuns.streamline.common.NeptunStreamlineConnector

@OptIn(DelicateCoroutinesApi::class)
class WatchdogApp {

    private val redissonClient: RedissonClient
    private val permissionApi: PermissionApiImpl
    private lateinit var job: Job

    private val permissionBundleValidator: PermissionBundleValidator
    private val permissionGroupValidator: PermissionGroupValidator
    private val permissionPlayerValidator: PermissionPlayerValidator

    init {
        this.redissonClient = establishDatabaseConnection()
        this.permissionApi = PermissionApiImpl()

        NeptunPermissionProvider.api = permissionApi

        runBlocking {
            permissionApi.loadContents()
        }

        this.permissionBundleValidator = PermissionBundleValidator(this.permissionApi.permissionBundleService, this.permissionApi.permissionBundleAdapter)
        this.permissionGroupValidator = PermissionGroupValidator(this.permissionApi.permissionGroupService, this.permissionApi.permissionGroupAdapter)
        this.permissionPlayerValidator = PermissionPlayerValidator(this.permissionApi.permissionPlayerService, this.permissionApi.permissionPlayerAdapter)
    }

    fun start() {
        this.job = GlobalScope.launch {
            while (isActive) {
                permissionBundleValidator.validatePermissionBundles()
                permissionGroupValidator.validatePermissionGroups()
                permissionPlayerValidator.validatePermissionPlayers()

                delay(5000L)
            }
        }
    }

    fun stop() {
        this.job.cancelChildren()
        this.job.cancel()
    }

    private fun establishDatabaseConnection(): RedissonClient {
        val redisHostname = "37.114.34.73"
        val redisPort = 6379
        val redisPassword = "FxEjrfDu3Z6wP9dza8AU5b"

        val config = Config()
        config.codec = SerializationCodec()
        config.nettyThreads = 4
        config.useSingleServer()
            .setAddress("redis://$redisHostname:$redisPort")
            .setPassword(redisPassword)

        val redissonClient = Redisson.create(config)

        val password = "K{T4?f,AmV${"$"}${"p"}/U3Q+_%g^@"

        Database.connect(
            url = "jdbc:mariadb://37.114.34.73:3306/neptunsworld_core",
            driver = "org.mariadb.jdbc.Driver",
            user = "root",
            password = password
        )

        NeptunStreamlineConnector.init(redissonClient)

        return redissonClient
    }

}