package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.Medic;
import com.goldornetwork.uhc.utils.MessageSender;



//TODO kick player from team



public class TeamManager {

	//instances
	private UHC plugin;
	//storage
	private int playersPerTeam;
	private int MaxFFASize;
	private boolean isFFAEnabled;
	private boolean isTeamsEnabled;

	private List<UUID> playersInGame = new ArrayList<UUID>();
	private List<UUID> observers = new ArrayList<UUID>();
	private List<String> listOfAvailableTeams = new ArrayList<String>();
	private List<UUID> freezedPlayers = new ArrayList<UUID>();

	private Map<UUID, String> teamOfPlayer = new HashMap<UUID, String>();
	private Map<String, String> colorOfTeam = new HashMap<String, String>();
	private Map<String, Integer> playersOnCurrentTeam = new HashMap<String, Integer>();
	private Map<String, UUID> ownerOfTeam = new HashMap<String, UUID>();
	private Map<UUID, UUID> invitedPlayers = new HashMap<UUID, UUID>();
	

	public TeamManager(UHC plugin) {
		this.plugin=plugin;
	}

	/**
	 * Does the following: removes all players from ingame, removes all observers, and basically starts a new blank TeamManager
	 */
	public void setup(){
		config();
		isFFAEnabled=false;
		isTeamsEnabled=false;
		playersInGame.clear();
		observers.clear();
		listOfAvailableTeams.clear();
		freezedPlayers.clear();
		teamOfPlayer.clear();
		colorOfTeam.clear();
		playersOnCurrentTeam.clear();
		ownerOfTeam.clear();
		invitedPlayers.clear();
	}
	
	private void config(){
		plugin.getConfig().addDefault("MAX-FFA-SIZE", 100);
		plugin.saveConfig();
		this.MaxFFASize=plugin.getConfig().getInt("MAX-FFA-SIZE");
	}


	/**
	 * Sets up new teams based on how many teams are given
	 * @param numberOfTeams - the number of teams to create
	 */
	private void initializeTeams(int numberOfTeams){


		for(int i =0; i<numberOfTeams; i++){
			String loopTeam=null;
			String colorOfTeam = null;


			switch(i){
			case 1: loopTeam = "Alpha";
			colorOfTeam = ChatColor.BLACK.toString();
			break;
			case 2: loopTeam = "Beta";
			colorOfTeam = ChatColor.BLUE.toString();
			break;
			case 3: loopTeam = "Gamma";
			colorOfTeam = ChatColor.YELLOW.toString();
			break;
			case 4: loopTeam = "Delta";
			colorOfTeam = ChatColor.DARK_AQUA.toString();
			break;
			case 5: loopTeam = "Epsilon";
			colorOfTeam = ChatColor.DARK_BLUE.toString();
			break;
			case 6: loopTeam = "Zeta";
			colorOfTeam = ChatColor.DARK_GRAY.toString();
			break;
			case 7: loopTeam = "Eta";
			colorOfTeam = ChatColor.DARK_GREEN.toString();
			break;
			case 8: loopTeam = "Theta";
			colorOfTeam = ChatColor.DARK_PURPLE.toString();
			break;
			case 9: loopTeam = "Iota";
			colorOfTeam = ChatColor.DARK_RED.toString();
			break;
			case 10: loopTeam = "Kappa";
			colorOfTeam = ChatColor.GOLD.toString();
			break;
			case 11: loopTeam = "Lambda";
			colorOfTeam = ChatColor.GRAY.toString();
			break;
			case 12: loopTeam = "Mu";
			colorOfTeam = ChatColor.GREEN.toString();
			break;
			case 13: loopTeam = "Nu";
			colorOfTeam = ChatColor.RED.toString();
			break;
			case 14: loopTeam = "Xi";
			colorOfTeam = ChatColor.LIGHT_PURPLE.toString();
			break;
			case 15: loopTeam = "Omicron";
			colorOfTeam = ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC;
			break;
			case 16: loopTeam = "Pi";
			colorOfTeam = ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC;
			break;
			case 17: loopTeam = "Rho";
			colorOfTeam = ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC;
			break;
			case 18: loopTeam = "Sigma";
			colorOfTeam = ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC;
			break;
			case 19: loopTeam = "Tau";
			colorOfTeam = ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC;
			break;
			case 20: loopTeam = "Upsilon";
			colorOfTeam = ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC;
			break;
			case 21: loopTeam = "Phi";
			colorOfTeam = ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC;
			break;
			case 22: loopTeam = "Chi";
			colorOfTeam = ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC;
			break;
			case 23: loopTeam = "Psi";
			colorOfTeam = ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC;
			break;
			case 24: loopTeam = "Omega";
			colorOfTeam = ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC;
			break;
			default: plugin.getLogger().info("Error at team initialization");

			}
			if(loopTeam == null){
				// nothing
			}
			else{
				playersOnCurrentTeam.put(loopTeam.toLowerCase(), 0);
				listOfAvailableTeams.add(loopTeam.toLowerCase());
				this.colorOfTeam.put(loopTeam.toLowerCase(), colorOfTeam);
				
			}

		}



	}

