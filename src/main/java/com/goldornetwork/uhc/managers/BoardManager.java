package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.managers.world.customevents.GameOpenEvent;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;
import com.goldornetwork.uhc.managers.world.customevents.PVPEnableEvent;
import com.goldornetwork.uhc.managers.world.customevents.UHCKillEvent;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class BoardManager implements Listener{


	private UHC plugin;
	private TeamManager teamM;
	private Scoreboard mainBoard;
	private Team observerTeam;
	private WorldManager worldM;
	private TimerManager timerM;


	private Map<String, List<Team>> teamScoreBoards = new HashMap<String, List<Team>>();
	private Map<UUID, Team> individualScoreboards = new HashMap<UUID, Team>();
	private Map<UUID, Objective> individualScoreBoardHeaders = new HashMap<UUID, Objective>();
	private Set<UUID> invisible = new HashSet<UUID>();
	private Objective header;

	private Map<UUID, Integer> playerKills = new HashMap<UUID, Integer>();

	public BoardManager(UHC plugin) {
		this.plugin=plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void setup(TeamManager teamM, WorldManager worldM, TimerManager timerM){
		this.teamM=teamM;
		this.worldM=worldM;
		this.timerM=timerM;
		mainBoard=Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		initializeObserverBoard();
		invisibleChecker();
		for(Player all : Bukkit.getServer().getOnlinePlayers()){
			all.setScoreboard(mainBoard);
			all.setPlayerListName(ChatColor.stripColor(all.getName()));
		}
	}

	private void initializeInfo(Objective header){
		Score teamsize = header.getScore(ChatColor.AQUA + "Max Team Size: ");
		teamsize.setScore(teamM.getTeamSize());
		updater(teamsize, 20L, new Callable<Integer>() {
			public Integer call(){
				return teamM.getTeamSize();
			}
		});

		Score playersInGame = header.getScore(ChatColor.GOLD + "Players: ");
		updater(playersInGame, 40L, new Callable<Integer>(){
			public Integer call(){
				return teamM.getPlayersInGame().size();
			}
		});

		Score teamsLeft = header.getScore(ChatColor.GOLD + "Teams Left: ");
		updater(teamsLeft, 40L, new Callable<Integer>(){
			public Integer call(){
				return teamM.getActiveTeams().size();
			}
		});

		Score currentBorder = header.getScore(ChatColor.AQUA + "Border Radius: ");
		currentBorder.setScore((int) worldM.getUHCWorld().getWorldBorder().getSize());
		updater(currentBorder, 100L, new Callable<Integer>() {
			public Integer call(){
				return (int) (10*(Math.round((worldM.getUHCWorld().getWorldBorder().getSize())/2)/10));
			}
		});
	}

	private void initializeObserverBoard() {

		header = mainBoard.registerNewObjective("header", "dummy");
		header.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "GoldorNetwork");
		header.setDisplaySlot(DisplaySlot.SIDEBAR);

		Objective objective = mainBoard.registerNewObjective("showhealth", "health");
		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objective.setDisplayName(ChatColor.RED + "\u2665");

		initializeInfo(header);

		observerTeam = mainBoard.registerNewTeam(ChatColor.AQUA + "Observers");
		observerTeam.setPrefix(ChatColor.AQUA + "[Observer] ");

		Team otherPlayers = mainBoard.registerNewTeam("others");
		otherPlayers.setPrefix(ChatColor.RED + "");

	}

	private void initializeBoardForPlayer(Player target, String team){
		Scoreboard teamBoard = Bukkit.getScoreboardManager().getNewScoreboard();
		String teamToAdd = teamM.getTeamNameProper(team);

		Team newTeam = teamBoard.registerNewTeam(team);
		newTeam.setCanSeeFriendlyInvisibles(true);
		newTeam.setAllowFriendlyFire(false);
		newTeam.setPrefix(ChatColor.GREEN + "");

		Team otherPlayers = teamBoard.registerNewTeam("others");
		otherPlayers.setPrefix(ChatColor.RED + "");

		Team observers = teamBoard.registerNewTeam(ChatColor.AQUA + "Observers");
		observers.setPrefix(ChatColor.AQUA + "");
		
		Objective header = teamBoard.registerNewObjective("header", "dummy");
		header.setDisplaySlot(DisplaySlot.SIDEBAR);
		header.setDisplayName(teamM.getColorOfTeam(team) + ChatColor.BOLD + teamToAdd);

		Objective health = teamBoard.registerNewObjective("healthDisplayer", "health");
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);
		health.setDisplayName(ChatColor.RED + "\u2665");

		initializeInfo(header);

		Score timeTillMatch = header.getScore(ChatColor.AQUA + "Match starts in: ");
		updater(timeTillMatch, 20L, 0L, new Callable<Integer>() {
			public Integer call(){
				return timerM.getTimeTillMatchStart();
			}
		});

		mainBoard.getTeam("others").addPlayer(target);
		
		individualScoreBoardHeaders.put(target.getUniqueId(), header);
		teamScoreBoards.get(team).add(newTeam);
		individualScoreboards.put(target.getUniqueId(), newTeam);
		playerKills.put(target.getUniqueId(), 0);

		newTeam.addPlayer(target);
		target.setScoreboard(teamBoard);

		
		for(UUID players : teamM.getPlayersInGame()){
			if(teamM.areTeamMates(target.getUniqueId(), players)==false){
				otherPlayers.addPlayer(PlayerUtils.getOfflinePlayer(players));
				getScoreboardOfPlayer(players).getTeam("others").addPlayer(target);
			}
		}
		for(UUID u : teamM.getPlayersOnATeam(team)){
			if(individualScoreboards.get(u).getScoreboard().getTeam("others").hasPlayer(target)){
				individualScoreBoardHeaders.get(u).getScoreboard().getTeam("others").removePlayer(target);
			}
			newTeam.addPlayer(PlayerUtils.getOfflinePlayer(u));
			individualScoreboards.get(u).addPlayer(target);
		}
	}

	private void invisibleChecker(){

		new BukkitRunnable() {
			@Override
			public void run() {
				List<UUID> toRemove = new ArrayList<UUID>();

				for(UUID invis : invisible){
					if(teamM.isPlayerInGame(invis)){
						if(plugin.getServer().getOfflinePlayer(invis).isOnline()){
							Player target = plugin.getServer().getPlayer(invis);
							boolean hasInvis = false;
							for(PotionEffect effect : target.getActivePotionEffects()){
								if(effect.getType().equals(PotionEffectType.INVISIBILITY)){
									hasInvis = true;
								}
							}
							if(hasInvis==false){
								playerIsVisible(invis);
								toRemove.add(target.getUniqueId());
							}
						}
					}
					else{
						toRemove.add(invis);
					}
				}
				invisible.removeAll(toRemove);

				List<UUID> toAdd = new ArrayList<UUID>();

				for(Player online : Bukkit.getOnlinePlayers()){
					if(invisible.contains(online.getUniqueId())==false){
						if(teamM.isPlayerInGame(online.getUniqueId())){
							for(PotionEffect effect : online.getActivePotionEffects()){
								if(effect.getType().equals(PotionEffectType.INVISIBILITY)){
									toAdd.add(online.getUniqueId());
									playerIsInvisible(online.getUniqueId());
								}
							}
						}
					}
				}
				invisible.addAll(toAdd);
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	@SuppressWarnings("deprecation")
	private void playerIsInvisible(UUID u){

		for(String team : teamM.getActiveTeams()){
			if(teamM.getTeamOfPlayer(u)!=team){
				for(Team teamBoards : teamScoreBoards.get(team)){
					teamBoards.getScoreboard().getTeam("others").removePlayer(plugin.getServer().getOfflinePlayer(u));
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void playerIsVisible(UUID u){
		for(String team : teamM.getActiveTeams()){
			if(teamM.getTeamOfPlayer(u)!=team){
				for(Team teamBoards : teamScoreBoards.get(team)){
					teamBoards.getScoreboard().getTeam("others").addPlayer(plugin.getServer().getOfflinePlayer(u));
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void addPlayerToObserver(Player target){
		observerTeam.addPlayer(target);
		target.setScoreboard(mainBoard);

		for(String team : teamM.getActiveTeams()){
			for(Team teamBoards : teamScoreBoards.get(team)){
				teamBoards.getScoreboard().getTeam(ChatColor.AQUA + "Observers").addPlayer(target);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void removePlayerFromObservers(OfflinePlayer p){
		observerTeam.removePlayer(p);

		if(p.isOnline()){
			Player target = (Player) p;
			target.setPlayerListName(target.getName());
		}
	}

	public void createTeam(String team){
		teamScoreBoards.put(team, new ArrayList<Team>());
	}

	public void removeTeam(String team){
		for(Team scoreboardTeam : teamScoreBoards.get(team)){
			scoreboardTeam.unregister();
		}
		teamScoreBoards.remove(team);
	}

	private Scoreboard getScoreboardOfPlayer(UUID u){
		return individualScoreboards.get(u).getScoreboard();
	}

	@SuppressWarnings("deprecation")
	public void addPlayerToTeam(String team, Player target){
		initializeBoardForPlayer(target, team);
	}
	
	@SuppressWarnings("deprecation")
	public void removePlayerFromTeam(String team, OfflinePlayer p){
		for(Team teamBoards : teamScoreBoards.get(team)){
			teamBoards.removePlayer(p);
		}

		mainBoard.getTeam("others").removePlayer(p);
		for(UUID enemies : teamM.getPlayersInGame()){
			if(teamM.getTeamOfPlayer(enemies)!=team){
				getScoreboardOfPlayer(enemies).getTeam("others").removePlayer(p);
			}
		}
		if(p.isOnline()){
			Player target = (Player) p;
			target.setScoreboard(mainBoard);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerJoinEvent e){
		Player p = e.getPlayer();

		if(individualScoreboards.containsKey(p.getUniqueId())){
			p.setScoreboard(getScoreboardOfPlayer(p.getUniqueId()));
		}
		else{
			p.setScoreboard(mainBoard);
		}
	}

	private void gameStartInit(Objective header){

		Score timeTillPVP = header.getScore(ChatColor.AQUA + "Time Till PVP: ");
		updater(timeTillPVP, 20L, 0L, new Callable<Integer>() {
			public Integer call(){
				return timerM.getTimeTillPVP();
			}
		});

		Score spectators = header.getScore(ChatColor.AQUA + "Observers: ");
		spectators.setScore(teamM.getObservers().size());
		updater(spectators, 60L, new Callable<Integer>() {
			public Integer call(){
				return teamM.getObservers().size();
			}
		});
	}
	
	private void teamGameStartInit(UUID target, Objective header){
		Score kills = header.getScore(ChatColor.GREEN + "Your kills: ");
		updater(kills, 20L, new Callable<Integer>() {
			public Integer call(){
				return playerKills.get(target);
			}
		});
	}

	private void meetupStartInit(Objective header){

		Score timeTillMeetup = header.getScore(ChatColor.AQUA + "Time Till Meetup: ");
		updater(timeTillMeetup, 20L, 0L, new Callable<Integer>() {
			public Integer call(){
				return timerM.getTimeTillMeetup();
			}
		});
	}

	@EventHandler
	public void on(UHCKillEvent e){
		UUID target = e.getOfflinePlayer().getUniqueId();
		if(playerKills.containsKey(target)){
			playerKills.put(target, playerKills.get(target) + 1);
		}
	}
	
	public void customTimer(String title, Callable<Integer> func){
		Score observerScore = this.header.getScore(title);
		updater(observerScore, 20L, 0L, func);
		
		for(String team : teamM.getActiveTeams()){
			for(UUID u : teamM.getPlayersOnATeam(team)){
				Objective header = individualScoreBoardHeaders.get(u);
				Score playerScore = header.getScore(title);
				updater(playerScore, 20L, 0L, func);
			}
		}
	}
	
	//updaters

	private void updater(Score score, long frequency, Callable<Integer> func){

		new BukkitRunnable() {
			@Override
			public void run() {
				int val = 0;

				try {
					val = func.call();
				} catch (Exception e) {
					e.printStackTrace();
				}

				score.setScore(val);
			}
		}.runTaskTimer(plugin, 0L, frequency);
	}
	
	private void updater(Score score, long frequency, long delay, Callable<Integer> expiration){
		try {

			new BukkitRunnable() {
				int i =expiration.call();
				String toBlink = score.getEntry();

				@Override
				public void run() {

					if(i>60){
						double k = i;
						int toSet = (int) Math.ceil(k/60);
						score.setScore(toSet);
					}
					else if(i<=60 && i>10){
						score.setScore(i);
					}
					else if(i<=10 && i>0){
						score.getScoreboard().resetScores(toBlink);
						if(i%2==0){
							toBlink = score.getEntry();
						}
						else{
							toBlink = ChatColor.RED + ChatColor.stripColor(score.getEntry());
						}

						Score toChange = score.getObjective().getScore(toBlink);
						toChange.setScore(i);
					}
					else if(i==0){
						score.getScoreboard().resetScores(toBlink);
						cancel();
					}
					i--;
				}
			}.runTaskTimer(plugin, delay, frequency);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onUpdate(GameStartEvent e){
		gameStartInit(this.header);

		for(String team : teamM.getActiveTeams()){
			for(UUID u : teamM.getPlayersOnATeam(team)){
				Objective header = individualScoreBoardHeaders.get(u);
				gameStartInit(header);
				teamGameStartInit(u, header);
			}
		}
	}

	@EventHandler
	public void onUpdate(GameOpenEvent e){

		Score timeTillMatch = header.getScore(ChatColor.AQUA + "Match Starts In: ");
		updater(timeTillMatch, 20L, 0L, new Callable<Integer>() {
			public Integer call(){
				return timerM.getTimeTillMatchStart();
			}
		});
	}

	@EventHandler
	public void onUpdate(PVPEnableEvent e){
		meetupStartInit(this.header);

		for(String team : teamM.getActiveTeams()){
			for(UUID u : teamM.getPlayersOnATeam(team)){
				Objective header = individualScoreBoardHeaders.get(u);
				meetupStartInit(header);
			}
		}
	}
}
