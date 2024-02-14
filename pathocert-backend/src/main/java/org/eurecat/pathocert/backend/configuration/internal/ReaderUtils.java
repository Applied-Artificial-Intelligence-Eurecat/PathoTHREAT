package org.eurecat.pathocert.backend.configuration.internal;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Reads all the properties and provides utilities to parse them.
 */
public class ReaderUtils {

    public static void showAllProperties(Properties p) {
        System.out.println("*******");
        for (Object k : p.keySet()) {
            System.out.println(k + "=" + p.getProperty((String) k));
        }
        System.out.println("*******");
    }

    private static boolean yamlFormated(String path) {
        boolean yamlFormat = path.endsWith(".yaml");
        boolean propertiesFormat = path.endsWith(".properties");
        if (!yamlFormat && !propertiesFormat) {
            System.err.println(
                    "Unknown file format for " + path + " should be .yaml or .properties. Asuming .properties");
        }
        return yamlFormat;
    }

    public static Properties read(String configs) throws RuntimeException {
        Properties finalProperties = initProperties(configs);
        for (String path : configs.split(";")) {
            var partialProperties = readSourceOfProperties(path);
            finalProperties.putAll(partialProperties);
        }
        List<Object> nullKeys = new LinkedList<>();

        for (Object key : finalProperties.keySet()) {
            if ("\\NULL".equals(finalProperties.get(key))) nullKeys.add(key);
        }

        for (Object key : nullKeys) finalProperties.remove(key);
        showAllProperties(finalProperties);
        return finalProperties;

    }

    private static Properties readSourceOfProperties(String path) {
        if (path.startsWith("file://")) {
            return readFileOfProperties(path);
        } else if (path.startsWith("classpath://")) {
            return readFileFromClasspath(path);
        } else if (path.startsWith("inline://")) {
            return readInlineProperty(path);
        } else if (!path.equalsIgnoreCase("default")) {
            System.err.println(
                    "Unknown source for " + path + " must be 'file://', 'classpath://', default or 'inline://' ");
        }
        return new Properties(); // default :: ignore, i.e.: 0 properties loaded
        // print(partialProperties);
    }

    private static Properties readInlineProperty(String path) {
        path = path.replace("inline://", "");
        Properties partialProperties = new Properties();
        partialProperties.put(path.split("=")[0], path.split("=")[1]);
        return partialProperties;
    }

    private static Properties readFileFromClasspath(String path) {
        Properties partialProperties = new Properties();
        path = path.replace("classpath://", "");
        try (final InputStream inputStream = ReaderUtils.class.getClassLoader()
                .getResourceAsStream(path)) {
            if (inputStream == null || inputStream.available() == 0)
                throw new RuntimeException("Can't find file " + path);

            if (yamlFormated(path)) {
                partialProperties.putAll(getYAMLProperties(inputStream));
            } else {
                partialProperties.load(inputStream);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return partialProperties;
    }

    private static Properties readFileOfProperties(String path) {
        Properties partialProperties = new Properties();
        path = path.replace("file://", "");
        try (FileInputStream f = new FileInputStream(path)) {
            if (yamlFormated(path)) {
                partialProperties.putAll(getYAMLProperties(f));
            } else {
                partialProperties.load(f);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return partialProperties;
    }

    public static Properties getYAMLProperties(InputStream f) {
        TreeMap<String, String> p = new Yaml().load(f);
        var prop = new Properties();
        prop.putAll(p);
        return prop;
    }

    private static Properties initProperties(String configs) {
        return new Properties();
    }


}
