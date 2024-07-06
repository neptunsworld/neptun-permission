package world.neptuns.permission.watchdog

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import world.neptuns.permission.api.permission.PermissionInfo
import world.neptuns.permission.common.api.permission.PermissionInfoImpl

class PermissionInfoAdapter : TypeAdapter<PermissionInfo>() {
    override fun write(out: JsonWriter?, value: PermissionInfo?) {
        // Implementierung für das Schreiben, falls erforderlich
        // Normalerweise nicht notwendig für deserialisierungszwecke
    }

    override fun read(`in`: JsonReader?): PermissionInfo {
        `in`?.apply {
            // Lesen der JSON-Werte
            var name = ""
            var timeoutTimestamp = 0L

            beginObject()
            while (hasNext()) {
                when (nextName()) {
                    "name" -> name = nextString()
                    "timeoutTimestamp" -> timeoutTimestamp = nextLong()
                    else -> skipValue()
                }
            }
            endObject()

            // Rückgabe der Instanz von PermissionInfoImpl
            return PermissionInfoImpl(name, timeoutTimestamp)
        }

        throw IllegalStateException("Invalid JSON format for PermissionInfo")
    }
}
