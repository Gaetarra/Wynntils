/*
 * Copyright © Wynntils 2026.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.mc.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.wynntils.mc.extension.MinecraftExtension;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @WrapMethod(method = "mainRenderTarget()Lcom/mojang/blaze3d/pipeline/RenderTarget;")
    private RenderTarget mainRenderTarget(Operation<RenderTarget> operation) {
        RenderTarget overriden = ((MinecraftExtension) McUtils.mc()).getOverridenRenderTarget();
        if (overriden != null) {
            return overriden;
        }

        return operation.call();
    }
}
