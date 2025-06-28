package org.nonolbc1.cmdshort;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class AliasStorage {
    private static final String FILE_NAME = "cmdshort_aliases.json";

    public static void saveAlias(Path worldDir, String alias, String command, int opLevel) {
        Path filePath = worldDir.resolve(FILE_NAME);
        Map<String, AliasEntry> aliases = loadAllAliases(filePath);
        aliases.put(alias, new AliasEntry(command, opLevel));
        saveAllAliases(filePath, aliases);
    }

    private static Map<String, AliasEntry> loadAllAliases(Path path) {
        Map<String, AliasEntry> map = new HashMap<>();
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                for (String key : obj.keySet()) {
                    JsonObject entry = obj.getAsJsonObject(key);
                    String cmd = entry.get("command").getAsString();
                    int op = entry.get("opLevel").getAsInt();
                    map.put(key, new AliasEntry(cmd, op));
                }
            } catch (IOException e) {
                CmdShort.LOGGER.error("Erreur lors du chargement des alias", e);
            }
        }
        return map;
    }

    private static void saveAllAliases(Path path, Map<String, AliasEntry> aliases) {
        JsonObject root = new JsonObject();
        for (Map.Entry<String, AliasEntry> entry : aliases.entrySet()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("command", entry.getValue().command);
            obj.addProperty("opLevel", entry.getValue().opLevel);
            root.add(entry.getKey(), obj);
        }

        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
            }
        } catch (IOException e) {
            CmdShort.LOGGER.error("Erreur lors de la sauvegarde des alias", e);
        }
    }

    private static class AliasEntry {
        String command;
        int opLevel;

        AliasEntry(String command, int opLevel) {
            this.command = command;
            this.opLevel = opLevel;
        }
    }

    public static Map<String, AliasData> load(Path worldDir) {
        Path filePath = worldDir.resolve(FILE_NAME);
        Map<String, AliasEntry> aliasEntries = loadAllAliases(filePath);
        Map<String, AliasData> result = new HashMap<>();
        for (Map.Entry<String, AliasEntry> entry : aliasEntries.entrySet()) {
            result.put(entry.getKey(), new AliasData(entry.getValue().command, entry.getValue().opLevel));
        }
        return result;
    }

    public static class AliasData {
        public final String command;
        public final int opLevel;

        public AliasData(String command, int opLevel) {
            this.command = command;
            this.opLevel = opLevel;
        }
    }
}
