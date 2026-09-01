/*
 * Copyright © Wynntils 2023-2026.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.mc.mixin;

import com.wynntils.core.events.MixinHelper;
import com.wynntils.mc.event.EntityNameTagRenderEvent;
import com.wynntils.mc.extension.EntityExtension;
import com.wynntils.mc.extension.EntityRenderStateExtension;
import com.wynntils.utils.colors.CustomColor;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    @Inject(
            method =
                    "extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V",
            at = @At("RETURN"))
    private void onExtractRenderState(T entity, S entityRenderState, float f, CallbackInfo ci) {
        if (entityRenderState instanceof EntityRenderStateExtension) {
            ((EntityRenderStateExtension) entityRenderState).setEntity(entity);
        }
    }

    @Inject(
            method =
                    "extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V",
            at = @At("TAIL"))
    private void onExtractRenderStatePost(Entity entity, EntityRenderState state, float partialTick, CallbackInfo ci) {
        EntityExtension entityExt = (EntityExtension) entity;

        if (entityExt.getGlowColor() != CustomColor.NONE) {
            state.outlineColor = entityExt.getGlowColorInt();
        }
    }

    @Inject(
            method =
                    "extractNameTags(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onNameTagSubmitPre(T entity, S renderState, float partialTick, CallbackInfo ci) {
        EntityNameTagRenderEvent event = new EntityNameTagRenderEvent(renderState);
        MixinHelper.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
