/*
 * Copyright © Wynntils 2026.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.mc.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.wynntils.core.events.MixinHelper;
import com.wynntils.mc.event.ScreenClosedEvent;
import com.wynntils.mc.event.ScreenOpenedEvent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.2 moved ownership of the current Screen from Minecraft to Gui. Screen#onClose calls
 * Gui#setScreen directly, and Minecraft#setScreenAndShow is only the heavyweight "set and render
 * immediately" path, so hooking that method missed every ordinary screen close. Gui#setScreen is
 * the single setter both routes end up in.
 *
 * <p>Note this is not GuiMixin, which targets Hud - the HUD rendering half of the old Gui class.
 */
@Mixin(Gui.class)
public abstract class GuiScreenMixin {
    @Shadow
    private Screen screen;

    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"), cancellable = true)
    private void setScreenPre(Screen screen, CallbackInfo ci, @Share("oldScreen") LocalRef<Screen> oldScreen) {
        oldScreen.set(this.screen);

        // "var" is needed since there is no specific enough common supertype between ScreenOpenedEvent.Pre and
        // ScreenClosedEvent.Pre
        var event = (screen == null)
                ? new ScreenClosedEvent.Pre(oldScreen.get())
                : new ScreenOpenedEvent.Pre(screen, oldScreen.get());
        MixinHelper.postAlways(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("RETURN"))
    private void setScreenPost(Screen screen, CallbackInfo ci, @Share("oldScreen") LocalRef<Screen> oldScreen) {
        Event event = (screen == null)
                ? new ScreenClosedEvent.Post(oldScreen.get())
                : new ScreenOpenedEvent.Post(screen, oldScreen.get());
        MixinHelper.post(event);
    }
}
