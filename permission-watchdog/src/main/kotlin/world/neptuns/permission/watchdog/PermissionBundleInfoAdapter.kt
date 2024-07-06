package world.neptuns.permission.watchdog

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.coroutines.runBlocking
import world.neptuns.permission.api.permission.PermissionBundleInfo
import world.neptuns.permission.common.api.permission.PermissionBundleInfoImpl

class PermissionBundleInfoAdapter : TypeAdapter<PermissionBundleInfo>() {
    override fun write(out: JsonWriter?, value: PermissionBundleInfo?) {
        // Implementierung für das Schreiben, falls erforderlich
        // Normalerweise nicht notwendig für Deserialisierungszwecke
    }

    override fun read(`in`: JsonReader?): PermissionBundleInfo {
        `in`?.apply {
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

            // Hier könnten Sie `NeptunPermissionProvider` oder andere Ressourcen verwenden, um eine Instanz von PermissionBundleInfoImpl zu erstellen
            return runBlocking {
                //val bundle = NeptunPermissionProvider.api.permissionBundleService.getPermissionBundle(name)
                PermissionBundleInfoImpl(name, timeoutTimestamp) // Annahme: PermissionBundleInfoImpl ist die konkrete Implementierung
            }
        }

        throw IllegalStateException("Invalid JSON format for PermissionBundleInfo")
    }
}
