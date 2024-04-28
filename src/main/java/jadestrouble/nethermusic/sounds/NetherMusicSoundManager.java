package jadestrouble.nethermusic.sounds;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.Sound;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import paulscode.sound.SoundSystem;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
@Environment(EnvType.CLIENT)
public class NetherMusicSoundManager {
    public static final String STREAMING_KEY = "streaming";
    public static final String MUSIC_KEY = "BgMusic";
    public static final Random RANDOM = new Random();
    private static boolean inTheNether = false;
    private static GameOptions gameOptions;
    private static SoundSystem soundSystem;
    public static final Namespace NAMESPACE = Namespace.of("nethermusic");
    public static final Logger LOGGER = LogManager.getLogger();
    public static Identifier id(String name) {
        return NAMESPACE.id(name);
    }
    public static URL getURL(String path) {
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }
    @SuppressWarnings("deprecation")
    public static Minecraft getMinecraft() {
        return (Minecraft) FabricLoader.getInstance().getGameInstance();
    }

    public static void setInTheNether(boolean inTheNether) {
        if (NetherMusicSoundManager.inTheNether != inTheNether) {
            NetherMusicSoundManager.LOGGER.info("Switched Dimension!");
            soundSystem.stop(MUSIC_KEY);
        }
        NetherMusicSoundManager.inTheNether = inTheNether;
    }
    public static void init(GameOptions gameOptions, SoundSystem soundSystem) {
        NetherMusicSoundManager.gameOptions = gameOptions;
        NetherMusicSoundManager.soundSystem = soundSystem;
    }
    public static boolean updateMusicVolume() {
        NetherMusicSoundManager.LOGGER.info("Changed Music Volume!");
        if (!inTheNether || gameOptions.musicVolume == 0.0f) return false;
        soundSystem.setVolume(MUSIC_KEY, gameOptions.musicVolume * 0.25F);
        return true;
    }
    private static final Map<Identifier, Sound> SOUNDS = new HashMap<>();
    public static Sound getSound(Identifier id) {
        NetherMusicSoundManager.LOGGER.info("Tried to get sounds!");
        if (id == null) return null;
        return SOUNDS.computeIfAbsent(id, key -> {
            String path = "assets/" + key.namespace + "/stationapi/music/" + key.path + ".ogg";
            URL url = NetherMusicSoundManager.getURL(path);
            if (url == null) {
                NetherMusicSoundManager.LOGGER.warn("Sound " + path + " is missing!");
                return null;
            }
            return new Sound(path, url);
        });
    }

    private static final Sound[] MUSIC = new Sound[] {
            getSound(NetherMusicSoundManager.id("nether1")),
            getSound(NetherMusicSoundManager.id("nether2")),
            getSound(NetherMusicSoundManager.id("nether3")),
            getSound(NetherMusicSoundManager.id("nether4")),
    };

    private static final byte[] MUSIC_INDEX_DATA = new byte[MUSIC.length];
    private static byte musicIndex;

    static {
        for (byte i = 0; i < MUSIC.length; i++) {
            MUSIC_INDEX_DATA[i] = i;
        }
        shuffleMusic(new Random());
    }

    public static Sound getRandomMusic(Random random) {
        if (MUSIC.length == 1) return MUSIC[MUSIC_INDEX_DATA[0]];
        if (musicIndex == MUSIC.length) {
            byte value = MUSIC_INDEX_DATA[musicIndex - 1];
            shuffleMusic(random);
            while (MUSIC_INDEX_DATA[0] == value) {
                shuffleMusic(random);
            }
            musicIndex = 0;
        }
        return MUSIC[MUSIC_INDEX_DATA[musicIndex++]];
    }
    private static void shuffleMusic(Random random) {
        for (byte i = 0; i < MUSIC.length; i++) {
            byte i2 = (byte) random.nextInt(MUSIC.length);
            byte value = MUSIC_INDEX_DATA[i];
            MUSIC_INDEX_DATA[i] = MUSIC_INDEX_DATA[i2];
            MUSIC_INDEX_DATA[i2] = value;
        }
    }
}
