package com.tiers.screens;

import com.mojang.blaze3d.platform.NativeImage;
import com.tiers.PlayerProfileQueue;
import com.tiers.TiersClient;
import com.tiers.misc.ConfigManager;
import com.tiers.profile.PlayerProfile;
import com.tiers.profile.Status;
import com.tiers.profile.types.PvPTiersProfile;
import com.tiers.textures.ColorControl;
import com.tiers.textures.Icons;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.stream.Stream;

import static com.tiers.TiersClient.LOGGER;

public class ConfigScreen extends Screen {
    public static PlayerProfile ownProfile;
    public static PlayerProfile defaultProfile;

    private boolean useOwnProfile;
    private String autoDetectKitBoundKey;
    private String cycleRightBoundKey;
    private String cycleLeftBoundKey;
    private final Identifier playerAvatarTexture = Identifier.parse("tiers:avatar");
    private boolean imageReady;

    private Button toggleMod;
    private Button toggleIcons;
    private Button toggleTab;
    private Button toggleChat;
    private Button toggleSeparatorMode;
    private Button cycleDisplayMode;
    private Button clearPlayerCache;
    private Button autoKitDetect;
//    private Button leftMCTiers;
//    private Button centerMCTiers;
//    private Button rightMCTiers;
    private Button leftPvPTiers;
//    private Button centerPvPTiers;
    private Button rightPvPTiers;
//    private Button leftSubtiers;
//    private Button centerSubtiers;
//    private Button rightSubtiers;
    private Button activeRightMode;
    private Button activeLeftMode;
    private Button enableOwnProfile;

    private int centerX;
    private int distance;

    private ConfigScreen() {
        super(Component.literal("Tiers config"));

        autoDetectKitBoundKey = String.valueOf(TiersClient.autoDetectKey.getTranslatedKeyMessage()).replace("literal{", "\"").replace("}", "\"");
        if (autoDetectKitBoundKey.length() != 3)
            autoDetectKitBoundKey = "the assigned keybind";

        cycleRightBoundKey = String.valueOf(TiersClient.cycleRightKey.getTranslatedKeyMessage()).replace("literal{", "\"").replace("}", "\"");
        if (cycleRightBoundKey.length() != 3)
            cycleRightBoundKey = "the assigned keybind";

        cycleLeftBoundKey = String.valueOf(TiersClient.cycleLeftKey.getTranslatedKeyMessage()).replace("literal{", "\"").replace("}", "\"");
        if (cycleLeftBoundKey.length() != 3)
            cycleLeftBoundKey = "the assigned keybind";

        loadPlayerAvatar();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float a) {
        centerX = width / 2;
        distance = height / 14;

        super.render(graphics, mouseX, mouseY, a);

        graphics.drawCenteredString(font, Component.literal("Tiers config"), centerX, height / 50, 0xFFFFFF);

        drawIconShowcase(graphics);

        if (!useOwnProfile)
            graphics.blit(playerAvatarTexture, centerX - height / 10 / 2, height - (int) (height / 4.166) - height / 54, height / 10, (int) (height / 4.166), 0, 0, height / 10, (int) (height / 4.166));
        else
            drawPlayerAvatar(graphics, centerX, height - (int) (height / 4.166) - height / 54);

        graphics.drawCenteredString(font, useOwnProfile ? ownProfile.getFullName() : defaultProfile.getFullName(), centerX, height - (int) (height / 4.166) - height / 54 - 12, 0xFFFFFF);

        //graphics.blit(MCTiersProfile.MCTIERS_IMAGE, centerX - 120 - 64, distance + 110 + 4, 0, 0, 128, 24, 128, 24);
        graphics.blit(PvPTiersProfile.PVPTIERS_IMAGE, centerX - 12, distance + 110 + 4, 24, 24, 0, 0, 24, 24);
        //graphics.blit(SubtiersProfile.SUBTIERS_IMAGE, centerX + 120 - 15, distance + 110, 0, 0, 30, 30, 30, 30);

        graphics.drawString(font, TiersClient.getRightIcon(), centerX + 90 + 32, distance + 75 + 9, 0xFFFFFF);
        graphics.drawString(font, TiersClient.getLeftIcon(), centerX - 90 - 32 - 12, distance + 75 + 9, 0xFFFFFF);

        checkUpdates();
    }

