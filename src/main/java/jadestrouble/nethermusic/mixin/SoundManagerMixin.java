package jadestrouble.nethermusic.mixin;

import jadestrouble.nethermusic.sounds.NetherMusicClientSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import jadestrouble.nethermusic.sounds.NetherMusicSoundManager;
import paulscode.sound.SoundSystem;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    @Shadow private static boolean started;
    @Shadow private GameOptions gameOptions;
    @Shadow private static SoundSystem soundSystem;

    @Inject(method = "start", at = @At("TAIL"))
    private void nethermusic_start(CallbackInfo info) { NetherMusicSoundManager.init(gameOptions, soundSystem);
    }
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void nethermusic_tick(CallbackInfo info) {
        if (!started) return;
        Minecraft minecraft = NetherMusicClientSounds.getMinecraft();
        boolean isNether = minecraft != null && minecraft.world != null && minecraft.world.dimension.id == -1;
        NetherMusicSoundManager.setInTheNether(isNether);
        if (isNether) {
            NetherMusicSoundManager.playBackgroundMusic();
            info.cancel();
        }
    }
    @Inject(method = "updateMusicVolume", at = @At("HEAD"), cancellable = true)
    public void nethermusic_updateMusicVolume(CallbackInfo info) { if (NetherMusicSoundManager.updateMusicVolume()) info.cancel();
    }
}
