package com.goldornetwork.uhc.managers.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.managers.world.events.GameOpenEvent;
import com.goldornetwork.uhc.managers.world.events.GameStartEvent;
import com.goldornetwork.uhc.managers.world.events.UHCDeathEvent;

public class BoardManager implements Listener{

	private UHC plugin;
	private TeamManager teamM;
	private Scoreboard mainBoard;
	private Team observerTeam;
	private WorldManager worldM;
	private TimerManager timerM;

	private List<Team> activeTeams = new ArrayList<Team>(); 
	private Map<String, Team> teamScoreBoards = new HashMap<String, Team>();
	private Map<String, Objective> teamHeaders = new HashMap<String, Objective>();
	private Map<UUID, String> teamOfPlayer = new HashMap<UUID, String>();

	private List<UUID> invisible = new ArrayList<UUID>();

	//scoreboard
	private Objective header;

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
		for(Player all : Bukkit.getServer().getOnlinePlayers()){
			all.setScoreboard(mainBoard);
			all.setPlayerListName(ChatColor.stripColor(all.getName()));
		}
		invisibleChecker();
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


		observerTeam = mainBoard.registerNewTeam("observers");
		observerTeam.setPrefix(ChatColor.AQUA + "[Observer] ");
	}



	private Scoreboard initializeTeamBoard(String team){
		Scoreboard teamBoard = Bukkit.getScoreboardManager().getNewScoreboard();
		String teamToAdd = teamM.getTeamNameProper(team);

		Team newTeam = teamBoard.registerNewTeam(team);
		newTeam.setCanSeeFriendlyInvisibles(true);
		newTeam.setAllowFriendlyFire(false);
		newTeam.setPrefix(ChatColor.GREEN + "");

		Team otherPlayers = teamBoard.registerNewTeam("others");
		otherPlayers.setPrefix(ChatColor.RED + "");

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

		teamHeaders.put(team, header);
		teamScoreBoards.put(team, newTeam);
		activeTeams.add(newTeam);
		return teamBoard;

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


	private void playerIsInvisible(UUID u){
		for(Team team : activeTeams){
			if(!(team.equals(teamScoreBoards.get(teamM.getTeamOfPlayer(u))))){
				team.getScoreboard().getTeam("others").removePlayer(Bukkit.getOfflinePlayer(u));
			}
		}

	}

	private void playerIsVisible(UUID u){
		for(Team team : activeTeams){
			if(!(team.equals(teamScoreBoards.get(teamM.getTeamOfPlayer(u))))){
				team.getScoreboard().getTeam("others").addPlayer(Bukkit.getOfflinePlayer(u));
			}
		}
	}

	public void addPlayerToObserver(OfflinePlayer p){
		observerTeam.addPlayer(p);
		if(p.isOnline()){
			Player target = (Player) p;
			target.setPlayerListName(ChatColor.AQUA + target.getName());
			target.setScoreboard(mainBoard);
		}
	}
	public void removePlayerFromObservers(OfflinePlayer p){
		observerTeam.removePlayer(p);
		if(p.isOnline()){
			Player target = (Player) p;
			target.setPlayerListName(target.getName());
		}
	}

	public void createTeam(String team){
		initializeTeamBoard(team);
	}

	public void removeTeam(String team){
		activeTeams.remove(teamScoreBoards.get(team));
		teamScoreBoards.get(team).unregister();
		teamScoreBoards.remove(team);
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
	public Scoreboard getScoreboardOfPlayer(UUID u){
		return teamScoreBoards.get(teamOfPlayer.get(u)).getScoreboard();
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
			p.setScoreboard(getScoreboardOfPlayer(p.getUniqueId()));
		}
		else{
			p.setScoreboard(mainBoard);
		}
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void on(UHCDeathEvent e){
		OfflinePlayer p = e.getOfflinePlayer();
		if(teamOfPlayer.containsKey(p.getUniqueId())){
			for(Team team : activeTeams){
				if(!(team.getPlayers().contains(Bukkit.getOfflinePlayer(p.getUniqueId())))){
					team.getScoreboard().getTeam("others").removePlayer(p);
				}

			}
			getScoreboardOfPlayer(p.getUniqueId()).getTeam(teamOfPlayer.get(p.getUniqueId())).removePlayer(p);

		}

	}

	private void gameStartInit(Objective header){
		Score timeTillPVP = header.getScore(ChatColor.AQUA + "Time Till Meetup: ");
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
			Objective header = teamHeaders.get(team);
			gameStartInit(header);

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



}
