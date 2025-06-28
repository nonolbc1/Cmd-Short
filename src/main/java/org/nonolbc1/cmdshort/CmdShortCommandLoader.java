package org.nonolbc1.cmdshort;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;

import java.nio.file.Path;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class CmdShortCommandLoader {
    public static void registerAll(CommandDispatcher<ServerCommandSource> dispatcher, MinecraftServer server) {
        Path worldDir = server.getSavePath(WorldSavePath.ROOT);
        Map<String, AliasStorage.AliasData> aliases = AliasStorage.load(worldDir);

        for (Map.Entry<String, AliasStorage.AliasData> entry : aliases.entrySet()) {
            String alias = entry.getKey();
            String fullCommand = entry.getValue().command;
            int requiredOpLevel = entry.getValue().opLevel;

            dispatcher.register(literal(alias)
                    .requires(source -> source.hasPermissionLevel(requiredOpLevel))
                    .executes(context -> {
                        ServerCommandSource source = context.getSource();
                        source.getServer().getCommandManager().executeWithPrefix(source, fullCommand);
                        return 1;
                    })
            );
        }
    }
}
