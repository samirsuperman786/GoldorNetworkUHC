package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;



//TODO kick player from team



public class TeamManager {

	//instances
	private UHC plugin;
	private BoardManager boardM;
	private Random random = new Random();
	//storage
	private int playersPerTeam;
	private int MaxTeams;
	private boolean isTeamsEnabled;

	private List<UUID> playersInGame = new ArrayList<UUID>();
	private List<UUID> observers = new ArrayList<UUID>();
	private List<String> listOfActiveTeams = new ArrayList<String>();

	private Map<UUID, String> teamOfPlayer = new HashMap<UUID, String>();
	private Map<String, String> colorOfTeam = new HashMap<String, String>();
	private Map<String, Integer> playersOnCurrentTeam = new HashMap<String, Integer>();
	private Map<String, UUID> ownerOfTeam = new HashMap<String, UUID>();
	private Map<String, List<UUID>> invitedPlayers = new HashMap<String, List<UUID>>();

	public TeamManager(UHC plugin, BoardManager boardM) {
		this.plugin=plugin;
		this.boardM=boardM;
	}

	/**
	 * Does the following: removes all players from ingame, removes all observers, and basically starts a new blank TeamManager
	 */
	public void setup(){
		config();
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
		BLUE, DARK_AQUA, DARK_BLUE, DARK_GRAY, DARK_PURPLE, DARK_RED, LIGHT_PURPLE, YELLOW
	}

