package com.ruletadificultad;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

/**
 * Lista de cambios de dificultad. El número del menú (1, 2, 3...) corresponde al id de cada cambio.
 * Para agregar más, solo hay que añadir una línea nueva aquí y su lógica en {@link DificultadLogic}.
 */
public final class Dificultades {

    /**
     * @param id          número que se elige en el menú (1 a 21)
     * @param descripcion texto que se muestra en el menú
     * @param icono       ítem que se dibuja como ícono
     * @param tachar      si es true, se dibuja una X roja sobre el ícono (cambios en los que algo deja de funcionar)
     */
    public record Cambio(int id, String descripcion, Item icono, boolean tachar) {
    }

    public static final int TOTAL_NUMEROS = 21;

    public static final List<Cambio> CAMBIOS = List.of(
            // --- Los 5 primeros ---
            new Cambio(1, "las vacas ya no darán más leche", Items.MILK_BUCKET, true),
            new Cambio(2, "las gallinas ya no pondrán huevos", Items.EGG, true),
            new Cambio(3, "los creepers explotarán con el doble de radio", Items.GUNPOWDER, false),
            new Cambio(4, "la vida ya no se regenerará sola", Items.APPLE, true),
            new Cambio(5, "los monstruos serán 30% más rápidos", Items.ROTTEN_FLESH, false),
            // --- Los 10 extra ---
            new Cambio(6, "el hambre bajará mucho más rápido", Items.COOKED_BEEF, false),
            new Cambio(7, "ya no se podrá dormir en camas", Items.RED_BED, true),
            new Cambio(8, "siempre será de noche", Items.CLOCK, false),
            new Cambio(9, "siempre estará lloviendo", Items.WATER_BUCKET, false),
            new Cambio(10, "los jugadores tendrán fatiga de minado", Items.IRON_PICKAXE, false),
            new Cambio(11, "los jugadores serán más lentos", Items.COBWEB, false),
            new Cambio(12, "las ovejas ya no se podrán esquilar", Items.SHEARS, true),
            new Cambio(13, "los aldeanos ya no querrán comerciar", Items.EMERALD, true),
            new Cambio(14, "los jugadores tendrán 3 corazones menos", Items.POISONOUS_POTATO, false),
            new Cambio(15, "los jugadores saltarán mucho menos", Items.RABBIT_FOOT, false)
    );

    private Dificultades() {
    }

    /** Devuelve el cambio asignado a ese número o null si el número no tiene cambio. */
    public static Cambio obtener(int id) {
        if (id < 1 || id > CAMBIOS.size()) {
            return null;
        }
        return CAMBIOS.get(id - 1);
    }
}
