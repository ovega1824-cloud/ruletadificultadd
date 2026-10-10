package com.ruletadificultad;

import java.util.List;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

/** Aquí está lo que hace cada cambio de dificultad. Todo se activa sin mostrar mensajes. */
public final class DificultadLogic {

    private static final Identifier MONSTRUOS_RAPIDOS = Identifier.of(RuletaDificultadMod.MOD_ID, "monstruos_rapidos");
    private static final Identifier JUGADOR_LENTO = Identifier.of(RuletaDificultadMod.MOD_ID, "jugador_lento");
    private static final Identifier JUGADOR_VIDA = Identifier.of(RuletaDificultadMod.MOD_ID, "jugador_vida");
    private static final Identifier JUGADOR_SALTO = Identifier.of(RuletaDificultadMod.MOD_ID, "jugador_salto");

    private DificultadLogic() {
    }

    public static void registrar() {
        // 1, 12 y 13: acciones sobre entidades (leche, esquilar, comerciar)
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient || !(world instanceof ServerWorld serverWorld)) {
                return ActionResult.PASS;
            }
            DificultadState state = DificultadState.get(serverWorld.getServer());
            ItemStack held = player.getStackInHand(hand);

            if (state.isEnabled(1) && entity instanceof CowEntity
                    && (held.isOf(Items.BUCKET) || held.isOf(Items.BOWL))) {
                return ActionResult.FAIL;
            }
            if (state.isEnabled(12) && entity instanceof SheepEntity && held.isOf(Items.SHEARS)) {
                return ActionResult.FAIL;
            }
            if (state.isEnabled(13) && entity instanceof VillagerEntity) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // 7: no se puede dormir
        EntitySleepEvents.ALLOW_SLEEPING.register((player, sleepingPos) -> {
            if (player instanceof ServerPlayerEntity serverPlayer
                    && DificultadState.get(serverPlayer.getServer()).isEnabled(7)) {
                return PlayerEntity.SleepFailureReason.OTHER_PROBLEM;
            }
            return null;
        });

        // Al iniciar el servidor se vuelve a aplicar la regla de regeneración si estaba activada
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (DificultadState.get(server).isEnabled(4)) {
                aplicarAlCambiar(server, 4, true);
            }
        });

        // El resto se revisa una vez por segundo
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % 20 != 0) {
                return;
            }
            tickPorSegundo(server);
        });
    }

    /** Cosas que deben pasar justo al activar o desactivar un cambio. */
    public static void aplicarAlCambiar(MinecraftServer server, int id, boolean activado) {
        if (id == 4) {
            // 4: regeneración natural de vida
            server.getGameRules().get(GameRules.NATURAL_REGENERATION).set(!activado, server);
        }
    }

    private static void tickPorSegundo(MinecraftServer server) {
        DificultadState state = DificultadState.get(server);

        for (ServerWorld world : server.getWorlds()) {
            // 2: las gallinas no ponen huevos
            if (state.isEnabled(2)) {
                for (ChickenEntity chicken : world.getEntitiesByType(EntityType.CHICKEN, e -> true)) {
                    chicken.eggLayTime = 6000;
                }
            }

            // 3: creepers con el doble de radio de explosión
            List<? extends CreeperEntity> creepers = world.getEntitiesByType(EntityType.CREEPER, e -> true);
            int radio = state.isEnabled(3) ? 6 : 3;
            for (CreeperEntity creeper : creepers) {
                if (creeper.explosionRadius != radio) {
                    creeper.explosionRadius = radio;
                }
            }

            // 5: monstruos 30% más rápidos
            List<? extends HostileEntity> monstruos =
                    world.getEntitiesByType(TypeFilter.instanceOf(HostileEntity.class), e -> true);
            for (HostileEntity monstruo : monstruos) {
                modificador(monstruo, EntityAttributes.GENERIC_MOVEMENT_SPEED, MONSTRUOS_RAPIDOS,
                        0.3, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, state.isEnabled(5));
            }

            if (world.getRegistryKey() == World.OVERWORLD) {
                // 8: siempre de noche
                if (state.isEnabled(8)) {
                    world.setTimeOfDay(18000L);
                }
                // 9: siempre lloviendo
                if (state.isEnabled(9) && !world.isRaining()) {
                    world.setWeather(0, 6000, true, false);
                }
            }
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            // 6: más hambre
            if (state.isEnabled(6) && !player.isCreative() && !player.isSpectator()) {
                player.getHungerManager().addExhaustion(0.2f);
            }

            // 10: fatiga de minado
            if (state.isEnabled(10)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 60, 0, true, false));
            }

            // 11: jugadores más lentos
            modificador(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, JUGADOR_LENTO,
                    -0.25, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, state.isEnabled(11));

            // 14: 3 corazones menos (6 puntos de vida)
            modificador(player, EntityAttributes.GENERIC_MAX_HEALTH, JUGADOR_VIDA,
                    -6.0, EntityAttributeModifier.Operation.ADD_VALUE, state.isEnabled(14));
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }

            // 15: saltan mucho menos
            modificador(player, EntityAttributes.GENERIC_JUMP_STRENGTH, JUGADOR_SALTO,
                    -0.4, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, state.isEnabled(15));
        }
    }

    /** Pone o quita un modificador de atributo temporal según esté activado el cambio. */
    private static void modificador(LivingEntity entity, RegistryEntry<EntityAttribute> atributo, Identifier id,
                                    double valor, EntityAttributeModifier.Operation operacion, boolean activo) {
        EntityAttributeInstance instancia = entity.getAttributeInstance(atributo);
        if (instancia == null) {
            return;
        }
        if (activo) {
            if (!instancia.hasModifier(id)) {
                instancia.addTemporaryModifier(new EntityAttributeModifier(id, valor, operacion));
            }
        } else if (instancia.hasModifier(id)) {
            instancia.removeModifier(id);
        }
    }
}
