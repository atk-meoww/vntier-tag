package com.tiers.misc;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tiers.TiersClient;
import com.tiers.textures.Icons;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ConfigManager {
    private static Config config;
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("Tiers.json");
    private static String version;
    private static boolean upgradeAdjustmentDone;
    private static int launchTickCounter;

    static {
        FabricLoader.getInstance().getModContainer("tiers").ifPresent(tiers -> version = tiers.getMetadata().getVersion().getFriendlyString());
    }

    private static class Config {
        boolean toggleMod;
        boolean toggleIcons;
        boolean toggleTab;
        boolean toggleChat;
        boolean toggleAdaptiveSeparator;
        boolean toggleAutoKitDetect;
        TiersClient.ModesTierDisplay displayMode;
        Icons.Type activeIcons;

//        TiersClient.DisplayStatus positionMCTiers;
//        Mode activeMCTiersMode;

        TiersClient.DisplayStatus positionVSList;
        Mode activeVSListMode;

//        TiersClient.DisplayStatus positionSubtiers;
//        Mode activeSubtiersMode;

        String version;
    }

    public static void loadConfig() {
        Gson gson = new Gson();
        File file = CONFIG_PATH.toFile();
        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                config = gson.fromJson(fileReader, Config.class);
                if (config == null)
                    restoreFromClient();
            } catch (IOException | JsonSyntaxException ignored) {
                restoreFromClient();
            }
        } else
            restoreFromClient();

        TiersClient.toggleMod = config.toggleMod;
        TiersClient.toggleIcons = config.toggleIcons;
        TiersClient.toggleTab = config.toggleTab;
        TiersClient.toggleChat = config.toggleChat;
        TiersClient.toggleAdaptiveSeparator = config.toggleAdaptiveSeparator;
        TiersClient.toggleAutoKitDetect = config.toggleAutoKitDetect;

        if (Arrays.stream(TiersClient.ModesTierDisplay.values()).toList().contains(config.displayMode))
            TiersClient.displayMode = config.displayMode;

        if (Arrays.stream(Icons.Type.values()).toList().contains(config.activeIcons))
            TiersClient.activeIcons = config.activeIcons;

//        if (Arrays.stream(TiersClient.DisplayStatus.values()).toList().contains(config.positionMCTiers))
//            TiersClient.positionMCTiers = config.positionMCTiers;
//        if (Arrays.stream(Mode.values()).toList().contains(config.activeMCTiersMode) && config.activeMCTiersMode.toString().contains("MCTIERS"))
//            TiersClient.activeMCTiersMode = config.activeMCTiersMode;

        if (Arrays.stream(TiersClient.DisplayStatus.values()).toList().contains(config.positionVSList))
            TiersClient.positionVSList = config.positionVSList;
        if (Arrays.stream(Mode.values()).toList().contains(config.activeVSListMode) && config.activeVSListMode.toString().contains("VSLIST"))
            TiersClient.activeVSListMode = config.activeVSListMode;

//        if (Arrays.stream(TiersClient.DisplayStatus.values()).toList().contains(config.positionSubtiers))
//            TiersClient.positionSubtiers = config.positionSubtiers;
//        if (Arrays.stream(Mode.values()).toList().contains(config.activeSubtiersMode) && config.activeSubtiersMode.toString().contains("SUBTIERS"))
//            TiersClient.activeSubtiersMode = config.activeSubtiersMode;

        if (config.version == null) {
            ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
                if (upgradeAdjustmentDone)
                    return;

                if (minecraft.screen instanceof net.minecraft.client.gui.screens.TitleScreen) {
                    launchTickCounter++;

                    if (launchTickCounter >= 20) {
                        SystemToast.add(minecraft.getToastManager(), SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.literal("Thanks for updating Tiers"), Component.literal("Some settings may have changed"));
                        TiersClient.toggleMod = true;
                        TiersClient.toggleIcons = true;
                        TiersClient.toggleTab = true;
                        TiersClient.toggleChat = true;
                        TiersClient.toggleAdaptiveSeparator = true;
                        TiersClient.toggleAutoKitDetect = false;

                        saveConfig();
                        upgradeAdjustmentDone = true;
                    }
                }
            });
        }

        saveConfig();
    }

    private static void restoreFromClient() {
        config = new Config();
        updateConfig(config);

        TiersClient.LOGGER.info("Broken config file: Tiers has restored values from the client memory");

        saveConfig();
    }

    public static void saveConfig() {
        Gson gson = new Gson();
        File file = CONFIG_PATH.toFile();
        Config config = new Config();

        updateConfig(config);

        CompletableFuture.runAsync(() -> {
            try (FileWriter fileWriter = new FileWriter(file)) {
                gson.toJson(config, fileWriter);
            } catch (IOException ignored) {
                restoreFromClient();
            } finally {
                TiersClient.updateAllTags();
            }
        });
    }

    private static void updateConfig(Config config) {
        config.toggleMod = TiersClient.toggleMod;
        config.toggleIcons = TiersClient.toggleIcons;
        config.toggleTab = TiersClient.toggleTab;
        config.toggleChat = TiersClient.toggleChat;
        config.toggleAdaptiveSeparator = TiersClient.toggleAdaptiveSeparator;
        config.toggleAutoKitDetect = TiersClient.toggleAutoKitDetect;
        config.displayMode = TiersClient.displayMode;
        config.activeIcons = TiersClient.activeIcons;

//        config.positionMCTiers = TiersClient.positionMCTiers;
//        config.activeMCTiersMode = TiersClient.activeMCTiersMode;

        config.positionVSList = TiersClient.positionVSList;
        config.activeVSListMode = TiersClient.activeVSListMode;

//        config.positionSubtiers = TiersClient.positionSubtiers;
//        config.activeSubtiersMode = TiersClient.activeSubtiersMode;

        config.version = version;
    }

    public static String getCurrentConfig() {
        return "\nConfig{" +
                "\ntoggleMod=" + config.toggleMod +
                "\ntoggleIcons=" + config.toggleIcons +
                "\ntoggleTab=" + config.toggleTab +
                "\ntoggleChat=" + config.toggleChat +
                "\ntoggleAdaptiveSeparator=" + config.toggleAdaptiveSeparator +
                "\ntoggleAutoKitDetect=" + config.toggleAutoKitDetect +
                "\ndisplayMode=" + config.displayMode +
                "\nactiveIcons=" + config.activeIcons +
//                "\npositionMCTiers=" + config.positionMCTiers +
//                "\nactiveMCTiersMode=" + config.activeMCTiersMode +
                "\npositionVSList=" + config.positionVSList +
                "\nactiveVSListMode=" + config.activeVSListMode +
//                "\npositionSubtiers=" + config.positionSubtiers +
//                "\nactiveSubtiersMode=" + config.activeSubtiersMode +
                "\nversion=" + config.version +
                "\n}";
    }
}
