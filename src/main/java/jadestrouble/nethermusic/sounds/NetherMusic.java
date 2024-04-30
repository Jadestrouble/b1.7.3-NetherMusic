package jadestrouble.nethermusic.sounds;

import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

public class NetherMusic {
    public static final Namespace NAMESPACE = Namespace.of("nethermusic");
    public static final Logger LOGGER = LogManager.getLogger();

    public static Identifier id(String name) {
        return NAMESPACE.id(name);
    }

    public static URL getURL(String path) {
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }
}