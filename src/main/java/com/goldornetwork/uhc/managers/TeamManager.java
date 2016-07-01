package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;
import com.goldornetwork.uhc.utils.MessageSender;


public class TeamManager implements Listener{


	private UHC plugin;
	private BoardManager boardM;
	private Random random = new Random();
	//storage
	private int playersPerTeam;
	private int MaxTeams;
	private boolean isTeamsEnabled;

	private Set<UUID> playersInGame = new HashSet<UUID>();
	private Set<UUID> observers = new HashSet<UUID>();
	private Set<String> listOfActiveTeams = new HashSet<String>();
	
	private Map<UUID, String> teamOfPlayer = new HashMap<UUID, String>();
	private Map<String, String> colorOfTeam = new HashMap<String, String>();
	private Map<String, Integer> playersOnCurrentTeam = new HashMap<String, Integer>();
	private Map<String, UUID> ownerOfTeam = new HashMap<String, UUID>();
	private Map<String, Set<UUID>> invitedPlayers = new HashMap<String, Set<UUID>>();
	

	public TeamManager(UHC plugin, BoardManager boardM) {
		this.plugin=plugin;
		this.boardM=boardM;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void setup(){
		config();
		this.playersPerTeam=0;
		isTeamsEnabled=false;
		playersInGame.clear();
		observers.clear();
		listOfActiveTeams.clear();
		teamOfPlayer.clear();
		colorOfTeam.clear();
		playersOnCurrentTeam.clear();
		ownerOfTeam.clear();
		invitedPlayers.clear();
	}

	private void config(){
		List<String> toADD = new ArrayList<String>();
		for(TEAMS team : TEAMS.values()){
			toADD.add(team.toString());
		}
		plugin.getConfig().addDefault("MAX-TEAMS", 24);
		plugin.getTeamConfig().addDefault("TEAM-NAMES", toADD);
		plugin.saveTeamConfig();
		plugin.saveConfig();
		this.MaxTeams=plugin.getConfig().getInt("MAX-TEAMS");
	}

	public enum TEAMS{
		ALPHA, BETA, GAMMA, DELTA, EPSILON, ZETA, ETA, THETA, IOTA, KAPPA, LAMBDA, MU, NU, XI, OMICRON, PI, RHO, SIGMA, TAU, UPSILON, PHI, CHI, PSI, OMEGA
	}

	public enum BASECOLORS{
		BLUE, DARK_AQUA, DARK_BLUE, DARK_PURPLE, LIGHT_PURPLE, YELLOW
	}

	private void initializeTeams(){
		List<BASECOLORS> colorsCombinations = Arrays.asList(BASECOLORS.values());
		int i = 0;
		for(String team : plugin.getTeamConfig().getStringList("TEAM-NAMES")){
			playersOnCurrentTeam.put(team.toString().toLowerCase(), 0);
			this.colorOfTeam.put(team.toString().toLowerCase(), ChatColor.valueOf(colorsCombinations.get(random.nextInt(colorsCombinations.size())).toString()).toString());
			i++;
			if(i>=MaxTeams){
				break;
			}
		}
	}

	public void setupTeams(int teamSize){
		isTeamsEnabled = true;
		this.playersPerTeam = teamSize;
		initializeTeams();
	}

	public int getTeamSize(){
		return this.playersPerTeam;
	}
	
	public void setTeamSize(int val){
		this.playersPerTeam=val;
	}

	public boolean isTeamsEnabled(){
		return isTeamsEnabled;
	}

	public boolean isValidTeam(String team){
		return listOfActiveTeams.contains(team.toLowerCase());
	}

	public boolean isPlayerInGame(UUID target){
		return playersInGame.contains(target);
	}

	public void addPlayerToOwner(String team, UUID target){
		ownerOfTeam.put(team, target);
	}

	public void removePlayerFromOwner(String team, UUID target){
		if(ownerOfTeam.get(team).equals(target)){
			ownerOfTeam.remove(team);
		}
	}

	public void removePlayerFromTeam(UUID target){
		OfflinePlayer offTarget = Bukkit.getOfflinePlayer(target);
		boardM.removePlayerFromTeam(getTeamOfPlayer(target), offTarget);
		decreaseTeamCount(getTeamOfPlayer(target), 1);
		playersInGame.remove(target);
		teamOfPlayer.remove(target);
	}

	public boolean isPlayerAnObserver(UUID u){
		return observers.contains(u);
	}

	public Set<UUID> getPlayersInGame(){
		return playersInGame;
	}

	public Set<UUID> getObservers(){
		return observers;
	}

	public String getTeamOfPlayer(UUID target){
		return teamOfPlayer.get(target).toLowerCase();
	}

	public UUID getOwnerOfTeam(String team){
		return ownerOfTeam.get(team);
	}

	public boolean createRandomTeam(Player target){
		boolean foundTeam = false;
		for(Map.Entry<String, Integer> entry : playersOnCurrentTeam.entrySet()){
			if(entry.getValue()==0){
				String team = entry.getKey();
				listOfActiveTeams.add(team);
				boardM.createTeam(team);
				addPlayerToTeam(target, team);
				ownerOfTeam.put(team, target.getUniqueId());
				foundTeam= true;
				break;
			}
		}

		return foundTeam;
	}

	public boolean isRoomToJoin(String team){
		return (playersPerTeam - playersOnCurrentTeam.get(team.toLowerCase())>0);
	}

	public void addPlayerToTeam(Player p, String team){

		for(UUID u : getPlayersOnATeam(team.toLowerCase())){
			if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
				MessageSender.alertMessage(Bukkit.getServer().getPlayer(u), ChatColor.GREEN + p.getName() + ChatColor.GOLD + " has joined your team.");
			}
		}

		
		playersInGame.add(p.getUniqueId());
		teamOfPlayer.put(p.getUniqueId(), team);
		increaseTeamCount(team, 1);
		boardM.addPlayerToTeam(team, p);
		if(invitedPlayers.containsKey(team)){
			if(invitedPlayers.get(team).contains(p.getUniqueId())){
				invitedPlayers.get(team).remove(p.getUniqueId());
			}
		}
	}

