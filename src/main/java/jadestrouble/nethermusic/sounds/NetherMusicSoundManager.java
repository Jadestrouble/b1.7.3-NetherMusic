package jadestrouble.nethermusic.sounds;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.Sound;
import paulscode.sound.SoundSystem;

import java.util.Random;
public class NetherMusicSoundManager {
    private static final String STREAMING_KEY = "streaming";
    private static final String MUSIC_KEY = "BgMusic";
    private static final Random RANDOM = new Random();
    private static boolean inTheNether = false;
    private static int timeUntilNextSong = 100;
    private static GameOptions gameOptions;
    private static SoundSystem soundSystem;

    public static void setInTheNether(boolean inTheNether) {
        if (NetherMusicSoundManager.inTheNether != inTheNether) {
            soundSystem.stop(MUSIC_KEY);
        }
        NetherMusicSoundManager.inTheNether = inTheNether;
    }

    public static void init(GameOptions gameOptions, SoundSystem soundSystem) {
        NetherMusicSoundManager.gameOptions = gameOptions;
        NetherMusicSoundManager.soundSystem = soundSystem;
    }

    public static void playBackgroundMusic() {
        if (gameOptions.musicVolume == 0.0f) return;
        if (soundSystem.playing(MUSIC_KEY) || soundSystem.playing(STREAMING_KEY)) return;
        if (--timeUntilNextSong > 0) return;
        NetherMusic.LOGGER.info("Attempting to play song!");
        Sound sound = NetherMusicClientSounds.getRandomMusic(RANDOM);
        timeUntilNextSong = 500 + RANDOM.nextInt(10);
        soundSystem.backgroundMusic(MUSIC_KEY, sound.soundFile, sound.id, false);
        soundSystem.setVolume(MUSIC_KEY, gameOptions.musicVolume * 0.25F);
        soundSystem.play(MUSIC_KEY);
    }

    public static boolean updateMusicVolume() {
        if (!inTheNether || gameOptions.musicVolume == 0.0f) return false;
        soundSystem.setVolume(MUSIC_KEY, gameOptions.musicVolume * 0.25F);
        return true;
    }
}
