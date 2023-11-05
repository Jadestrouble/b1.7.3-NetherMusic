package jadestrouble.nethermusic.mixin;

import jadestrouble.nethermusic.sounds.NetherMusicSoundManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulscode.sound.SoundSystem;

import static jadestrouble.nethermusic.sounds.NetherMusicSoundManager.*;

@Mixin(SoundHelper.class)
public class SoundHelperMixin {
    @Shadow private static boolean initialized;
    @Shadow private GameOptions gameOptions;
    @Shadow private static SoundSystem soundSystem;
    @Shadow private int musicCountdown;

    @Inject(method = "setLibsAndCodecs", at = @At("TAIL"))
    private void netherMusic_setLibsAndCodecs(CallbackInfo info) {
        NetherMusicSoundManager.init(gameOptions, soundSystem);
    }
    @Inject(method = "handleBackgroundMusic", at = @At("HEAD"), cancellable = true)
    private void netherMusic_handleBackgroundMusic(CallbackInfo info) {
        if (!initialized) return;
        @SuppressWarnings("deprecated")
        Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
        boolean isNether = minecraft != null && minecraft.level != null && minecraft.level.dimension.id == -1;
        NetherMusicSoundManager.setInTheNether(isNether);
        if (isNether) {
            if (gameOptions.music == 0.0f) return;
            if (soundSystem.playing(MUSIC_KEY) || soundSystem.playing(STREAMING_KEY)) return;
            if (musicCountdown > 0) return;
            SoundEntry soundEntry = NetherMusicSoundManager.getRandomMusic(RANDOM);
            musicCountdown = 12000 + RANDOM.nextInt(12000);
            soundSystem.backgroundMusic(MUSIC_KEY, soundEntry.soundUrl, soundEntry.soundName, false);
            soundSystem.setVolume(MUSIC_KEY, gameOptions.music * 0.25F);
            soundSystem.play(MUSIC_KEY);
            info.cancel();
        }
    }
    @Inject(method = "updateMusicVolume", at = @At("HEAD"), cancellable = true)
    public void Nether_updateMusicVolume(CallbackInfo info) {
        if (NetherMusicSoundManager.updateMusicVolume()) info.cancel();
    }
}