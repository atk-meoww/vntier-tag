package com.tiers.textures;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.util.HashMap;

public class ColorControl {
    private static final HashMap<String, Integer> colors = new HashMap<>();

    public static void updateColors(JsonObject jsonObject) {
        colors.clear();
        jsonObject.keySet().forEach(key -> colors.put(key, Integer.parseUnsignedInt(jsonObject.get(key).getAsString().replace("#", ""), 16)));

        Icons.GLOBE = Component.literal("\uF000").setStyle(Style.EMPTY.withColor(getColorMinecraftStandard("region")).withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "misc"))));
        Icons.OVERALL = Component.literal("\uF001").setStyle(Style.EMPTY.withColor(getColorMinecraftStandard("overall")).withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "misc"))));
    }

    public static int getColor(String colorName) {
        return colors.getOrDefault(colorName, 0xaaaaaa);
    }

    public static int getColorMinecraftStandard(String colorName) {
        return 0xff000000 | (colors.getOrDefault(colorName, 0xaaaaaa) & 0x00ffffff);
    }
}