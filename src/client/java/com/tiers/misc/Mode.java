package com.tiers.misc;

import com.tiers.textures.Icons;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.util.Arrays;
import java.util.Locale;

public enum Mode {
    MCTIERS_VANILLA(Category.MCTIERS, "\uF000", "Vanilla"),
    MCTIERS_UHC(Category.MCTIERS, "\uF001", "UHC"),
    MCTIERS_POT(Category.MCTIERS, "\uF002", "Pot"),
    MCTIERS_NETH_OP(Category.MCTIERS, "\uF003", "Neth Op"),
    MCTIERS_SMP(Category.MCTIERS, "\uF004", "Smp"),
    MCTIERS_SWORD(Category.MCTIERS, "\uF005", "Sword"),
    MCTIERS_AXE(Category.MCTIERS, "\uF006", "Axe"),
    MCTIERS_MACE(Category.MCTIERS, "\uF007", "Mace"),

    PVPTIERS_CRYSTAL(Category.PVPTIERS, "\uF000", "Crystal"),
    PVPTIERS_SWORD(Category.PVPTIERS, "\uF005", "Sword"),
    PVPTIERS_UHC(Category.PVPTIERS, "\uF001", "UHC"),
    PVPTIERS_POT(Category.PVPTIERS, "\uF002", "Pot"),
    PVPTIERS_NETH_POT(Category.PVPTIERS, "\uF003", "Neth Pot"),
    PVPTIERS_SMP(Category.PVPTIERS, "\uF004", "Smp"),
    PVPTIERS_AXE(Category.PVPTIERS, "\uF006", "Axe"),
    PVPTIERS_MACE(Category.PVPTIERS, "\uF007", "Mace"),

    SUBTIERS_MINECART(Category.SUBTIERS, "\uF000", "Minecart"),
    SUBTIERS_DIAMOND_VANILLA(Category.SUBTIERS, "\uF001", "Diamond Vanilla"),
    SUBTIERS_DEBUFF(Category.SUBTIERS, "\uF002", "DeBuff"),
    SUBTIERS_ELYTRA(Category.SUBTIERS, "\uF003", "Elytra"),
    SUBTIERS_SPEED(Category.SUBTIERS, "\uF004", "Speed"),
    SUBTIERS_CREEPER(Category.SUBTIERS, "\uF005", "Creeper"),
    SUBTIERS_MANHUNT(Category.SUBTIERS, "\uF006", "Manhunt"),
    SUBTIERS_DIAMOND_SMP(Category.SUBTIERS, "\uF007", "Diamond Smp"),
    SUBTIERS_BOW(Category.SUBTIERS, "\uF008", "Bow"),
    SUBTIERS_BED(Category.SUBTIERS, "\uF009", "Bed"),
    SUBTIERS_OG_VANILLA(Category.SUBTIERS, "\uF00A", "OG Vanilla"),
    SUBTIERS_TRIDENT(Category.SUBTIERS, "\uF00B", "Trident"),

    VSLIST_VANILLA(Category.VSLIST, "\uF000", "Vanilla"),
    VSLIST_UHC(Category.VSLIST, "\uF001", "UHC"),
    VSLIST_POT(Category.VSLIST, "\uF002", "Pot"),
    VSLIST_NETHOP(Category.VSLIST, "\uF003", "NethOP"),
    VSLIST_SMP(Category.VSLIST, "\uF004", "Smp"),
    VSLIST_SWORD(Category.VSLIST, "\uF005", "Sword"),
    VSLIST_AXE(Category.VSLIST, "\uF006", "Axe"),
    VSLIST_MACE(Category.VSLIST, "\uF007", "Mace");

    private final Category category;
    private final String unicode;
    private final String label;

    Mode(Category category, String unicode, String label) {
        this.category = category;
        this.unicode = unicode;
        this.label = label;
    }

    public enum Category {
        MCTIERS,
        PVPTIERS,
        SUBTIERS,
        VSLIST
    }

    public Component getIcon() {
        Identifier identifier = switch (category) {
            case MCTIERS -> Icons.identifierMCTiers;
            case PVPTIERS -> Icons.identifierPvPTiers;
            case SUBTIERS -> Icons.identifierSubtiers;
            case VSLIST -> Icons.identifierVSList;
        };
        return Component.literal(unicode).setStyle(Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(identifier)).withColor(0xFFFFFF));
    }

    public Component getIconTag() {
        Identifier identifier = switch (category) {
            case MCTIERS -> Icons.identifierMCTiersTags;
            case PVPTIERS -> Icons.identifierPvPTiersTags;
            case SUBTIERS -> Icons.identifierSubtiersTags;
            case VSLIST -> Icons.identifierVSListTags;
        };
        return Component.literal(unicode).setStyle(Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(identifier)).withColor(0xFFFFFF));
    }

    public Component getTextLabel() {
        return Icons.colorText(label, name().toLowerCase(Locale.ROOT));
    }

    public static Mode[] getMCTiersValues() {
        return Arrays.stream(values()).filter(mode -> mode.toString().contains("MCTIERS")).toArray(Mode[]::new);
    }

    public static Mode[] getPvPTiersValues() {
        return Arrays.stream(values()).filter(mode -> mode.toString().contains("PVPTIERS")).toArray(Mode[]::new);
    }

    public static Mode[] getSubtiersValues() {
        return Arrays.stream(values()).filter(mode -> mode.toString().contains("SUBTIERS")).toArray(Mode[]::new);
    }

    public static Mode[] getVSListValues() {
        return Arrays.stream(values()).filter(mode -> mode.toString().contains("VSLIST")).toArray(Mode[]::new);
    }
}