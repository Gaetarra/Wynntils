/*
 * Copyright © Wynntils 2025-2026.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.mc.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.wynntils.core.events.MixinHelper;
import com.wynntils.mc.event.SystemMessageEvent;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChatListener.class)
public abstract class ChatListenerMixin {
    /*
     * 26.2 split the two paths apart. handleSystemMessage is now chat only - its boolean is no
     * longer "overlay" and ClientPacketListener passes a constant true - while action bar text
     * goes to handleOverlay, which feeds Hud#setOverlayMessage. Branching on the boolean here
     * posted every chat message as action bar text and never saw the real action bar at all.
     */
    @WrapMethod(method = "handleSystemMessage(Lnet/minecraft/network/chat/Component;Z)V")
    private void handleSystemMessageWrap(Component message, boolean canReceive, Operation<Void> original) {
        SystemMessageEvent event = new SystemMessageEvent.ChatReceivedEvent(message);
        MixinHelper.post(event);

        Component newMessage = event.isMessageChanged() ? event.getMessage() : message;

        if (!event.isCanceled()) {
            original.call(newMessage, canReceive);
        }
    }

    @WrapMethod(method = "handleOverlay(Lnet/minecraft/network/chat/Component;)V")
    private void handleOverlayWrap(Component message, Operation<Void> original) {
        SystemMessageEvent event = new SystemMessageEvent.GameInfoReceivedEvent(message);
        MixinHelper.post(event);

        Component newMessage = event.isMessageChanged() ? event.getMessage() : message;

        if (!event.isCanceled()) {
            original.call(newMessage);
        }
    }
}
