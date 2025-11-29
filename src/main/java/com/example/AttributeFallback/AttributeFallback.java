package com.example.attributefallback;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Mod(AttributeFallbackMod.MOD_ID)
public class AttributeFallbackMod {

    public static final String MOD_ID = "attribute_fallback";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AttributeFallbackMod() {
        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("[AttributeFallback] Mod loaded.");
    }

    @SubscribeEvent
    public void onItemAttributeModifiers(ItemAttributeModifierEvent event) {

        List<ItemAttributeModifiers.Entry> mods = event.getModifiers();

        // If the item already has modifiers, do nothing.
        if (mods != null && !mods.isEmpty()) {
            LOGGER.debug("[AttributeFallback] Existing modifiers found, skipping.");
            return;
        }

        ItemStack stack = event.getItemStack();
        String itemId = stack.getItem().toString();

        LOGGER.warn("[AttributeFallback] NULL/empty attributes on {} — injecting fallback.", itemId);

        // Stable UUID so modifier doesn't duplicate
        UUID uuid = UUID.nameUUIDFromBytes(
                ("attribute_fallback:" + itemId).getBytes(StandardCharsets.UTF_8)
        );

        AttributeModifier fallback = new AttributeModifier(
                ResourceLocation.withDefaultNamespace("attribute_fallback_zero"),
                0.0D,
                AttributeModifier.Operation.ADD_VALUE
        );

        // Inject the fallback attack damage modifier into ANY slot.
        event.addModifier(Attributes.ATTACK_DAMAGE, fallback, EquipmentSlotGroup.ANY);

        LOGGER.info("[AttributeFallback] Injected safe fallback modifier for {}", itemId);
    }
}