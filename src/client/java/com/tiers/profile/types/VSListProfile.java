package com.tiers.profile.types;



import com.google.gson.JsonObject;

import com.google.gson.JsonParser;

import com.tiers.misc.Mode;

import com.tiers.profile.GameMode;

import com.tiers.profile.Status;

import com.tiers.textures.Icons;

import net.minecraft.network.chat.Component;

import net.minecraft.network.chat.Style;

import net.minecraft.util.CommonColors;



/**

 * Profile source cho VSList (vslist.sokimc.vn).

 *

 * QUAN TRỌNG: response của VSList (GET /api/player/:username) có shape khác

 * hẳn mctiers/pvptiers, nên class này KHÔNG dùng SuperProfile.parseJson() gốc

 * mà override hoàn toàn. Đã verify schema + điểm số dựa trên response thật:

 *

 * {

 *   "success": true,

 *   "player": {

 *     "uuid": "1453597510525255741",   // đây là Discord ID, KHÔNG phải Mojang UUID

 *     "username": "yowx",

 *     "region": "vn",                   // free text, không theo chuẩn EU/NA/AS

 *     "isPremium": true,

 *     "verifiedAt": 1786155387651,

 *     "tiers": { "sword": "LT3", "pot": "LT3", ... },  // string đã format sẵn, không có peak/retired/attained

 *     "views": 69,

 *     "overallScore": 35,

 *     "overallRank": 1

 *   }

 * }

 *

 * Gọi API bằng USERNAME, không phải UUID.

 */

public class VSListProfile extends SuperProfile {



    public VSListProfile(String apiUrl, String username, String extra) {

        super();

        addGamemodes();

        buildRequest(apiUrl, username, extra);

    }



    public VSListProfile(String json) {

        super();

        addGamemodes();

        parseJson(json);

    }



    private void addGamemodes() {

        gameModes.add(new GameMode(Mode.VSLIST_VANILLA, "vanilla"));

        gameModes.add(new GameMode(Mode.VSLIST_UHC, "uhc"));

        gameModes.add(new GameMode(Mode.VSLIST_POT, "pot"));

        gameModes.add(new GameMode(Mode.VSLIST_NETHOP, "nethop"));

        gameModes.add(new GameMode(Mode.VSLIST_SMP, "smp"));

        gameModes.add(new GameMode(Mode.VSLIST_SWORD, "sword"));

        gameModes.add(new GameMode(Mode.VSLIST_AXE, "axe"));

        gameModes.add(new GameMode(Mode.VSLIST_MACE, "mace"));

    }



    @Override

    public void parseJson(String json) {

        if (json == null || JsonParser.parseString(json).isJsonNull()) {

            status = Status.API_ISSUE;

            return;

        }



        JsonObject root = JsonParser.parseString(json).getAsJsonObject();



        if (!root.has("success") || !root.get("success").getAsBoolean() || !root.has("player")) {

            status = Status.NOT_EXISTING;

            return;

        }



        JsonObject player = root.getAsJsonObject("player");



        region = (player.has("region") && !player.get("region").isJsonNull())

                ? player.get("region").getAsString() : "unknown";

        overallPosition = player.has("overallRank") ? player.get("overallRank").getAsInt() : 0;

        points = player.has("overallScore") ? player.get("overallScore").getAsInt() : 0;



        // VSList region là free text (vd "vn"), không map được vào bộ icon

        // EU/NA/AS/AU/SA/ME/AF/OC gốc -> hiển thị dạng chữ thường, không icon.

        displayedRegion = Component.literal(region.toUpperCase())

                .setStyle(Style.EMPTY.withColor(CommonColors.WHITE));

        regionTooltip = displayedRegion;



        displayedOverall = getVSListOverallText();

        overallTooltip = getVSListOverallTooltip();



        if (player.has("tiers") && player.get("tiers").isJsonObject()) {

            JsonObject tiers = player.getAsJsonObject("tiers");

            for (GameMode gameMode : gameModes) {

                if (tiers.has(gameMode.parsingName) && !tiers.get(gameMode.parsingName).isJsonNull())

                    gameMode.parseSimpleTier(tiers.get(gameMode.parsingName).getAsString());

                else

                    gameMode.status = Status.NOT_EXISTING;

            }

        }

        recomputeHighest();



        status = Status.READY;

        originalJson = json;

        triggerUpdate();

    }



    private void recomputeHighest() {

        GameMode best = null;

        int bestPoints = 0;

        for (GameMode gameMode : gameModes) {

            if (gameMode.status == Status.READY && gameMode.getTierPoints(false) > bestPoints) {

                best = gameMode;

                bestPoints = gameMode.getTierPoints(false);

            }

        }

        highest = best;

    }



    // Mốc rank dưới đây là GỢI Ý ban đầu dựa trên thang điểm tối đa lý thuyết

    // của VSList (8 mode x 10 điểm/mode = 80). Chỉnh lại theo ý bạn.

    private Component getVSListOverallText() {

        String posString = "#" + overallPosition;

        if (points >= 60) return Icons.colorText(posString, "master");

        else if (points >= 40) return Icons.colorText(posString, "ace");

        else if (points >= 25) return Icons.colorText(posString, "specialist");

        else if (points >= 12) return Icons.colorText(posString, "cadet");

        else if (points >= 5) return Icons.colorText(posString, "novice");

        return Icons.colorText(posString, "rookie");

    }



    private Component getVSListOverallTooltip() {

        String label;

        if (points >= 60) label = "Master";

        else if (points >= 40) label = "Ace";

        else if (points >= 25) label = "Specialist";

        else if (points >= 12) label = "Cadet";

        else if (points >= 5) label = "Novice";

        else label = "Rookie";



        String tooltip = label + "\n\nPoints: " + points;

        return Component.literal(tooltip).setStyle(displayedOverall.getStyle());

    }

}