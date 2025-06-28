package org.nonolbc1.cmdshort;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import java.nio.file.Path;

public class CommandShortener {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("cmdshort")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(argument("alias", StringArgumentType.string())
                                .then(argument("command", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String alias = StringArgumentType.getString(ctx, "alias");
                                            String command = StringArgumentType.getString(ctx, "command");
                                            int opLevel = 4;
                                            return registerAlias(ctx.getSource(), alias, command, opLevel);
                                        })
                                        .then(argument("opLevel", IntegerArgumentType.integer(0, 4))
                                                .executes(ctx -> {
                                                    String alias = StringArgumentType.getString(ctx, "alias");
                                                    String command = StringArgumentType.getString(ctx, "command");
                                                    int opLevel = IntegerArgumentType.getInteger(ctx, "opLevel");
                                                    return registerAlias(ctx.getSource(), alias, command, opLevel);
                                                })
                                        )
                                )
                        )
        );
    }

    private static int registerAlias(ServerCommandSource source, String alias, String command, int opLevel) {
        MinecraftServer server = source.getServer();
        Path worldDir = server.getSavePath(WorldSavePath.ROOT).toAbsolutePath();

        AliasStorage.saveAlias(worldDir, alias, command, opLevel);

        source.sendMessage(Text.literal("Commande \"/" + alias + "\" ajoutée !"));
        source.sendMessage(Text.literal("Veuillez faire /reload ou relancer le monde solo pour l'activer."));
        return Command.SINGLE_SUCCESS;
    }
}
