package com.tiers.misc;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.tiers.TiersClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class CommandRegister {
    private static final SuggestionProvider<FabricClientCommandSource> PLAYERS = (ignored, suggestionsBuilder) -> suggestPlayers(suggestionsBuilder);

    private static CompletableFuture<Suggestions> suggestPlayers(SuggestionsBuilder suggestionsBuilder) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.getConnection() == null)
            return suggestionsBuilder.buildFuture();

        for (PlayerInfo playerListEntry : minecraft.getConnection().getOnlinePlayers())
            if (SharedSuggestionProvider.matchesSubStr(suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT), playerListEntry.getProfile().name().toLowerCase(Locale.ROOT)) && playerListEntry.getProfile().name().length() > 2)
                suggestionsBuilder.suggest(playerListEntry.getProfile().name(), () -> "Search tiers for " + playerListEntry.getProfile().name());

        if (SharedSuggestionProvider.matchesSubStr(suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT), "-config"))
            suggestionsBuilder.suggest("-config", () -> "Open Tiers config screen");

        return suggestionsBuilder.buildFuture();
    }

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, ignored2) -> commandDispatcher.register(
                ClientCommandManager.literal("tiers").executes(ignored -> {
                            TiersClient.toggleMod(null);
                            return 1;
                        })
                        .then(ClientCommandManager.argument("Name", StringArgumentType.string()).suggests(PLAYERS).executes(context -> {
                                    TiersClient.tiersCommand(StringArgumentType.getString(context, "Name"));
                                    return 1;
                                })
                        )
        ));
    }
}
