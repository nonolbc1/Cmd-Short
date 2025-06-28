package org.nonolbc1.cmdshort;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdShort implements ModInitializer {
    public static final String MOD_ID = "cmdshort";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(CommandShortener::register);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            if (environment.dedicated || environment.integrated) {
                MinecraftServer server = net.fabricmc.loader.api.FabricLoader.getInstance().getGameInstance() instanceof MinecraftServer s ? s : null;
                if (server != null) {
                    CmdShortCommandLoader.registerAll(dispatcher, server);
                }
            }
        });
    }
}
