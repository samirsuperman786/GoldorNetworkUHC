package com.goldornetwork.uhc.managers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.GameStartEvent;

public class BoardManager implements Listener{

	private UHC plugin;
	private TeamManager teamM;

	public BoardManager(UHC plugin, TeamManager teamM) {
		//plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin=plugin;
		this.teamM=teamM;
	}
	
	public void setup(){
	//initializeScoreBoard();	
	}
	
	@EventHandler
	public void on(GameStartEvent e){
		displayHealth();
	}
	
	@EventHandler
	public void on(PlayerDeathEvent e){
		if(teamM.isTeamsEnabled()){
			ScoreboardManager manager = Bukkit.getServer().getScoreboardManager();
			Scoreboard board = manager.getMainScoreboard();
			if(board.getTeam(teamM.getTeamOfPlayer(e.getEntity())).hasPlayer(e.getEntity())){
				board.getTeam(teamM.getTeamOfPlayer(e.getEntity())).removePlayer(e.getEntity());
			}
		}
	}
	/**
	 * Will setup the scoreboard to the initial values
	 */
	public void initializeScoreBoard(){
		ScoreboardManager manager = Bukkit.getServer().getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective header = board.registerNewObjective("header", "dummy");
		header.setDisplaySlot(DisplaySlot.SIDEBAR);
		header.setDisplayName(ChatColor.GOLD + "GoldorNetwork");
		Objective objective = board.registerNewObjective("mapsize", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.AQUA + "MapSize");
		Score score = objective.getScore("");
		score.setScore(plugin.getConfig().getInt("radius"));
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			p.setScoreboard(board);
		}
	}
	
	
	public void displayHealth(){
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		 
		Objective objective = board.registerNewObjective("showhealth", "health");
		objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		//objective.setDisplayName(ChatColor.RED + "\u2764");
		 
		for(Player online : Bukkit.getOnlinePlayers()){
		  online.setScoreboard(board);
		  online.setHealth(online.getHealth()); 
		}
	}
}