    private void drawIconShowcase(GuiGraphics graphics) {
        for (int i = 0; i < 8; i++) {
            graphics.drawCenteredString(font, Component.literal(String.valueOf((char) (0xF000 + i))).setStyle(Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "gamemodes/classic-medium")))), 34 + 14 * i, 13, 0xFFFFFF);
            graphics.drawCenteredString(font, Component.literal(String.valueOf((char) (0xF000 + i))).setStyle(Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "gamemodes/pvptiers-medium")))), 34 + 14 * i, 38, 0xFFFFFF);
            graphics.drawCenteredString(font, Component.literal(String.valueOf((char) (0xF000 + i))).setStyle(Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "gamemodes/mctiers-medium")))), 34 + 14 * i, 63, 0xFFFFFF);
        }
    }

    private void checkUpdates() {
        toggleMod.setPosition(width / 2 - 88 - 2, distance);
        toggleIcons.setPosition(width / 2 + 2, distance);
        toggleTab.setPosition(width / 2 + 2 + 28 + 2, distance);
        toggleChat.setPosition(width / 2 + 2 + 28 + 2 + 28 + 2, distance);
        toggleSeparatorMode.setPosition(width / 2 - 90, distance + 25);
        cycleDisplayMode.setPosition(width / 2 - 90, distance + 50);
        autoKitDetect.setPosition(width / 2 - 90, distance + 75);
        clearPlayerCache.setPosition(width - 88 - 5, height - 20 - 5);
//        leftMCTiers.setPosition(centerX - 120 - 10 - 24, distance + 145);
//        centerMCTiers.setPosition(centerX - 120 - 10, distance + 145);
//        rightMCTiers.setPosition(centerX - 120 - 10 + 24, distance + 145);
        leftPvPTiers.setPosition(centerX - 10 - 12, distance + 145);
//        centerPvPTiers.setPosition(centerX - 10, distance + 145);
        rightPvPTiers.setPosition(centerX - 10 + 12, distance + 145);
//        leftSubtiers.setPosition(centerX + 120 - 10 - 24, distance + 145);
//        centerSubtiers.setPosition(centerX + 120 - 10, distance + 145);
//        rightSubtiers.setPosition(centerX + 120 - 10 + 24, distance + 145);
        activeRightMode.setPosition(centerX + 90 + 4, distance + 75);
        activeLeftMode.setPosition(centerX - 90 - 20 - 4, distance + 75);
        enableOwnProfile.setPosition(width - 20 - 5 - 88 - 4, height - 20 - 5);

        updateVisibilities();
    }

    @Override
    protected void init() {
        centerX = width / 2;
        distance = height / 14;

        toggleMod = Button.builder(Component.literal(TiersClient.toggleMod ? "Disable Tiers" : "Enable Tiers"), (Button) -> {
            TiersClient.toggleMod();
            toggleTab.active = TiersClient.toggleMod;
            toggleChat.active = TiersClient.toggleMod;
            Button.setMessage(Component.literal(TiersClient.toggleMod ? "Disable Tiers" : "Enable Tiers"));
            Button.setTooltip(Tooltip.create(Component.literal((TiersClient.toggleMod ? "Disable Tiers" : "Enable Tiers"))));
        }).bounds(width / 2 - 88 - 2, distance, 88, 20).tooltip(Tooltip.create(Component.literal((TiersClient.toggleMod ? "Disable Tiers" : "Enable Tiers")))).build();

        toggleIcons = Button.builder(TiersClient.toggleIcons ? Icons.ICONS : Icons.ICONS_DISABLED, (buttonWidget) -> {
            TiersClient.toggleIcons();
            buttonWidget.setMessage(TiersClient.toggleIcons ? Icons.ICONS : Icons.ICONS_DISABLED);
            buttonWidget.setTooltip(Tooltip.create(Component.literal(TiersClient.toggleIcons ? "Disable the gamemode icon next to the tier" : "Enable the gamemode icon next to the tier")));
        }).bounds(width / 2 + 2, distance, 28, 20).tooltip(Tooltip.create(Component.literal(TiersClient.toggleIcons ? "Disable the gamemode icon next to the tier" : "Enable the gamemode icon next to the tier"))).build();

        toggleTab = Button.builder(TiersClient.toggleTab ? Icons.TAB : Icons.TAB_DISABLED, (buttonWidget) -> {
            TiersClient.toggleTab();
            buttonWidget.setMessage(TiersClient.toggleTab ? Icons.TAB : Icons.TAB_DISABLED);
            buttonWidget.setTooltip(Tooltip.create(Component.literal(TiersClient.toggleTab ? "Disable Tiers on the tablist" : "Enable Tiers on the tablist")));
        }).bounds(width / 2 + 2 + 28 + 2, distance, 28, 20).tooltip(Tooltip.create(Component.literal(TiersClient.toggleTab ? "Disable Tiers on the tablist" : "Enable Tiers on the tablist"))).build();

        toggleChat = Button.builder(TiersClient.toggleChat ? Icons.CHAT : Icons.CHAT_DISABLED, (buttonWidget) -> {
            TiersClient.toggleChat();
            buttonWidget.setMessage(TiersClient.toggleChat ? Icons.CHAT : Icons.CHAT_DISABLED);
            buttonWidget.setTooltip(Tooltip.create(Component.literal(TiersClient.toggleChat ? "Disable Tiers in chat" : "Enable Tiers in chat")));
        }).bounds(width / 2 + 2 + 28 + 2 + 28 + 2, distance, 28, 20).tooltip(Tooltip.create(Component.literal(TiersClient.toggleChat ? "Disable Tiers in chat" : "Enable Tiers in chat"))).build();

        toggleTab.active = TiersClient.toggleMod;
        toggleChat.active = TiersClient.toggleMod;

        toggleSeparatorMode = Button.builder(Component.literal(TiersClient.toggleAdaptiveSeparator ? "Disable Dynamic Separator" : "Enable Dynamic Separator"), (buttonWidget) -> {
            TiersClient.toggleAdaptiveSeparator();
            buttonWidget.setMessage(Component.literal(TiersClient.toggleAdaptiveSeparator ? "Disable Dynamic Separator" : "Enable Dynamic Separator"));
            buttonWidget.setTooltip(Tooltip.create(Component.literal(TiersClient.toggleAdaptiveSeparator ? "Make the Tiers separator gray" : "Make the Tiers separator match the tier color")));
        }).bounds(width / 2 - 90, distance + 25, 180, 20).tooltip(Tooltip.create(Component.literal(TiersClient.toggleAdaptiveSeparator ? "Make the Tiers separator gray" : "Make the Tiers separator match the tier color"))).build();

        cycleDisplayMode = Button.builder(Component.literal(TiersClient.displayMode.getCurrentMode()), (buttonWidget) -> {
            TiersClient.cycleDisplayMode();
            buttonWidget.setMessage(Component.literal(TiersClient.displayMode.getCurrentMode()));
        }).bounds(width / 2 - 90, distance + 50, 180, 20).tooltip(Tooltip.create(Component.literal(("""
                Selected: only the selected tier will be displayed
                
                Highest: only the highest tier will be displayed
                
                Adaptive Highest: the highest tier will be displayed if selected does not exist""")))).build();

        autoKitDetect = Button.builder(Component.literal(TiersClient.toggleAutoKitDetect ? "Disable auto kit detect" : "Enable auto kit detect"), (buttonWidget) -> {
            TiersClient.toggleAutoKitDetect();
            buttonWidget.setMessage(Component.literal(TiersClient.toggleAutoKitDetect ? "Disable auto kit detect" : "Enable auto kit detect"));
            buttonWidget.setTooltip(Tooltip.create(Component.literal((TiersClient.toggleAutoKitDetect ?
                    "Disable auto kit detect: you will need to press " + autoDetectKitBoundKey + " to auto-detect the current gamemode" :
                    "Enable auto kit detect: Tiers will always scan your inventory to display the right gamemode (instead of pressing " + autoDetectKitBoundKey + ")"))));
        }).bounds(width / 2 - 90, distance + 75, 180, 20).tooltip(Tooltip.create(Component.literal((TiersClient.toggleAutoKitDetect ?
                "Disable auto kit detect: you will need to press " + autoDetectKitBoundKey + " to auto-detect the current gamemode" :
                "Enable auto kit detect: Tiers will always scan your inventory to display the right gamemode (instead of pressing " + autoDetectKitBoundKey + ")")))).build();

        if (ownProfile.status == Status.READY) {
            enableOwnProfile = Button.builder(Icons.CYCLE, (buttonWidget) -> {
                useOwnProfile = !useOwnProfile;

                imageReady = false;
                loadPlayerAvatar();

                buttonWidget.setTooltip(Tooltip.create(Component.literal(useOwnProfile ? "Preview the default profile (" + defaultProfile.name + ")" : "Preview your player profile (" + ownProfile.name + ")")));
            }).bounds(width - 20 - 5 - 88 - 4, height - 20 - 5, 20, 20).tooltip(Tooltip.create(Component.literal(useOwnProfile ? "Preview the default profile (" + defaultProfile.name + ")" : "Preview your player profile (" + ownProfile.name + ")"))).build();
        } else {
            enableOwnProfile = Button.builder(Component.literal("⚠"), (ignored) -> {
                ownProfile = new PlayerProfile(Minecraft.getInstance().getGameProfile().name(), false);
                PlayerProfileQueue.putFirstInQueue(ownProfile);

                onClose();
            }).bounds(width - 20 - 5 - 88 - 4, height - 20 - 5, 20, 20).tooltip(Tooltip.create(Component.literal("Can't switch profiles: " + ownProfile.name + " is not found or fetched yet. Click to close screen and retry"))).build();
        }

        clearPlayerCache = Button.builder(Component.literal("Clear cache"), (ignored) -> TiersClient.clearCache(false)).bounds(width - 88 - 5, height - 20 - 5, 88, 20).tooltip(Tooltip.create(Component.literal("Clear all player cache"))).build();

//        leftMCTiers = Button.builder(Component.literal("←"), (buttonWidget) -> {
//            TiersClient.positionMCTiers = TiersClient.DisplayStatus.LEFT;
//            if (TiersClient.positionVSList == TiersClient.DisplayStatus.LEFT) {
//                TiersClient.positionVSList = TiersClient.DisplayStatus.OFF;
//                leftPvPTiers.active = true;
//                centerPvPTiers.active = false;
//            }
//            updateLeftSwitcher(buttonWidget, centerMCTiers, rightMCTiers);
//        }).bounds(centerX - 120 - 10 - 24, distance + 145, 20, 20).tooltip(Tooltip.create(Component.literal("Display MCTiers on the left"))).build();
//
//        centerMCTiers = Button.builder(Component.literal("●"), (buttonWidget) -> {
//            TiersClient.positionMCTiers = TiersClient.DisplayStatus.OFF;
//            leftMCTiers.active = true;
//            buttonWidget.active = false;
//            rightMCTiers.active = true;
//            ConfigManager.saveConfig();
//        }).bounds(centerX - 120 - 10, distance + 145, 20, 20).tooltip(Tooltip.create(Component.literal("Disable MCTiers"))).build();
//
//        rightMCTiers = Button.builder(Component.literal("→"), (buttonWidget) -> {
//            TiersClient.positionMCTiers = TiersClient.DisplayStatus.RIGHT;
//            if (TiersClient.positionVSList == TiersClient.DisplayStatus.RIGHT) {
//                TiersClient.positionVSList = TiersClient.DisplayStatus.OFF;
//                centerPvPTiers.active = false;
//                rightPvPTiers.active = true;
//            }
//            updateRightSwitcher(buttonWidget, leftMCTiers, centerMCTiers);
//        }).bounds(centerX - 120 - 10 + 24, distance + 145, 20, 20).tooltip(Tooltip.create(Component.literal("Display MCTiers on the right"))).build();

        leftPvPTiers = Button.builder(Component.literal("←"), (buttonWidget) -> {
            TiersClient.positionVSList = TiersClient.DisplayStatus.LEFT;
//            if (TiersClient.positionMCTiers == TiersClient.DisplayStatus.LEFT) {
//                TiersClient.positionMCTiers = TiersClient.DisplayStatus.OFF;
//                leftMCTiers.active = true;
//                centerMCTiers.active = false;
//            }
//            updateLeftSwitcher(buttonWidget, centerPvPTiers, rightPvPTiers);
            updateLeftSwitcher(buttonWidget, rightPvPTiers);
        }).bounds(centerX - 10 - 12, distance + 145, 20, 20).tooltip(Tooltip.create(Component.literal("Display PvPTiers on the left"))).build();

//        centerPvPTiers = Button.builder(Component.literal("●"), (buttonWidget) -> {
//            TiersClient.positionVSList = TiersClient.DisplayStatus.OFF;
//            leftPvPTiers.active = true;
//            buttonWidget.active = false;
//            rightPvPTiers.active = true;
//            ConfigManager.saveConfig();
//        }).bounds(centerX - 10, distance + 145, 20, 20).tooltip(Tooltip.create(Component.literal("Disable PvPTiers"))).build();

        rightPvPTiers = Button.builder(Component.literal("→"), (buttonWidget) -> {
            TiersClient.positionVSList = TiersClient.DisplayStatus.RIGHT;
//            if (TiersClient.positionMCTiers == TiersClient.DisplayStatus.RIGHT) {
//                TiersClient.positionMCTiers = TiersClient.DisplayStatus.OFF;
//                centerMCTiers.active = false;
//                rightMCTiers.active = true;
//            }
//            updateRightSwitcher(buttonWidget, leftPvPTiers, centerPvPTiers);
            updateRightSwitcher(buttonWidget, leftPvPTiers);
        }).bounds(centerX - 10 + 12, distance + 145, 20, 20).tooltip(Tooltip.create(Component.literal("Display PvPTiers on the right"))).build();

//        leftSubtiers = Button.builder(Component.literal("←"), (buttonWidget) -> {
//            TiersClient.positionSubtiers = TiersClient.DisplayStatus.LEFT;
//            if (TiersClient.positionMCTiers == TiersClient.DisplayStatus.LEFT) {
//                TiersClient.positionMCTiers = TiersClient.DisplayStatus.OFF;
//                leftMCTiers.active = true;
//                centerMCTiers.active = false;
//            }
//            if (TiersClient.positionVSList == TiersClient.DisplayStatus.LEFT) {
//                TiersClient.positionVSList = TiersClient.DisplayStatus.OFF;
//                leftPvPTiers.active = true;
//                centerPvPTiers.active = false;
//            }
//            buttonWidget.active = false;
//            centerSubtiers.active = true;
//            rightSubtiers.active = true;
//            ConfigManager.saveConfig();
//        }).bounds(centerX + 120 - 10 - 24, distance + 145, 20, 20).tooltip(Tooltip.create(Component.literal("Display Subtiers on the left"))).build();
//
//        centerSubtiers = Button.builder(Component.literal("●"), (buttonWidget) -> {
//            TiersClient.positionSubtiers = TiersClient.DisplayStatus.OFF;
//            leftSubtiers.active = true;
//            buttonWidget.active = false;
//            rightSubtiers.active = true;
//            ConfigManager.saveConfig();
//        }).bounds(centerX + 120 - 10, distance + 145, 20, 20).tooltip(Tooltip.create(Component.literal("Disable Subtiers"))).build();
//
//        rightSubtiers = Button.builder(Component.literal("→"), (buttonWidget) -> {
//            TiersClient.positionSubtiers = TiersClient.DisplayStatus.RIGHT;
//            if (TiersClient.positionMCTiers == TiersClient.DisplayStatus.RIGHT) {
//                TiersClient.positionMCTiers = TiersClient.DisplayStatus.OFF;
//                centerMCTiers.active = false;
//                rightMCTiers.active = true;
//            }
//            if (TiersClient.positionVSList == TiersClient.DisplayStatus.RIGHT) {
//                TiersClient.positionVSList = TiersClient.DisplayStatus.OFF;
//                centerPvPTiers.active = false;
//                rightPvPTiers.active = true;
//            }
//            leftSubtiers.active = true;
//            centerSubtiers.active = true;
//            buttonWidget.active = false;
//            ConfigManager.saveConfig();
//        }).bounds(centerX + 120 - 10 + 24, distance + 145, 20, 20).tooltip(Tooltip.create(Component.literal("Display Subtiers on the right"))).build();

//        switch (TiersClient.positionMCTiers) {
//            case RIGHT -> rightMCTiers.active = false;
//            case OFF -> centerMCTiers.active = false;
//            case LEFT -> leftMCTiers.active = false;
//        }

        switch (TiersClient.positionVSList) {
            case RIGHT -> rightPvPTiers.active = false;
//            case OFF -> centerPvPTiers.active = false;
            case LEFT -> leftPvPTiers.active = false;
        }

//        switch (TiersClient.positionSubtiers) {
//            case RIGHT -> rightSubtiers.active = false;
//            case OFF -> centerSubtiers.active = false;
//            case LEFT -> leftSubtiers.active = false;
//        }

        activeRightMode = Button.builder(Icons.CYCLE, (ignored) -> {
            TiersClient.cycleRightMode();
            autoKitDetect.setMessage(Component.literal(TiersClient.toggleAutoKitDetect ? "Disable auto kit detect" : "Enable auto kit detect"));
            autoKitDetect.setTooltip(Tooltip.create(Component.literal((TiersClient.toggleAutoKitDetect ?
                    "Disable auto kit detect: you will need to press " + autoDetectKitBoundKey + " to auto-detect the current gamemode" :
                    "Enable auto kit detect: Tiers will always scan your inventory to display the right gamemode (instead of pressing " + autoDetectKitBoundKey + ")"))));
        }).bounds(centerX + 90 + 4, distance + 75, 20, 20).tooltip(Tooltip.create(Component.literal("Cycle active right gamemode (press " + cycleRightBoundKey + " in game)"))).build();

        activeLeftMode = Button.builder(Icons.CYCLE, (ignored) -> {
            TiersClient.cycleLeftMode();
            autoKitDetect.setMessage(Component.literal(TiersClient.toggleAutoKitDetect ? "Disable auto kit detect" : "Enable auto kit detect"));
            autoKitDetect.setTooltip(Tooltip.create(Component.literal((TiersClient.toggleAutoKitDetect ?
                    "Disable auto kit detect: you will need to press " + autoDetectKitBoundKey + " to auto-detect the current gamemode" :
                    "Enable auto kit detect: Tiers will always scan your inventory to display the right gamemode (instead of pressing " + autoDetectKitBoundKey + ")"))));
        }).bounds(centerX - 90 - 20 - 4, distance + 75, 20, 20).tooltip(Tooltip.create(Component.literal("Cycle active left gamemode (press " + cycleLeftBoundKey + " in game)"))).build();

        Button useClassicIcons = Button.builder(TiersClient.activeIcons == Icons.Type.CLASSIC ? Component.literal("●") : Component.empty(), (buttonWidget) -> {
            buttonWidget.setMessage(TiersClient.activeIcons == Icons.Type.CLASSIC ? Component.literal("●") : Component.empty());
            TiersClient.changeIcons(Icons.Type.CLASSIC, true);
        }).bounds(5, 5, 20, 20).tooltip(Tooltip.create(Component.literal("Use classic styled icons and colors"))).build();

        Button usePvPTiersIcons = Button.builder(TiersClient.activeIcons == Icons.Type.PVPTIERS ? Component.literal("●") : Component.empty(), (buttonWidget) -> {
            buttonWidget.setMessage(TiersClient.activeIcons == Icons.Type.PVPTIERS ? Component.literal("●") : Component.empty());
            TiersClient.changeIcons(Icons.Type.PVPTIERS, true);
        }).bounds(5, 30, 20, 20).tooltip(Tooltip.create(Component.literal("Use PvPTiers styled icons and colors"))).build();

        Button useMCTiersIcons = Button.builder(TiersClient.activeIcons == Icons.Type.MCTIERS ? Component.literal("●") : Component.empty(), (buttonWidget) -> {
            buttonWidget.setMessage(TiersClient.activeIcons == Icons.Type.MCTIERS ? Component.literal("●") : Component.empty());
            TiersClient.changeIcons(Icons.Type.MCTIERS, true);
        }).bounds(5, 55, 20, 20).tooltip(Tooltip.create(Component.literal("Use MCTiers styled icons and colors"))).build();

        switch (TiersClient.activeIcons) {
            case CLASSIC -> useClassicIcons.active = false;
            case PVPTIERS -> usePvPTiersIcons.active = false;
            case MCTIERS -> useMCTiersIcons.active = false;
        }

        updateVisibilities();

//        Stream.of(toggleMod, toggleIcons, toggleTab, toggleChat, toggleSeparatorMode, cycleDisplayMode, autoKitDetect, clearPlayerCache, leftMCTiers, centerMCTiers, rightMCTiers, leftPvPTiers, centerPvPTiers, rightPvPTiers, leftSubtiers, centerSubtiers, rightSubtiers, activeRightMode, activeLeftMode, enableOwnProfile, useClassicIcons, usePvPTiersIcons, useMCTiersIcons)
//                .forEach(this::addRenderableWidget);
//        Stream.of(toggleMod, toggleIcons, toggleTab, toggleChat, toggleSeparatorMode, cycleDisplayMode, autoKitDetect, clearPlayerCache, leftPvPTiers, centerPvPTiers, rightPvPTiers, activeRightMode, activeLeftMode, enableOwnProfile, useClassicIcons, usePvPTiersIcons, useMCTiersIcons)
//                .forEach(this::addRenderableWidget);
        Stream.of(toggleMod, toggleIcons, toggleTab, toggleChat, toggleSeparatorMode, cycleDisplayMode, autoKitDetect, clearPlayerCache, leftPvPTiers, rightPvPTiers, activeRightMode, activeLeftMode, enableOwnProfile, useClassicIcons, usePvPTiersIcons, useMCTiersIcons)
                .forEach(this::addRenderableWidget);
    }

