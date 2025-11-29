package com.example.AttributeFallback;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Mod(AttributeFallbackMod.MOD_ID)
public class AttributeFallbackMod {

    public static final String MOD_ID = "attribute_fallback";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AttributeFallbackMod() {
        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("[AttributeFallback] Mod '{}' initialized.", MOD_ID);
    }

    @SubscribeEvent
    public void onItemAttributeModifiers(ItemAttributeModifierEvent event) {

        List<ItemAttributeModifiers.Entry> mods = event.getModifiers();

        // Skip items with existing modifiers
        if (mods != null && !mods.isEmpty()) {
            LOGGER.debug("[AttributeFallback] Existing modifiers found, skipping.");
            return;
        }

        ItemStack stack = event.getItemStack();
        String itemId = stack.getItem().toString();

        LOGGER.warn("[AttributeFallback] Null/empty attribute modifiers detected for {} — applying fallback.", itemId);

        // Stable UUID
        UUID uuid = UUID.nameUUIDFromBytes(
                ("attribute_fallback:" + itemId).getBytes(StandardCharsets.UTF_8)
        );

        AttributeModifier zero = new AttributeModifier(
                ResourceLocation.withDefaultNamespace("attribute_fallback_zero"),
                0.0D,
                AttributeModifier.Operation.ADD_VALUE
        );

        // Add fallback modifier
        event.addModifier(Attributes.ATTACK_DAMAGE, zero, EquipmentSlotGroup.ANY);

        LOGGER.info("[AttributeFallback] Injected fallback modifier into {}", itemId);
    }
}
