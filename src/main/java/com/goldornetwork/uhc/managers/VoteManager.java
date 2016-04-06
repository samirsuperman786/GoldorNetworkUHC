package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;

public class VoteManager {

	private GameModeManager gamemodeM;
	private UHC plugin;
	private Random random = new Random();
	private final int NUMBEROFOPTIONS = 3;
	private final int AMOUNTTOENABLE = 3;
	private Map<Integer, List<Gamemode>> options = new HashMap<Integer, List<Gamemode>>();
	private Map<Integer, Integer> mostPopularVote = new HashMap<Integer, Integer>();
	private List<UUID> haveVoted = new ArrayList<UUID>();
	
	public VoteManager(UHC plugin, GameModeManager gamemodeM) {
		this.plugin=plugin;
		this.gamemodeM = gamemodeM;
	}
	
	public void setup(){
		options.clear();
		mostPopularVote.clear();
		haveVoted.clear();
	}
	public int getNumberOfOptions(){
		return NUMBEROFOPTIONS;
	}
	public boolean isValidOption(int option){
		if(option <NUMBEROFOPTIONS){
			return true;
		}
		return false;
	}
	public void generateOptions(){
		for(int k = 0; k<NUMBEROFOPTIONS; k++){
			List<Gamemode> toAdd = new ArrayList<Gamemode>();
			toAdd.clear();
			for(int i =0; i<AMOUNTTOENABLE; i++){
				boolean matched = true;
				while(matched){
					int index = random.nextInt(gamemodeM.getNumberOfGamemodes());
					Gamemode game = gamemodeM.getGameMode(gamemodeM.getGamemodes().get(index).getClass());
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
			options.put(k, toAdd);
			mostPopularVote.put(k, 0);
		}
	}
	
	public Map<Integer, List<Gamemode>> getOptions(){
		return options;
	}
	
	public void enableOption(int choice){
		for(Gamemode game : options.get(choice)){
			game.enable(plugin);
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
		mostPopularVote.put(choice, (mostPopularVote.get(choice) + 1));
	}
	
	public int getWinner(){
		int max = 0;
		int currentWinner = 0;
		for(int i = 0, temp =0; i< NUMBEROFOPTIONS; i++){
			currentWinner=i;
			temp= mostPopularVote.get(i);
			if(temp > max){
				max = temp;
				currentWinner=i;
			}
		}
		return currentWinner;
	}
}
