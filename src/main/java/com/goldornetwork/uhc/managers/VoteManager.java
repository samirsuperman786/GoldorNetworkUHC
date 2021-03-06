package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.utils.MessageSender;

public class VoteManager implements Listener{

	
	private GameModeManager gamemodeM;
	private UHC plugin;
	private Random random = new Random();
	private final int NUMBEROFOPTIONS = 3;
	private final int AMOUNTTOENABLE = 3;
	private List<List<Gamemode>> options= new LinkedList<List<Gamemode>>();
	private Map<Integer, Integer> mostPopularVote = new HashMap<Integer, Integer>();
	private Map<UUID, Integer> playerVotes = new HashMap<UUID, Integer>();
	private boolean voteActive;

	
	public VoteManager(UHC plugin, GameModeManager gamemodeM) {
		this.plugin=plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.gamemodeM = gamemodeM;
	}

	public void setup(){
		mostPopularVote.clear();
		playerVotes.clear();
		options.clear();
		voteActive=false;
	}

	public int getNumberOfOptions(){
		return NUMBEROFOPTIONS;
	}

	public boolean isValidOption(int option){
		return (option> 0 && option <=NUMBEROFOPTIONS);
	}

	public void broadcastOptions(){

		new BukkitRunnable() {
			@Override
			public void run() {
				MessageSender.broadcast(getMessage());				
			}
		}.runTaskLater(plugin, 80L);
	}

	private List<String> getMessage(){
		List<String> toBroadcast = new LinkedList<String>();
		toBroadcast.add("[Options] Please use /vote [option], also /info [scenario]");

		for(int i = 0; i<getNumberOfOptions(); i++){
			int comma = 0;
			StringBuilder str = new StringBuilder();

			for(Gamemode game : getOptions().get(i)){
				comma++;
				String message = ChatColor.AQUA + game.getProperName();
				String properMessage;
				if(comma<getOptions().get(i).size()){
					properMessage = message + ChatColor.GRAY + " + ";
				}
				else{
					properMessage=message;
				}

				str.append(properMessage);
			}
			String msg = str.toString();
			toBroadcast.add("Option " + (i + 1) + ": " + msg + ChatColor.GRAY + " (" + getVotes(i+1) + ")");
		}

		return toBroadcast;
	}

	public void generateOptions(){
		voteActive=true;

		for(int k = 1; k<(NUMBEROFOPTIONS + 1); k++){
			List<Gamemode> toAdd = new ArrayList<Gamemode>();
			toAdd.clear();

			for(int i =0; i<AMOUNTTOENABLE; i++){
				boolean matched = false;

				while(matched==false){
					Gamemode game;
					int index;
					index = random.nextInt(gamemodeM.getNumberOfGamemodes()) ;
					game = gamemodeM.getGameMode(gamemodeM.getGamemodes().get(index).getClass());

					while(isValid(toAdd, game)==false){
						index = random.nextInt(gamemodeM.getNumberOfGamemodes());
						game = gamemodeM.getGameMode(gamemodeM.getGamemodes().get(index).getClass());
					}
					if(toAdd.contains(game)){
						matched=false;
					}
					else{
						toAdd.add(game);
						matched = true;
						break;
					}
				}
			}
			options.add(toAdd);
			mostPopularVote.put(k, 0);
		}
	}

	private boolean isValid(List<Gamemode> gamemodes, Gamemode game){
		//TODO validate
		boolean valid = true;
		return valid;
	}

	public List<List<Gamemode>> getOptions(){
		return options;
	}

	public void enableOption(int choice){
		voteActive=false;
		List<String> toBroadcast = new LinkedList<String>();

		for(Gamemode game : options.get((choice-1))){
			game.enable(plugin);
			toBroadcast.add(ChatColor.AQUA + game.getProperName() + " has been enabled.");
		}
		
		MessageSender.broadcastTitle(ChatColor.AQUA + "Scenarios have been enabled.", ChatColor.GOLD + "Use" + ChatColor.RED + " /info " + ChatColor.GOLD + "to learn them!");
		MessageSender.broadcast(toBroadcast);
	}

	public boolean hasVoted(UUID target){
		return playerVotes.containsKey(target);
	}
	
	public void changeVote(UUID target, int newChoice){
		int oldChoice = playerVotes.remove(target);
		subtractVote(target, oldChoice);
		
		playerVotes.put(target, newChoice);
		addVote(target, newChoice);
	}
	
	public void addVote(UUID target, int choice){
		playerVotes.put(target, choice);
		mostPopularVote.replace(choice, (mostPopularVote.get(choice) + 1));
	}
	
	private void subtractVote(UUID target, int choice){
		playerVotes.remove(target);
		mostPopularVote.replace(choice, (mostPopularVote.get(choice) - 1));
	}

	public int getWinner(){
		int maxVal = (Collections.max(mostPopularVote.values()));
		int currentWinner=0;
		for(Map.Entry<Integer, Integer> entry : mostPopularVote.entrySet()){
			if(entry.getValue()==maxVal){
				currentWinner=entry.getKey();
			}
		}

		return currentWinner;
	}
	
	public int getVotes(int choice){
		return mostPopularVote.get(choice);
	}

	public boolean isActive(){
		return voteActive;
	}

	@EventHandler
	public void on(PlayerJoinEvent e){
		Player target =e.getPlayer();
		if(isActive()){

			new BukkitRunnable() {
				@Override
				public void run() {
					if(target.isOnline()){
						MessageSender.send(target, getMessage());
					}					
				}
			}.runTaskLater(plugin, 10L);
		}
	}
}
