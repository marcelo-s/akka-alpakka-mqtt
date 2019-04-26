package utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtils {

    public static Config getConfig(String role, String port) {
        return getConfig(role, port, null);
    }

    public static Config getConfig(String role, String port, String configFile) {
        final Map<String, Object> properties = new HashMap<>();

        if (port != null) {
            properties.put("akka.remote.netty.tcp.port", port);
        }
        if (role != null) {
            properties.put("akka.cluster.roles", Arrays.asList(role));
        }
        // Check if a configuration file is provided to load it or use the default application.conf
        Config baseConfig = configFile != null ? ConfigFactory.load(configFile) : ConfigFactory.load();
        return ConfigFactory.parseMap(properties)
                .withFallback(baseConfig);
    }
}
