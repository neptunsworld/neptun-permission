package world.neptuns.permission.api.utils

interface Expirable {

    val timeoutTimestamp: Long

    fun isExpired() = (System.currentTimeMillis() > this.timeoutTimestamp) && this.timeoutTimestamp != -1L

}