	/**
	 * Sets up FFA
	 */
	public void setupFFA(){
		isFFAEnabled = true;
	}

	/**
	 * Sets up teams with a give team size
	 * @param teamSize - the number of teams to create
	 */
	public void setupTeams(int teamSize){
		isTeamsEnabled = true;
		initializeTeams(24);
		this.playersPerTeam = teamSize;
	}

	/**
	 * Checks if FFA is enabled
	 * @return <code> True </code> if FFA is enabled
	 */
	public boolean isFFAEnabled(){
		return isFFAEnabled;
	}

	/**
	 * Checks if teams are enabled
	 * @return <code> True </code> if teams are enabled
	 */
	public boolean isTeamsEnabled(){
		return isTeamsEnabled;
	}


	/**
	 * Checks if there is room in the FFA to join
	 * @return <code> True </code> if there is room to join
	 */
	public boolean isFFARoomToJoin(){
		if((MaxFFASize-playersInGame.size())>0){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Checks if a given team exists
	 * @param team the team to check
	 * @return <code> True </code> if the given team exists
	 */
	public boolean isValidTeam(String team){
		if(listOfAvailableTeams.contains(team.toLowerCase())){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Adds a given player to the FFA
	 * @param p the player to add to FFA
	 */
	public void addPlayerToFFA(Player p){
		playersInGame.add(p.getUniqueId());
		displayName(p, "FFA");
	}
	/**
	 * Checks if a given player is in game
	 * @param p the player to check 
	 * @return <code> True </code> if the player is in game
	 */
	public boolean isPlayerInGame(Player p){
		if(playersInGame.contains(p.getUniqueId())){
			return true;
		}
		else{
			return false;
		}

	}

	/**
	 * Removes a player from the FFA
	 * @param p the player to remove
	 */
	public void removePlayerFromFFA(Player p){
		playersInGame.remove(p.getUniqueId());
		p.setDisplayName(p.getName());
	}

	/**
	 * Removes a player from the status of owner and cancels invitations
	 * @param p the player to remove from owner
	 */
	public void removePlayerFromOwner(Player p){
		if(invitedPlayers.containsValue(p.getUniqueId())){
			for(Map.Entry<UUID, UUID> entry: invitedPlayers.entrySet()){
				if(entry.getValue()==p.getUniqueId()){
					if(Bukkit.getServer().getPlayer(entry.getKey()).isOnline()){
						MessageSender.alertMessage(Bukkit.getServer().getPlayer(entry.getKey()),ChatColor.RED, "Your invitation to team " + getTeamOfPlayer(p) + " has been revoked!");
					}
					invitedPlayers.remove(entry.getKey());
				}
			}
		}
		if(State.getState().equals(State.OPEN)){
			for(UUID u : getPlayersOnATeam(getTeamOfPlayer(p))){
				if(Bukkit.getServer().getPlayer(u).isOnline()){
					MessageSender.alertMessage(Bukkit.getPlayer(u), ChatColor.RED, "Your team has been disbanded");
					removePlayerFromTeam(Bukkit.getServer().getPlayer(u));
				}
			}
		}




		ownerOfTeam.remove(p.getUniqueId());
	}

	/**
	 * Removes a given player from a any team that player is on
	 * @param p the player to remove from a team
	 */
	public void removePlayerFromTeam(Player p){
		decreaseTeamSize(getTeamOfPlayer(p), 1);
		playersInGame.remove(p.getUniqueId());
		teamOfPlayer.remove(p.getUniqueId());
	}




	/**
	 * Checks if a given player is an observer
	 * @param p the player to check
	 * @return <code> True </code> if the give player is observing
	 */
	public boolean isPlayerAnObserver(Player p){
		if(observers.contains(p.getUniqueId())){
			return true;
		}
		else{
			return false;
		}

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
	public String getTeamOfPlayer(Player p){
		return teamOfPlayer.get(p.getUniqueId()).toLowerCase();
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
		if(Bukkit.getServer().getPlayer(target).isOnline()){
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
				addPlayerToTeam(p, entry.getKey());
				ownerOfTeam.put(entry.getKey(), p.getUniqueId());
				foundTeam= true;
				break;
			}

		}
		if(foundTeam==true){
			return true;
		}
		else{
			return false;
		}

	}

	/**
	 * Checks if there is room to join a specified team
	 * @param team - the team to check for
	 * @return <code> True </code> if there is room to join
	 */
	public boolean isTeamRoomToJoin(String team){
		if((playersPerTeam - playersOnCurrentTeam.get(team.toLowerCase()))>0){
			return true;
		}
		else{
			return false;
		}


	}


	/**
	 * Used to add a specified player to a given team
	 * @param p - the player who is joining a team
	 * @param team - the team to add the player to
	 */
	public void addPlayerToTeam(Player p, String team){
		for(UUID u : getPlayersOnATeam(team.toLowerCase())){
			if(Bukkit.getServer().getPlayer(u).isOnline()){
				MessageSender.alertMessage(Bukkit.getServer().getPlayer(u), ChatColor.GREEN, p.getName() + " has joined your team.");
			}
		}
		playersInGame.add(p.getUniqueId());
		teamOfPlayer.put(p.getUniqueId(), team);
		increaseTeamSize(team, 1);
		displayName(p, team);
	}


	/**
	 * Used to permit an arbitrary player to join the team of another player
	 * @param inviter - the person who is inviting
	 * @param target - the person to invite
	 */
	public void invitePlayer(Player inviter, Player target){
		invitedPlayers.put(target.getUniqueId(), inviter.getUniqueId());
	}

	/**
	 * Used to disallow a player from joining a team
	 * @param inviter - the person who is un-inviting
	 * @param target - the person to un-invite
	 */
	public void unInvitePlayer(Player inviter, Player target){
		invitedPlayers.remove(target);
	}

	
	/**
	 * Used to check if a player is invited to a team
	 * @param p - the player to check for
	 * @param team - the team to check for
	 * @return <code> True </code> if the player is invited to the given team
	 */
	public boolean isPlayerInvitedToTeam(Player p, String team){
		if(invitedPlayers.containsKey(p.getUniqueId())&& teamOfPlayer.get(invitedPlayers.get(p.getUniqueId())).equalsIgnoreCase(team)){
			return true;

		}
		else{
			return false;
		}

	}



	/**
	 * Used to check if a given player is the owner of a team
	 * @param p - the player to check for
	 * @return <code> True </code> if the player is the owner
	 */
	public boolean isPlayerOwner(Player p){
		if(ownerOfTeam.containsValue(p.getUniqueId())){
			return true;
		}
		else{
			return false;
		}
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
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
		p.setDisplayName(ChatColor.AQUA + "[Observer] " + p.getName()+ ChatColor.WHITE);
	}
	public String getColorOfPlayer(Player p){
		if(isFFAEnabled){
			return ChatColor.YELLOW.toString();
		}
		else if(isTeamsEnabled){
			return colorOfTeam.get(getTeamOfPlayer(p).toLowerCase());
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
	public List<String> getListOfTeams(){
		return listOfAvailableTeams;
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
		p.setDisplayName(getColorOfPlayer(p) + "["  + team + "] " + p.getName() + ChatColor.WHITE);
	}


}
