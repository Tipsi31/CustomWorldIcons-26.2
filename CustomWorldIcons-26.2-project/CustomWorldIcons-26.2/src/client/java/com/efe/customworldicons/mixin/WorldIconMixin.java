package com.efe.customworldicons.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FaviconTexture.class)
public abstract class WorldIconMixin {

    @Shadow @Final
    private TextureManager textureManager;

    @Shadow @Final
    private Identifier textureLocation;

    @Shadow
    private @Nullable DynamicTexture texture;

    @Shadow
    private boolean closed;

    @Inject(method = "upload", at = @At("HEAD"), cancellable = true)
    private void customworldicons26$uploadHighResolution(
            NativeImage image,
            CallbackInfo ci
    ) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Normal 64x64 ikonları vanilla Minecraft'a bırak.
        if (width == 64 && height == 64) {
            return;
        }

        // Yüksek çözünürlüklü ikon kare olmalı.
        // 4096 üstünü de gereksiz/aşırı büyük olduğu için engelliyoruz.
        if (width <= 0 || width != height || width > 4096) {
            image.close();

            throw new IllegalArgumentException(
                    "Custom World Icons: icon must be square and at most "
                            + "4096x4096, but was "
                            + width + "x" + height
            );
        }

        if (this.closed) {
            image.close();
            throw new IllegalStateException("Icon already closed");
        }

        try {
            // Eski texture varsa kaldır.
            // Böylece 64 -> 512 veya 512 -> 1024 gibi çözünürlük
            // değişimleri sorun çıkarmıyor.
            if (this.texture != null) {
                this.textureManager.release(this.textureLocation);
                this.texture.close();
            }

            this.texture = new DynamicTexture(
                    () -> "High resolution favicon " + this.textureLocation,
                    image
            );

            this.textureManager.register(
                    this.textureLocation,
                    this.texture
            );

            // Vanilla upload() metodunun 64x64 kontrolüne gitmesini engelle.
            ci.cancel();

        } catch (Throwable throwable) {
            image.close();

            if (this.texture != null) {
                this.textureManager.release(this.textureLocation);
                this.texture.close();
                this.texture = null;
            }

            throw throwable;
        }
    }
}
