package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.management.timer.TimerNotification;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;



//TODO kick player from team



public class TeamManager {
	
	//instances

	//storage
	private int playersPerTeam;
	private int FFATeamSize;
	private boolean isFFAEnabled;
	private boolean isTeamsEnabled;
	
	private List<UUID> playersInGame = new ArrayList<UUID>();
	private List<UUID> observers = new ArrayList<UUID>();
	private List<String> listOfAvailableTeams = new ArrayList<String>();
	private List<UUID> freezedPlayers = new ArrayList<UUID>();
	
	private Map<UUID, String> teamOfPlayer = new HashMap<UUID, String>();
	private Map<String, ChatColor> colorOfTeam = new HashMap<String, ChatColor>();
	private Map<String, Integer> playersOnCurrentTeam = new HashMap<String, Integer>();
	private Map<String, UUID> ownerOfTeam = new HashMap<String, UUID>();
	private Map<UUID, UUID> invitedPlayers = new HashMap<UUID, UUID>();
	

	public TeamManager() {
		
	}
	
	public void setup(){
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
	
	
	
	public void initializeTeams(int numberOfTeams){


		for(int i =0; i<numberOfTeams; i++){
			String loopTeam=null;
			ChatColor colorOfTeam = null;
			
			
			switch(i){
			case 1: loopTeam = "Alfa";
					colorOfTeam = ChatColor.BLACK;
			break;
			case 2: loopTeam = "Bravo";
					colorOfTeam = ChatColor.BLUE;
			break;
			case 3: loopTeam = "Charlie";
					colorOfTeam = ChatColor.YELLOW;
			break;
			case 4: loopTeam = "Delta";
					colorOfTeam = ChatColor.DARK_AQUA;
			break;
			case 5: loopTeam = "Echo";
					colorOfTeam = ChatColor.DARK_BLUE;
			break;
			case 6: loopTeam = "Foxtrot";
					colorOfTeam = ChatColor.DARK_GRAY;
			break;
			case 7: loopTeam = "Golf";
					colorOfTeam = ChatColor.DARK_GREEN;
			break;
			case 8: loopTeam = "Hotel";
					colorOfTeam = ChatColor.DARK_PURPLE;
			break;
			case 9: loopTeam = "India";
					colorOfTeam = ChatColor.DARK_RED;
			break;
			case 10: loopTeam = "Juliett";
					colorOfTeam = ChatColor.GOLD;
			break;
			case 11: loopTeam = "Kilo";
					colorOfTeam = ChatColor.GRAY;
			break;
			case 12: loopTeam = "Lima";
					colorOfTeam = ChatColor.GREEN;
			break;
			case 13: loopTeam = "Mike";
					colorOfTeam = ChatColor.RED;
			break;
			case 14: loopTeam = "November";
					colorOfTeam = ChatColor.LIGHT_PURPLE;
			break;
			//TODO modifying existing colors with underline, strike through, and bold

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
	
	public void setupFFA(){
		isFFAEnabled = true;
	}
	
	public void setupTeams(int numberOfTeams, int teamSize){
		isTeamsEnabled = true;
		initializeTeams(numberOfTeams);
		this.playersPerTeam = teamSize;
	}
	
	public boolean isFFAEnabled(){
		return isFFAEnabled;
	}
	
	public boolean isTeamsEnabled(){
		return isTeamsEnabled;
	}
	
	public boolean isFFARoomToJoin(){
		if((FFATeamSize-playersInGame.size())>0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean isValidTeam(String team){
		if(listOfAvailableTeams.contains(team.toLowerCase())){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void addPlayerToFFA(Player p){
		playersInGame.add(p.getUniqueId());
		displayName(p, "FFA");
	}
	public boolean isPlayerInGame(Player p){
		if(playersInGame.contains(p.getUniqueId())){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public void removePlayerFromFFA(Player p){
		playersInGame.remove(p.getUniqueId());
		p.setDisplayName(p.getName());
	}
	
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
	
	public void removePlayerFromTeam(Player p){
		decreaseTeamSize(getTeamOfPlayer(p), 1);
		playersInGame.remove(p.getUniqueId());
		teamOfPlayer.remove(p.getUniqueId());
	}
	
	
	
	
	public boolean isPlayerAnObserver(Player p){
		if(observers.contains(p.getUniqueId())){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	
	public List<UUID> getPlayersInGame(){
		return playersInGame;
	}
	
	public List<UUID> getObservers(){
		return observers;
	}
	
	public String getTeamOfPlayer(Player p){
		return teamOfPlayer.get(p.getUniqueId()).toLowerCase();
	}
	
	public UUID getOwnerOfTeam(String team){
		return ownerOfTeam.get(team);
	}
	
	public boolean isPlayerOnline(String target){
		if(Bukkit.getServer().getPlayer(target).isOnline()){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean createRandomTeam(Player p){
		boolean foundTeam = false;
		for(Map.Entry<String, Integer> entry :playersOnCurrentTeam.entrySet()){
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
	
	public boolean isTeamRoomToJoin(String team){
		if((playersPerTeam - playersOnCurrentTeam.get(team.toLowerCase()))>0){
			return true;
		}
		else{
			return false;
		}
		
		
	}
	
	
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
	
	
	public void invitePlayer(Player inviter, Player target){
		invitedPlayers.put(target.getUniqueId(), inviter.getUniqueId());
	}
	
	public void unInvitePlayer(Player inviter, Player target){
		invitedPlayers.remove(target);
	}
	
	public boolean isPlayerInvitedToTeam(Player p, String team){
		
		if(invitedPlayers.containsKey(p.getUniqueId())&& teamOfPlayer.get(invitedPlayers.get(p.getUniqueId())).equalsIgnoreCase(team)){
			return true;
			
		}
		else{
			return false;
		}
		
	}
	
	
	
	public boolean isPlayerOwner(Player p){
		if(ownerOfTeam.containsValue(p.getUniqueId())){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void addPlayerToObservers(Player p){
		observers.add(p.getUniqueId());
		p.setGameMode(GameMode.SPECTATOR);
		p.setDisplayName(ChatColor.AQUA + "[Observer] " + p.getName()+ ChatColor.WHITE);
	}
	public ChatColor getColorOfPlayer(Player p){
		if(isFFAEnabled){
			return ChatColor.YELLOW;
		}
		else if(isTeamsEnabled){
			return colorOfTeam.get(getTeamOfPlayer(p).toLowerCase());
		}
		else{
			return ChatColor.GRAY;
		}
	
	}
	public ChatColor getColorOfTeam(String team){
		return colorOfTeam.get(team.toLowerCase());
	}
	
	public List<UUID> getPlayersOnATeam(String team){
		List<UUID> players = null;
		if(teamOfPlayer.containsValue(team.toLowerCase())){
			for(Map.Entry<UUID, String>  entry : teamOfPlayer.entrySet()){
				if(entry.getValue().equalsIgnoreCase(team)){
					players.add(entry.getKey());
				}
			}
		}
		return players;
	}
	public List<String> getListOfTeams(){
		return listOfAvailableTeams;
	}
	private void increaseTeamSize(String team, Integer numberToIncrease){
		playersOnCurrentTeam.replace(team.toLowerCase(), playersOnCurrentTeam.get(team.toLowerCase()) + numberToIncrease);
	}
	
	private void decreaseTeamSize(String team, Integer numberToDecrease){
		playersOnCurrentTeam.replace(team.toLowerCase(), playersOnCurrentTeam.get(team.toLowerCase()) - numberToDecrease);
	}
	
	
	
	private void displayName(Player p, String team){
		p.setDisplayName(getColorOfPlayer(p) + "["  + team + "] " + p.getName() + ChatColor.WHITE);
	}
	
	
}
