/*
 * Copyright © Wynntils 2025-2026.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.mc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.events.MixinHelper;
import com.wynntils.mc.event.NametagBackgroundOpacityEvent;
import com.wynntils.mc.event.NametagScaleEvent;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/*
 * NOT REGISTERED in wynntils.mixins.json on 26.2.
 *
 * NameTagFeatureRenderer.Storage became the Submit record and its add(...) method is gone;
 * nametag drawing moved into buildGroup/prepareText, so neither the background opacity nor the
 * scale call site this hooked still exists. The nametag background opacity and scale options are
 * therefore inert until this is reworked against the new feature renderer flow.
 */
@Mixin(NameTagFeatureRenderer.Submit.class)
public class NameTagFeatureRendererStorageMixin {
    @ModifyArg(
            method =
                    "add(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getBackgroundOpacity(F)F"))
    private float onNametagOpacityGet(float backgroundOpacity) {
        NametagBackgroundOpacityEvent event = new NametagBackgroundOpacityEvent(backgroundOpacity);
        MixinHelper.post(event);

        return event.getOpacity();
    }

    @WrapOperation(
            method =
                    "add(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
    private void modifyNametagScale(PoseStack poseStack, float x, float y, float z, Operation<Void> original) {
        NametagScaleEvent event = new NametagScaleEvent();
        MixinHelper.post(event);

        original.call(poseStack, x * event.getScale(), y * event.getScale(), z * event.getScale());
    }
}
