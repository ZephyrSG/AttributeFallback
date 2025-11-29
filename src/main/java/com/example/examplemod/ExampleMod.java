package com.example.attributefallback;

import com.mojang.logging.LogUtils;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

import java.util.UUID;

@Mod(AttributeFallbackMod.MOD_ID)
public class AttributeFallbackMod {

    public static final String MOD_ID = "attribute_fallback";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AttributeFallbackMod() {
        LOGGER.info("[AttributeFallback] Loaded.");
    }

    @SubscribeEvent
    public void onItemRegistry(RegisterEvent event) {

        if (!event.getRegistryKey().equals(net.minecraft.core.registries.Registries.ITEM)) {
            return;
        }

        event.getRegistry().forEach(entry -> {
            Item item = entry.getValue();

            DataComponentMap comps = item.components();

            ItemAttributeModifiers existing = comps.get(DataComponentType.ATTRIBUTE_MODIFIERS);

            // If already valid, skip
            if (existing != null && !existing.modifiers().isEmpty()) {
                return;
            }

            LOGGER.warn("[AttributeFallback] '{}' missing attribute component — patching.", entry.getKey());

            // Build a safe fallback modifiers object
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

            builder.add(
                    Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(
                            UUID.nameUUIDFromBytes(entry.getKey().toString().getBytes()),
                            "fallback_zero",
                            0D,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.ANY
            );

            ItemAttributeModifiers fallback = builder.build();

            // Replace component safely
            DataComponentMap patched = comps.with(DataComponentTypes.ATTRIBUTE_MODIFIERS, fallback);

            // Apply to item instance
            item.updateComponents(patched);

            LOGGER.info("[AttributeFallback] Patched '{}'.", entry.getKey());
        });
    }
}