//    private void updateRightSwitcher(Button Button, Button leftMCTiers, Button centerMCTiers) {
//        if (TiersClient.positionSubtiers == TiersClient.DisplayStatus.RIGHT) {
//            TiersClient.positionSubtiers = TiersClient.DisplayStatus.OFF;
//            centerSubtiers.active = false;
//            rightSubtiers.active = true;
//        }
//        leftMCTiers.active = true;
//        centerMCTiers.active = true;
//        Button.active = false;
//        ConfigManager.saveConfig();
//    }
//
//    private void updateLeftSwitcher(Button Button, Button centerMCTiers, Button rightMCTiers) {
//        if (TiersClient.positionSubtiers == TiersClient.DisplayStatus.LEFT) {
//            TiersClient.positionSubtiers = TiersClient.DisplayStatus.OFF;
//            leftSubtiers.active = true;
//            centerSubtiers.active = false;
//        }
//        Button.active = false;
//        centerMCTiers.active = true;
//        rightMCTiers.active = true;
//        ConfigManager.saveConfig();
//    }

    private void updateRightSwitcher(Button Button, Button leftMCTiers) {
        leftMCTiers.active = true;
        Button.active = false;
        ConfigManager.saveConfig();
    }

    private void updateLeftSwitcher(Button Button, Button rightMCTiers) {
        Button.active = false;
        rightMCTiers.active = true;
        ConfigManager.saveConfig();
    }

