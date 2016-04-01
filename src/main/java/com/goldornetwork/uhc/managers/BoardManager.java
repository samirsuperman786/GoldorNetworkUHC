package com.goldornetwork.uhc.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class BoardManager {

	private TeamManager teamM;

	public BoardManager(TeamManager teamM) {
		this.teamM=teamM;
	}

	public void initializeScoreBoard(){
		ScoreboardManager manager = Bukkit.getServer().getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective objective = board.registerNewObjective("MapSize", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.AQUA + "MapSize");
		Score score = objective.getScore("");
		score.setScore(100);
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			p.setScoreboard(board);
		}
	}
}
