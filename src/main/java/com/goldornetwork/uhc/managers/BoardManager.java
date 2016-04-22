package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.GameStartEvent;
import com.goldornetwork.uhc.utils.MessageSender;

public class BoardManager implements Listener{

	private UHC plugin;
	private TeamManager teamM;
	private Scoreboard mainBoard;
	private Team observerTeam;
	
	private List<Team> activeTeams = new ArrayList<Team>(); 
	private Map<String, Team> teamScoreBoards = new HashMap<String, Team>();
	private Map<UUID, String> teamOfPlayer = new HashMap<UUID, String>();
	private Map<String, BukkitTask> scoreboardUpdaters = new HashMap<String, BukkitTask>();
	
	public BoardManager(UHC plugin) {
		this.plugin=plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}


	public void setup(TeamManager teamM){
		this.teamM=teamM;
		mainBoard=plugin.getServer().getScoreboardManager().getNewScoreboard();
		for(Player all : Bukkit.getServer().getOnlinePlayers()){
			all.setScoreboard(mainBoard);
			all.setPlayerListName(ChatColor.stripColor(all.getName()));
		}
		initializeScoreBoard();	
	}

	private void initializeScoreBoard() {
		observerTeam = mainBoard.registerNewTeam("observers");
		observerTeam.setPrefix(ChatColor.AQUA + "[Observer]");
	}

	public void addPlayerToObserver(OfflinePlayer p){
		observerTeam.addPlayer(p);
		if(p.isOnline()){
			Player target = (Player) p;
			target.setPlayerListName(ChatColor.AQUA + target.getName());
		}
	}
	
	public void createTeam(String team){
		Scoreboard teamBoard = Bukkit.getScoreboardManager().getNewScoreboard();
		/*Objective o = teamBoard.registerNewObjective("showhealth", "health");
		o.setDisplaySlot(DisplaySlot.BELOW_NAME);
		o.setDisplayName("/ 20");
		*/
		Team newTeam = teamBoard.registerNewTeam(team);
		newTeam.setCanSeeFriendlyInvisibles(true);
		newTeam.setAllowFriendlyFire(false);
		newTeam.setPrefix(ChatColor.GREEN + "");
		
		Team otherPlayers = teamBoard.registerNewTeam("others");
		otherPlayers.setPrefix(ChatColor.RED + "");
		
		teamScoreBoards.put(team, newTeam);
		activeTeams.add(newTeam);
		
		
		//updater(team, teamBoard);
	}
	
	public void removeTeam(String team){
		activeTeams.remove(teamScoreBoards.get(team));
		teamScoreBoards.get(team).unregister();
		teamScoreBoards.remove(team);
		//cancelUpdater(team);
	}
	
	public void addPlayerToTeam(String team, Player p){
		teamScoreBoards.get(team).addPlayer(p);
		teamOfPlayer.put(p.getUniqueId(), team.toLowerCase());
		p.setScoreboard(teamScoreBoards.get(team).getScoreboard());
		
	}
	public void removePlayerFromTeam(String team, OfflinePlayer p){
		teamScoreBoards.get(team).removePlayer(p);
		teamOfPlayer.remove(p.getUniqueId());
		if(p.isOnline()){
			Player target = (Player) p;
			target.setScoreboard(mainBoard);
		}
		
		
	}
	public Scoreboard getScoreboardOfPlayer(Player p){
		return teamScoreBoards.get(teamOfPlayer.get(p.getUniqueId())).getScoreboard();
	}
	
	private void updater(String team, Scoreboard sc){
		BukkitTask run = new BukkitRunnable() {
			
			@Override
			public void run() {
				for(OfflinePlayer teamates : teamScoreBoards.get(team).getPlayers()){
					if(teamates.isOnline()){
						Player p = teamates.getPlayer();
						p.setScoreboard(sc);
					}
				}
				
			}
		}.runTaskTimer(plugin, 0L, 20L);
		scoreboardUpdaters.put(team, run);
	}
	
	private void cancelUpdater(String team){
		scoreboardUpdaters.get(team).cancel();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(GameStartEvent e){
		for(Team team : activeTeams){
			for(UUID u : teamM.getPlayersInGame()){
				if(!(team.getPlayers().contains(Bukkit.getOfflinePlayer(u)))){
					team.getScoreboard().getTeam("others").addPlayer(Bukkit.getOfflinePlayer(u));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(teamOfPlayer.containsKey(p.getUniqueId())){
			p.setScoreboard(getScoreboardOfPlayer(p));
		}
	}
	

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerDeathEvent e){
		Player p = e.getEntity();
		if(teamOfPlayer.containsKey(p.getUniqueId())){
			for(Team team : activeTeams){
					if(!(team.getPlayers().contains(Bukkit.getOfflinePlayer(p.getUniqueId())))){
						team.getScoreboard().getTeam("others").removePlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
					}
				
			}
			getScoreboardOfPlayer(p).getTeam(teamOfPlayer.get(p.getUniqueId())).removePlayer(p);
			p.setScoreboard(mainBoard);
		}
		else{
			p.setScoreboard(mainBoard);
		}
	}




}
