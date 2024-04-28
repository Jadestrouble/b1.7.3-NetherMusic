package jadestrouble.nethermusic.mixin;

import jadestrouble.nethermusic.sounds.NetherMusicSoundManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulscode.sound.SoundSystem;

import static jadestrouble.nethermusic.sounds.NetherMusicSoundManager.*;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    @Shadow private static boolean started;
    @Shadow private GameOptions gameOptions;
    @Shadow private static SoundSystem soundSystem;
    @Shadow private int timeUntilNextSong;

    @Inject(method = "start", at = @At("TAIL"))
    private void netherMusic_setLibsAndCodecs(CallbackInfo info) {
        NetherMusicSoundManager.init(gameOptions, soundSystem);
    }
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void netherMusic_handleBackgroundMusic(CallbackInfo info) {
        if (!started) return;

        Minecraft minecraft = NetherMusicSoundManager.getMinecraft();
        boolean isNether = minecraft != null && minecraft.world != null && minecraft.world.dimension.id == -1;
        NetherMusicSoundManager.setInTheNether(isNether);
        if (isNether) {
            if (gameOptions.musicVolume == 0.0f) return;
            if (soundSystem.playing(MUSIC_KEY) || soundSystem.playing(STREAMING_KEY)) return;
            if (timeUntilNextSong > 0) return;
            NetherMusicSoundManager.LOGGER.info("Attempting to play song!");
            Sound sound = NetherMusicSoundManager.getRandomMusic(RANDOM);
            timeUntilNextSong = 12000 + RANDOM.nextInt(12000);
            soundSystem.backgroundMusic(MUSIC_KEY, sound.soundFile, sound.id, false);
            soundSystem.setVolume(MUSIC_KEY, gameOptions.musicVolume * 0.25F);
            soundSystem.play(MUSIC_KEY);
            info.cancel();
        }
    }
    @Inject(method = "updateMusicVolume", at = @At("HEAD"), cancellable = true)
    public void Nether_updateMusicVolume(CallbackInfo info) {
        if (NetherMusicSoundManager.updateMusicVolume()) info.cancel();
    }
}