//    private void updateVisibilities() {
//        activeRightMode.visible = TiersClient.positionMCTiers == TiersClient.DisplayStatus.RIGHT || TiersClient.positionVSList == TiersClient.DisplayStatus.RIGHT || TiersClient.positionSubtiers == TiersClient.DisplayStatus.RIGHT;
//        activeLeftMode.visible = TiersClient.positionMCTiers == TiersClient.DisplayStatus.LEFT || TiersClient.positionVSList == TiersClient.DisplayStatus.LEFT || TiersClient.positionSubtiers == TiersClient.DisplayStatus.LEFT;
//    }

    private void updateVisibilities() {
        activeRightMode.visible = TiersClient.positionVSList == TiersClient.DisplayStatus.RIGHT;
        activeLeftMode.visible = TiersClient.positionVSList == TiersClient.DisplayStatus.LEFT;
    }

    private void drawPlayerAvatar(GuiGraphics graphics, int x, int y) {
        if (imageReady) {
            if (ownProfile.imageSaved == 1 || ownProfile.imageSaved == 2)
                graphics.blit(playerAvatarTexture, x - height / 10 / 2, y, height / 10, (int) (height / 4.166), 0, 0, height / 10, (int) (height / 4.166));
            else if (ownProfile.imageSaved < 6 && ownProfile.imageSaved > 2)
                graphics.blit(playerAvatarTexture, x - height / 7 / 2, y, height / 7, (int) (height / 4.145), 0, 0, height / 7, (int) (height / 4.145));
        } else if (ownProfile.imageSaved != 0) {
            loadPlayerAvatar();
        } else if (ownProfile.numberOfImageRequests >= 6)
            graphics.drawCenteredString(font, Component.literal(ownProfile.name + "'s skin failed to load. Restart game to retry"), x, y + 50, ColorControl.getColorMinecraftStandard("red"));
    }

    private void loadPlayerAvatar() {
        if (imageReady)
            return;

        try (FileInputStream fileInputStream = new FileInputStream(FabricLoader.getInstance().getGameDir().resolve("cache/tiers/" + (useOwnProfile ? ownProfile.uuid : defaultProfile.uuid) + ".png").toFile())) {
            Minecraft.getInstance().getTextureManager().register(playerAvatarTexture, new DynamicTexture(() -> "tiers_avatar", NativeImage.read(fileInputStream)));
            imageReady = true;
        } catch (IOException ignored) {
            LOGGER.warn("Error loading player skin");
        }
    }

    public static Screen getConfigScreen(Screen ignoredScreen) {
        return new ConfigScreen();
    }
}
