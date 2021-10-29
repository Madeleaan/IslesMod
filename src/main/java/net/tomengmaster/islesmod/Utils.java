package net.tomengmaster.islesmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
    public static boolean onIsles() {
        MinecraftClient client = MinecraftClient.getInstance();
        return !client.isInSingleplayer() && client.getCurrentServerEntry().address.contains(".skyblockisles.com");
    }

    public static List<String> getScoreboard() {
        Scoreboard board = MinecraftClient.getInstance().world.getScoreboard();
        ScoreboardObjective objective = board.getObjectiveForSlot(1);
        List<String> lines = new ArrayList<>();
        for(ScoreboardPlayerScore score:board.getAllPlayerScores(objective)) {
            Team team = board.getPlayerTeam(score.getPlayerName());
            String line = team.getPrefix().getString() + team.getSuffix().getString();
            if(line.trim().length() > 0) {
                String formatted = Formatting.strip(line);
                lines.add(formatted);
            }
        }
        if(objective == null) {
            return lines;
        }
        lines.add(objective.getDisplayName().getString());
        Collections.reverse(lines);
        IslesMod.LOGGER.info(lines);
        return lines;
    }
}
