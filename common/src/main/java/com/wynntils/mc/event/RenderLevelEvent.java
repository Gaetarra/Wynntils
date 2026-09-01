/*
 * Copyright © Wynntils 2022-2026.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.mc.event;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.neoforged.bus.api.Event;
import org.joml.Matrix4fc;

// Note: Neither of these events provide a PoseStack, as it'd be just an empty stack.
public abstract class RenderLevelEvent extends Event {
    private final LevelRenderer levelRenderer;
    private final DeltaTracker deltaTracker;
    private final Matrix4fc projectionMatrix;
    private final CameraRenderState camera;

    protected RenderLevelEvent(
            LevelRenderer levelRenderer,
            DeltaTracker deltaTracker,
            Matrix4fc projectionMatrix,
            CameraRenderState camera) {
        this.levelRenderer = levelRenderer;
        this.deltaTracker = deltaTracker;
        this.projectionMatrix = projectionMatrix;
        this.camera = camera;
    }

    public LevelRenderer getLevelRenderer() {
        return this.levelRenderer;
    }

    public DeltaTracker getDeltaTracker() {
        return this.deltaTracker;
    }

    public Matrix4fc getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public CameraRenderState getCamera() {
        return camera;
    }

    public static class Pre extends RenderLevelEvent {
        public Pre(
                LevelRenderer levelRenderer,
                DeltaTracker deltaTracker,
                Matrix4fc projectionMatrix,
                CameraRenderState camera) {
            super(levelRenderer, deltaTracker, projectionMatrix, camera);
        }
    }

    public static class Post extends RenderLevelEvent {
        public Post(
                LevelRenderer levelRenderer,
                DeltaTracker deltaTracker,
                Matrix4fc projectionMatrix,
                CameraRenderState camera) {
            super(levelRenderer, deltaTracker, projectionMatrix, camera);
        }
    }
}
