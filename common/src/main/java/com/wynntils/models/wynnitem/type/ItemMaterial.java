/*
 * Copyright © Wynntils 2023-2026.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.models.wynnitem.type;

import com.wynntils.core.components.Models;
import com.wynntils.models.gear.type.GearType;
import com.wynntils.utils.mc.SkinUtils;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.datafix.fixes.ItemIdFix;
import net.minecraft.util.datafix.fixes.ItemStackTheFlatteningFix;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;

/**
 * From 26.2 an ItemStack cannot be built until Minecraft has bound item components, which happens
 * after Wynntils' entrypoint has already started downloading and parsing the item databases. The
 * backing item is resolved eagerly, so parsing can still tell a real material from a missing one,
 * but the stack itself is only created on first use, which is always render time.
 */
public final class ItemMaterial {
    private final Item item;
    private final Supplier<ItemStack> itemStackFactory;

    private volatile ItemStack itemStack;

    private ItemMaterial(Item item, Supplier<ItemStack> itemStackFactory) {
        this.item = item;
        this.itemStackFactory = itemStackFactory;
    }

    public static ItemMaterial getDefaultTomeItemMaterial() {
        return withModel(Items.ENCHANTED_BOOK, 0);
    }

    public static ItemMaterial getDefaultCharmItemMaterial() {
        // All charms are different items, this is as good as any other item
        return withModel(Items.CLAY, 0);
    }

    public static ItemMaterial fromPlayerHeadUUID(String uuid) {
        return new ItemMaterial(Items.PLAYER_HEAD, () -> {
            ItemStack itemStack = createItemStack(Items.PLAYER_HEAD, 0);
            SkinUtils.setPlayerHeadFromUUID(itemStack, uuid);

            return itemStack;
        });
    }

    public static ItemMaterial fromGearType(GearType gearType) {
        // Material is missing, so just give generic icon for this type of gear (weapon or accessory)
        return withModel(gearType.getDefaultItem(), gearType.getDefaultModel());
    }

    public static ItemMaterial fromItemId(String itemId, int customModelData) {
        return withModel(getItem(itemId), customModelData);
    }

    public static ItemMaterial fromItemTypeCode(int itemTypeCode, int damageCode) {
        String itemId;

        Optional<String> materialNameOverrideOpt = Models.WynnItem.getMaterialName(itemTypeCode, damageCode);
        if (materialNameOverrideOpt.isPresent()) {
            // The vanilla lookup fails for a handful of items, so we have a correctional data set
            itemId = "minecraft:" + materialNameOverrideOpt.get();
        } else {
            // Use normal vanilla lookup
            String toIdString = ItemIdFix.getItem(itemTypeCode);
            String alternativeName = ItemStackTheFlatteningFix.updateItem(toIdString, damageCode);
            itemId = alternativeName != null ? alternativeName : toIdString;
        }

        Item item = getItem(itemId);
        return new ItemMaterial(item, () -> createItemStackFromDamage(item, damageCode));
    }

    public ItemStack itemStack() {
        ItemStack cached = itemStack;
        if (cached == null) {
            cached = itemStackFactory.get();
            itemStack = cached;
        }

        return cached;
    }

    /** True when the icon could not be resolved to a real item, so this material is a placeholder. */
    public boolean isEmpty() {
        return item == Items.AIR;
    }

    private static ItemMaterial withModel(Item item, float modelValue) {
        return new ItemMaterial(item, () -> createItemStack(item, modelValue));
    }

    private static ItemStack createItemStack(Item item, float modelValue) {
        ItemStack itemStack = new ItemStack(item);

        CustomModelData customModelData = new CustomModelData(List.of(modelValue), List.of(), List.of(), List.of());
        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
        itemStack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        return itemStack;
    }

    private static ItemStack createItemStackFromDamage(Item item, int damageValue) {
        ItemStack itemStack = new ItemStack(item);

        itemStack.set(DataComponents.DAMAGE, damageValue);
        itemStack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        return itemStack;
    }

    private static Item getItem(String itemId) {
        return BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId));
    }
}
