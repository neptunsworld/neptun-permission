package world.neptuns.permission.bukkit.util

import org.bukkit.entity.Player
import org.bukkit.permissions.PermissibleBase
import world.neptuns.permission.bukkit.permissible.BukkitPermissible
import world.neptuns.permission.bukkit.permissible.DummyPermissibleBase
import java.lang.reflect.Field

object PermissibleInjector {

    private var HUMAN_ENTITY_PERMISSIBLE_FIELD: Field
    private var PERMISSIBLE_BASE_ATTACHMENTS_FIELD: Field

    init {
        try {
            HUMAN_ENTITY_PERMISSIBLE_FIELD = CraftBukkitImplementation.obcClass("entity.CraftHumanEntity").getDeclaredField("perm")
            HUMAN_ENTITY_PERMISSIBLE_FIELD.isAccessible = true

            PERMISSIBLE_BASE_ATTACHMENTS_FIELD = PermissibleBase::class.java.getDeclaredField("attachments")
            PERMISSIBLE_BASE_ATTACHMENTS_FIELD.setAccessible(true)
        } catch (e: ClassNotFoundException) {
            throw ExceptionInInitializerError(e)
        } catch (e: NoSuchFieldException) {
            throw ExceptionInInitializerError(e)
        }
    }

    @Throws(Exception::class)
    fun inject(player: Player, newPermissible: BukkitPermissible) {
        val oldPermissible = HUMAN_ENTITY_PERMISSIBLE_FIELD[player] as PermissibleBase

        check(oldPermissible !is BukkitPermissible) { "BukkitPermissible already injected into player $player" }

        val oldClass: Class<out PermissibleBase> = oldPermissible.javaClass

        if (PermissibleBase::class.java != oldClass) {
            println(
                """
                    Player ${player.name} already has a custom permissible (${oldClass.name})!
                    This is probably because you have multiple permission plugins installed.
                """
            )
        }

        val attachments = PERMISSIBLE_BASE_ATTACHMENTS_FIELD[oldPermissible] as MutableList<*>

        attachments.clear()
        oldPermissible.clearPermissions()

        newPermissible.active.set(true)
        newPermissible.oldPermissible = oldPermissible

        HUMAN_ENTITY_PERMISSIBLE_FIELD[player] = newPermissible
    }

    @Throws(Exception::class)
    fun uninject(player: Player, dummy: Boolean) {
        val permissible = HUMAN_ENTITY_PERMISSIBLE_FIELD[player] as PermissibleBase

        if (permissible is BukkitPermissible) {
            val lpPermissible: BukkitPermissible = permissible

            lpPermissible.clearPermissions()
            lpPermissible.active.set(false)

            if (dummy) {
                HUMAN_ENTITY_PERMISSIBLE_FIELD[player] = DummyPermissibleBase.INSTANCE
            } else {
                var newPb: PermissibleBase? = lpPermissible.oldPermissible
                if (newPb == null) {
                    newPb = PermissibleBase(player)
                }

                HUMAN_ENTITY_PERMISSIBLE_FIELD[player] = newPb
            }
        }
    }

}