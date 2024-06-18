package world.neptuns.permission.api.utils

import world.neptuns.permission.api.PermissionApi
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface Expirable {

    val timeoutTimestamp: Long

    fun isExpired() = (System.currentTimeMillis() > this.timeoutTimestamp) && this.timeoutTimestamp != PermissionApi.PERMANENT_DURATION

    fun durationLeft() = (this.timeoutTimestamp - System.currentTimeMillis()).toDuration(DurationUnit.MILLISECONDS)

}