	/**
	 * Sets up new teams based on how many teams are given
	 * @param numberOfTeams - the number of teams to create
	 */
	private void initializeT(){
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




	/**
	 * Sets up teams with a give team size
	 * @param teamSize - the number of teams to create
	 */
	public void setupTeams(int teamSize){
		isTeamsEnabled = true;
		this.playersPerTeam = teamSize;
		initializeT();

	}

	public int getTeamSize(){
		return this.playersPerTeam;
	}

	/**
	 * Checks if teams are enabled
	 * @return <code> True </code> if teams are enabled
	 */
	public boolean isTeamsEnabled(){
		return isTeamsEnabled;
	}

	/**
	 * Checks if a given team exists
	 * @param team the team to check
	 * @return <code> True </code> if the given team exists
	 */
	public boolean isValidTeam(String team){
		if(listOfActiveTeams.contains(team.toLowerCase())){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Checks if a given player is in game
	 * @param p the player to check 
	 * @return <code> True </code> if the player is in game
	 */
	public boolean isPlayerInGame(UUID target){
		return playersInGame.contains(target);

	}


	/**
	 * Removes a player from the status of owner
	 * @param p the player to remove from owner
	 */
	public void removePlayerFromOwner(Player p){
		ownerOfTeam.remove(p.getUniqueId());
	}

	/**
	 * Removes a given player from a any team that player is on
	 * @param p the player to remove from a team
	 */
	public void removePlayerFromTeam(UUID target){
		OfflinePlayer offTarget = Bukkit.getOfflinePlayer(target);
		boardM.removePlayerFromTeam(getTeamOfPlayer(target), offTarget);
		decreaseTeamSize(getTeamOfPlayer(target), 1);
		playersInGame.remove(target);
		teamOfPlayer.remove(target);
		
		if(offTarget.isOnline()){
			Player p = (Player) offTarget;
			p.setDisplayName(p.getName());
		}
	}




	/**
	 * Checks if a given player is an observer
	 * @param p the player to check
	 * @return <code> True </code> if the give player is observing
	 */
	public boolean isPlayerAnObserver(UUID u){
		return observers.contains(u);
		

	}


	/**
	 * Gets a list of players in game
	 * @return <code> List[UUID] </code> of players in game
	 */
	public List<UUID> getPlayersInGame(){
		return playersInGame;
	}

	/**
	 * Gets a list of observers
	 * @return <code> List[UUID] </code> of observers
	 */
	public List<UUID> getObservers(){
		return observers;
	}

	/**
	 * Retrieves the team of a given player
	 * @param p - the player to get the team of
	 * @return <code> String </code> of the given players team
	 */
	public String getTeamOfPlayer(UUID target){
		return teamOfPlayer.get(target).toLowerCase();
	}


	/**
	 * Retrieves the owner of a team
	 * @param team - the team to get the owner from
	 * @return <code> UUID </code> of the owner
	 */
	public UUID getOwnerOfTeam(String team){
		return ownerOfTeam.get(team);
	}

	/**
	 * Checks if a player is online with a given string
	 * @param target - the name of a player to check for
	 * @return <code> True </code> if the player is online
	 */
	public boolean isPlayerOnline(String target){
		if(Bukkit.getServer().getOfflinePlayer(target).isOnline()){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Used to create a new team with a given player
	 * @param p the player who creates the team
	 * @return <code> True </code> if the team could be created
	 */
	public boolean createRandomTeam(Player p){
		boolean foundTeam = false;
		for(Map.Entry<String, Integer> entry : playersOnCurrentTeam.entrySet()){
			if(entry.getValue()==0){
				listOfActiveTeams.add(entry.getKey());
				boardM.createTeam(entry.getKey());
				addPlayerToTeam(p, entry.getKey());
				ownerOfTeam.put(entry.getKey(), p.getUniqueId());
				foundTeam= true;
				break;
			}

		}
		return foundTeam;

	}

	/**
	 * Checks if there is room to join a specified team
	 * @param team - the team to check for
	 * @return <code> True </code> if there is room to join
	 */
	public boolean isTeamRoomToJoin(String team){
		return (playersPerTeam - playersOnCurrentTeam.get(team.toLowerCase())>0);

	}


	/**
	 * Used to add a specified player to a given team
	 * @param p - the player who is joining a team
	 * @param team - the team to add the player to
	 */
	public void addPlayerToTeam(Player p, String team){
		for(UUID u : getPlayersOnATeam(team.toLowerCase())){
			if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
				MessageSender.alertMessage(Bukkit.getServer().getPlayer(u), ChatColor.GREEN, p.getName() + " has joined your team.");
			}
		}
		//
		boardM.addPlayerToTeam(team, p);
		playersInGame.add(p.getUniqueId());
		teamOfPlayer.put(p.getUniqueId(), team);
		increaseTeamSize(team, 1);
		displayName(p, team);
	}

	public boolean isPlayerOnTeam(UUID target){
		return teamOfPlayer.containsKey(target);
		
	}

	/**
	 * Used to permit an arbitrary player to join the team of another player
	 * @param inviter - the person who is inviting
	 * @param target - the person to invite
	 */
	public void invitePlayer(String team, UUID target){
		if(invitedPlayers.containsKey(team)){
			invitedPlayers.get(team).add(target);
		}
		else{
			List<UUID> toAdd = new ArrayList<UUID>();
			toAdd.add(target);
			invitedPlayers.put(team, toAdd);
		}
	}

	/**
	 * Used to disallow a player from joining a team
	 * @param inviter - the person who is un-inviting
	 * @param target - the person to un-invite
	 */
	public void unInvitePlayer(String team, UUID target){
		if(invitedPlayers.containsKey(team)){
			if(invitedPlayers.get(team).contains(target)){
				invitedPlayers.get(team).remove(target);
			}
		}

	}


	/**
	 * Used to check if a player is invited to a team
	 * @param p - the player to check for
	 * @param team - the team to check for
	 * @return <code> True </code> if the player is invited to the given team
	 */
	public boolean isPlayerInvitedToTeam(Player p, String team){
		if(invitedPlayers.containsKey(team)){
			if(invitedPlayers.get(team).contains(p.getUniqueId())){
				return true;
			}
		}
		return false;

	}



	/**
	 * Used to check if a given player is the owner of a team
	 * @param p - the player to check for
	 * @return <code> True </code> if the player is the owner
	 */
	public boolean isPlayerOwner(Player p){
		return ownerOfTeam.containsValue(p.getUniqueId());
	}

	/**
	 * Used to add a given player to observers
	 * @param p - the player to add to observers
	 */
	public void addPlayerToObservers(Player p){
		observers.add(p.getUniqueId());
		p.setGameMode(GameMode.SPECTATOR);
		for(PotionEffect effect : p.getActivePotionEffects()){
			p.removePotionEffect(effect.getType());
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
		p.setDisplayName(ChatColor.AQUA + "[Observer] " + p.getName()+ ChatColor.WHITE);
		MessageSender.send(ChatColor.AQUA, p, "You are now spectating.");
		boardM.addPlayerToObserver(p);
	}
	public String getColorOfPlayer(UUID target){
		if(isPlayerAnObserver(target)){
			return ChatColor.AQUA.toString();
		}
		else if(isTeamsEnabled){
			return isPlayerOnTeam(target) ? getColorOfTeam(getTeamOfPlayer(target)) : ChatColor.WHITE.toString();
		}
		else{
			return ChatColor.GRAY.toString();
		}

	}
	/** Used to get the color of a team
	 * @param team - the team to get the color of
	 * @return <code> ChatColor </code> of the team
	 */
	public String getColorOfTeam(String team){
		return colorOfTeam.get(team.toLowerCase());
	}

	/**
	 * Used to retrieve the players on a specified team
	 * @param team - the team to get the list of players from
	 * @return <code> List[UUID] </code> of players on a specified team
	 */
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
	/**
	 * Used to retrieve the list of teams in game
	 * @return <code> List[String] </code> of teams in game
	 */

	public List<String> getActiveTeams(){
		return listOfActiveTeams;
	}
	public boolean isActiveTeam(String team){
		return listOfActiveTeams.contains(team.toLowerCase());
	}

	public boolean isTeamInactive(String team){
		boolean allOffline=true;
		while(allOffline){
			for(UUID u : getPlayersOnATeam(team)){
				if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
					allOffline=false;
					break;
				}
			}
			if(allOffline==false){
				break;
			}
		}
		return allOffline;
	}
	public void disbandTeam(String team){
		listOfActiveTeams.remove(team);
		if(invitedPlayers.containsKey(team)){
			for(UUID u: invitedPlayers.get(team)){
				if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
					MessageSender.alertMessage(Bukkit.getServer().getPlayer(u),ChatColor.RED, "Your invitation to team " + getColorOfTeam(team) + getTeamNameProper(team) + " has been revoked!");
				}
			}
			invitedPlayers.remove(team);
		}

		for(UUID u : getPlayersOnATeam(team)){
			if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
				MessageSender.alertMessage(Bukkit.getServer().getPlayer(u), ChatColor.RED, "Your team has been disbanded.");
			}
			removePlayerFromTeam(u);
		}
		boardM.removeTeam(team);
	}


	public String getTeamNameProper(String team){
		String output = team.substring(0, 1).toUpperCase() + team.toLowerCase().substring(1);
		return output;
	}

	/**
	 * used to increase the number of players currently on a team
	 * @param team - the team to increase
	 * @param numberToIncrease - the increment to increase
	 */
	private void increaseTeamSize(String team, Integer numberToIncrease){
		playersOnCurrentTeam.replace(team.toLowerCase(), playersOnCurrentTeam.get(team.toLowerCase()) + numberToIncrease);
	}

	/**
	 * used to decrease the number of players currently on a team
	 * @param team - the team to decrease
	 * @param numberToDecrease - the increment to decrease by
	 */
	private void decreaseTeamSize(String team, Integer numberToDecrease){
		playersOnCurrentTeam.replace(team.toLowerCase(), playersOnCurrentTeam.get(team.toLowerCase()) - numberToDecrease);
	}


	/**
	 * Used to edit the display name of a player
	 * @param p - the player to change the display name of
	 * @param team - the text to display in front of a players name
	 */
	public void displayName(Player p, String team){
		p.setDisplayName(getColorOfPlayer(p.getUniqueId()) + "["  + getTeamNameProper(team) + "] " + ChatColor.RED + p.getName() + ChatColor.WHITE);
	}


}
