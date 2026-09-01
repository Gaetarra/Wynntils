/*
 * Copyright © Wynntils 2023-2026.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.mc.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class EntityNameTagRenderEvent extends Event implements ICancellableEvent {
    private final EntityRenderState entityRenderState;
    private final PoseStack poseStack;
    private final SubmitNodeCollector submitNodeCollector;
    private final CameraRenderState cameraRenderState;

    public EntityNameTagRenderEvent(
            EntityRenderState entityRenderState,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState cameraRenderState) {
        this.entityRenderState = entityRenderState;
        this.poseStack = poseStack;
        this.submitNodeCollector = submitNodeCollector;
        this.cameraRenderState = cameraRenderState;
    }

    /**
     * 26.2 replaced EntityRenderer#submitNameTag with #extractNameTags, which receives no PoseStack,
     * SubmitNodeCollector or CameraRenderState. Events raised from that path carry only the render
     * state and are used for cancellation; the avatar path still supplies the full set.
     */
    public EntityNameTagRenderEvent(EntityRenderState entityRenderState) {
        this(entityRenderState, null, null, null);
    }

    public EntityRenderState getEntityRenderState() {
        return entityRenderState;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public SubmitNodeCollector getSubmitNodeCollector() {
        return submitNodeCollector;
    }

    public CameraRenderState getCameraRenderState() {
        return cameraRenderState;
    }
}
