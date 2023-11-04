package jadestrouble.nethermusic.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.sound.SoundHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulscode.sound.SoundSystem;

import java.util.Random;
@Mixin(SoundHelper.class)
public class SoundHelperMixin {
    private Random rand = new Random();
    private static SoundSystem soundSystem;
    private GameOptions gameOptions;
    @Shadow
    private int musicCountdown;

    @Inject(method = "handleBackgroundMusic", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundMap;getRandomSound()Lnet/minecraft/client/sound/SoundEntry;"),cancellable = true)
    private void netherCheck(CallbackInfo ci) {
        if (((Minecraft) FabricLoader.getInstance().getGameInstance()).player.dimensionId == -1) {
            this.musicCountdown = this.rand.nextInt(12000) + 12000;
            ci.cancel();
        }
    }
}