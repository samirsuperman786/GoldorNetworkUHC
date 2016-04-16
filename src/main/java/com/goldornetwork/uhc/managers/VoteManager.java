package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.BlockRush;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.KingsManager;
import com.goldornetwork.uhc.utils.MessageSender;
import com.google.common.collect.ImmutableSet;

public class VoteManager {

	private GameModeManager gamemodeM;
	private TeamManager teamM;
	private UHC plugin;
	private Random random = new Random();
	private final int NUMBEROFOPTIONS = 3;
	private final int AMOUNTTOENABLE = 3;
	private List<List<Gamemode>> options= new LinkedList<List<Gamemode>>();
	private Map<Integer, Integer> mostPopularVote = new HashMap<Integer, Integer>();
	private List<UUID> haveVoted = new ArrayList<UUID>();
	private boolean voteActive;
	
	public VoteManager(UHC plugin, GameModeManager gamemodeM, TeamManager teamM) {
		this.plugin=plugin;
		this.gamemodeM = gamemodeM;
		this.teamM=teamM;
	}
	
	public void setup(){
		mostPopularVote.clear();
		haveVoted.clear();
		options.clear();
		voteActive=false;
	}
	public int getNumberOfOptions(){
		return NUMBEROFOPTIONS;
	}
	public boolean isValidOption(int option){
		if(option> 0 && option <=NUMBEROFOPTIONS){
			return true;
		}
		return false;
	}
	public void generateOptions(){
		voteActive=true;
		for(int k = 0; k<NUMBEROFOPTIONS; k++){
			List<Gamemode> toAdd = new ArrayList<Gamemode>();
			toAdd.clear();
			for(int i =0; i<AMOUNTTOENABLE; i++){
				boolean matched = true;
				while(matched){
					Gamemode game;
					int index;
					index = random.nextInt(gamemodeM.getNumberOfGamemodes()) ;
					game = gamemodeM.getGameMode(gamemodeM.getGamemodes().get(index).getClass());
					while(isValid(game)==false){
						index = random.nextInt(gamemodeM.getNumberOfGamemodes());
						game = gamemodeM.getGameMode(gamemodeM.getGamemodes().get(index).getClass());
					}
					if(toAdd.contains(game)){
						matched=true;
					}
					else{
						toAdd.add(game);
						matched = false;
						break;
					}
				}
			}
			options.add(toAdd);
			mostPopularVote.put(k, 0);
		}
	}
	private boolean isValid(Gamemode game){
		boolean valid = true;
		if(teamM.isFFAEnabled()){
			for(Class<?> gameClass : INCOMPATABLE_WITH_FFA){
				if(game.equals(gamemodeM.getGameMode(gameClass))){
					valid=false;
					break;
				}
			}
		}
		return valid;
	}
	private static final Set<Class<?>> INCOMPATABLE_WITH_FFA = ImmutableSet.of(
			KingsManager.class
			);
	
	public List<List<Gamemode>> getOptions(){
		return options;
	}
	
	public void enableOption(int choice){
		voteActive=false;
		for(Gamemode game : options.get(choice)){
			game.enable(plugin);
			MessageSender.broadcast(ChatColor.AQUA + game.getName() + " has been enabled");
		}
	}
	
	public boolean hasVoted(Player p){
		if(haveVoted.contains(p.getUniqueId())){
			return true;
		}
		else{
			return false;
		}
	}
	public void addVote(Player p, int choice){
		haveVoted.add(p.getUniqueId());
		mostPopularVote.replace(choice-1, (mostPopularVote.get(choice-1) + 1));
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
	
	public int getWinnerVotes(){
		return (Collections.max(mostPopularVote.values()));
	}
	
	public boolean isActive(){
		return voteActive;
	}
	
	
}
