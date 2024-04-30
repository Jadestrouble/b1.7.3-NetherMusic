package jadestrouble.nethermusic.sounds;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.Sound;
import net.modificationstation.stationapi.api.util.Identifier;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class NetherMusicClientSounds {
    private static final Map<Identifier, Sound> SOUNDS = new HashMap<>();
    public static Sound getSound(Identifier id) {
        NetherMusic.LOGGER.info("Tried to get sounds!");
        if (id == null) return null;
        return SOUNDS.computeIfAbsent(id, key -> {
            String path = "assets/" + key.namespace + "/stationapi/music/" + key.path;
            URL url = NetherMusic.getURL(path);
            if (url == null) {
                NetherMusic.LOGGER.warn("Sound " + path + " is missing!");
                return null;
            }
            return new Sound(path, url);
        });
    }

    private static final Sound[] MUSIC = new Sound[] {
            getSound(NetherMusic.id("ballad_of_the_cats.ogg")),
            getSound(NetherMusic.id("concrete_halls.ogg")),
            getSound(NetherMusic.id("dead_voxel.ogg")),
            getSound(NetherMusic.id("warmth.ogg"))
    };

    private static final byte[] MUSIC_INDEX_DATA = new byte[MUSIC.length];
    private static byte musicIndex;

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
    @SuppressWarnings("deprecation")
    public static Minecraft getMinecraft() {
        return (Minecraft) FabricLoader.getInstance().getGameInstance();
    }
}