	public boolean isPlayerOnTeam(UUID target){
		return teamOfPlayer.containsKey(target);

	}

	public void invitePlayer(String team, UUID target){

		if(invitedPlayers.containsKey(team.toLowerCase())){
			invitedPlayers.get(team.toLowerCase()).add(target);
		}
		else{
			Set<UUID> toAdd = new HashSet<UUID>();
			toAdd.add(target);
			invitedPlayers.put(team.toLowerCase(), toAdd);
		}
	}

	public Set<UUID> getInvitedPlayers(String team){
		if(invitedPlayers.containsKey(team.toLowerCase())){
			return invitedPlayers.get(team.toLowerCase());
		}
		else{
			Set<UUID> toReturn= new HashSet<UUID>();
			return toReturn;
		}
	}

	public void unInvitePlayer(String team, UUID target){
		if(invitedPlayers.containsKey(team.toLowerCase())){
			if(invitedPlayers.get(team.toLowerCase()).contains(target)){
				invitedPlayers.get(team.toLowerCase()).remove(target);
			}
		}
	}

	public boolean isPlayerInvitedToTeam(OfflinePlayer p, String team){

		if(invitedPlayers.containsKey(team.toLowerCase())){
			if(invitedPlayers.get(team.toLowerCase()).contains(p.getUniqueId())){
				return true;
			}
		}
		return false;
	}
	

	public boolean isPlayerOwner(String team, UUID u){
		return ownerOfTeam.get(team).equals(u);
	}

	public void addPlayerToObservers(Player p){
		observers.add(p.getUniqueId());
		p.setGameMode(GameMode.SPECTATOR);

		for(PotionEffect effect : p.getActivePotionEffects()){
			p.removePotionEffect(effect.getType());
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
		MessageSender.send(p, ChatColor.AQUA + "You are now spectating.");
		boardM.addPlayerToObserver(p);
	}

	public void removePlayerFromObservers(Player p){
		observers.remove(p.getUniqueId());

		for(PotionEffect effect : p.getActivePotionEffects()){
			p.removePotionEffect(effect.getType());
		}
		boardM.removePlayerFromObservers(p);
	}

	public String getColorOfPlayer(UUID target){

		if(isPlayerAnObserver(target)){
			return ChatColor.AQUA.toString();
		}
		else if(isTeamsEnabled){
			return isPlayerOnTeam(target) ? getColorOfTeam(getTeamOfPlayer(target)) : ChatColor.WHITE.toString();
		}
		else{
			return ChatColor.WHITE.toString();
		}
	}

	public String getColorOfTeam(String team){
		return colorOfTeam.get(team.toLowerCase());
	}

	public List<UUID> getPlayersOnATeam(String team){
		List<UUID> players = new ArrayList<UUID>();
		if(teamOfPlayer.containsValue(team.toLowerCase())){
			for(Map.Entry<UUID, String>  entry : teamOfPlayer.entrySet()){
				if(entry.getValue().equalsIgnoreCase(team)){
					players.add(entry.getKey());
				}
			}
		}
		return players;
	}

	public Set<String> getActiveTeams(){
		return listOfActiveTeams;
	}

	public boolean isActiveTeam(String team){
		return listOfActiveTeams.contains(team.toLowerCase());
	}

	public void disbandTeam(String team){
		listOfActiveTeams.remove(team);

		if(invitedPlayers.containsKey(team)){
			for(UUID u: invitedPlayers.get(team)){
				if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
					
					MessageSender.alertMessage(Bukkit.getServer().getPlayer(u),ChatColor.RED + "Your invitation to team " + getColorOfTeam(team)
					+ getTeamNameProper(team) + ChatColor.RED + " has been revoked.");
				}
			}
			invitedPlayers.remove(team);
		}

		for(UUID u : getPlayersOnATeam(team)){
			removePlayerFromTeam(u);
		}

		boardM.removeTeam(team);
	}

	public boolean areTeamMates(UUID first, UUID second){
		return getPlayersOnATeam(getTeamOfPlayer(first)).contains(second);
	}

	public String getTeamNameProper(String team){
		String output = team.substring(0, 1).toUpperCase() + team.toLowerCase().substring(1);
		return output;
	}

	private void increaseTeamCount(String team, Integer numberToIncrease){
		playersOnCurrentTeam.replace(team.toLowerCase(), playersOnCurrentTeam.get(team.toLowerCase()) + numberToIncrease);
	}

	private void decreaseTeamCount(String team, Integer numberToDecrease){
		playersOnCurrentTeam.replace(team.toLowerCase(), playersOnCurrentTeam.get(team.toLowerCase()) - numberToDecrease);
	}

	@EventHandler
	public void on(GameStartEvent e){
		invitedPlayers.clear();
	}
}
