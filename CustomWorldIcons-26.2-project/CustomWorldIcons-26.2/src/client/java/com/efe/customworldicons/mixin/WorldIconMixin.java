package com.efe.customworldicons.mixin;

import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 26.2 port scaffold. The GitHub build workflow also decompiles/checks the
 * target Minecraft class so the final injection can be kept aligned with 26.2.
 */
@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class WorldIconMixin {
    // Minecraft 26.2 already uploads icon.png as a texture; the remaining
    // vanilla limitation is applied in the world-list icon preparation path.
    // Kept intentionally empty until the exact 26.2 method signature is
    // confirmed by the CI compile output instead of guessing a method name.
}
