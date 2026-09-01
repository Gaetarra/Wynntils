/*
 * Copyright © Wynntils 2022-2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.mc.mixin;

import com.wynntils.core.mod.CrashReportManager;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrashReport.class)
public abstract class CrashReportMixin {
    // 26.2 removed SystemReport#appendToCrashReportString, so this appends at the end instead.
    @Inject(method = "getDetails(Ljava/lang/StringBuilder;)V", at = @At("TAIL"))
    private void addWynntilsDetails(StringBuilder builder, CallbackInfo ci) {
        // This needs to go directly to CrashReportManager and not through Managers
        CrashReportCategory wynntilsCrashDetails = CrashReportManager.generateDetails();

        wynntilsCrashDetails.getDetails(builder);
        builder.append("\n\n");
    }
}
