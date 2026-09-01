/*
 * Copyright © Wynntils 2025-2026.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.utils.render.pipelines;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;

/**
 * 26.2 replaced the pipeline builder with a Vulkan-style model: blending moved into
 * ColorTargetState, samplers into BindGroupLayout, and the MATRICES_PROJECTION_SNIPPET these
 * pipelines were built on is gone. Rebuilding the custom pipelines needs matching shader
 * binding declarations, so they currently alias the closest vanilla pipelines instead.
 *
 * Known ceiling: blending and depth behaviour are vanilla's, not Wynntils' own. Restore custom
 * pipelines once the new shader binding model is wired up.
 */
public class CustomRenderPipelines extends RenderPipelines {
    public static final RenderPipeline LOOTRUN_QUAD_PIPELINE = RenderPipelines.DEBUG_QUADS;

    public static final RenderPipeline POSITION_COLOR_QUAD_PIPELINE = RenderPipelines.GUI;

    public static final RenderPipeline PROGRESS_BAR_PIPELINE = RenderPipelines.GUI_TEXTURED;
}
