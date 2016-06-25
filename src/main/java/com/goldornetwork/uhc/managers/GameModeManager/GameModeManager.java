package com.goldornetwork.uhc.managers.GameModeManager;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.BedBombs;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.BlockRush;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.Bows;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.CutClean;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.FlowerPower;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.KillSwitch;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.KingsManager;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.LandIsBad;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.PotionSwap;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.RewardingLongshots;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.RunBabyRun;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.SkyHigh;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.Switcheroo;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.TheHobbitManager;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.Weaklings;
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.google.common.collect.ImmutableList;

public class GameModeManager {

	
	Random random = new Random();

	private Set<Gamemode> gamemodes = new HashSet<Gamemode>();
	private UHC plugin;

	
	public GameModeManager(UHC plugin) {
		this.plugin=plugin;
	}

	public void setupGamemodes(TeamManager teamM, WorldManager worldM){

		for(Gamemode game : getEnabledGamemodes()){
			game.disable();
		}
		gamemodes.add(new CutClean(teamM));
		gamemodes.add(new BlockRush(worldM));
		gamemodes.add(new FlowerPower(teamM));
		gamemodes.add(new KillSwitch());
		gamemodes.add(new KingsManager(teamM));
		gamemodes.add(new LandIsBad(plugin, teamM));
		gamemodes.add(new PotionSwap(plugin, teamM));
		gamemodes.add(new RewardingLongshots(teamM));
		gamemodes.add(new SkyHigh(plugin, teamM));
		gamemodes.add(new Switcheroo(teamM));
		gamemodes.add(new TheHobbitManager(teamM));
		gamemodes.add(new BedBombs(teamM));
		gamemodes.add(new Bows(teamM));
		gamemodes.add(new RunBabyRun(plugin, teamM));
		gamemodes.add(new Weaklings(plugin, teamM));
	}

	@SuppressWarnings("unchecked")
	public<T> T getGameMode(Class<T> gamemodeClass){
		for(Gamemode game : gamemodes){
			if(game.getClass().equals(gamemodeClass)){
				return (T) game;
			}
		}

		return null;
	}

	public Gamemode getGamemode(String name){
		for(Gamemode game : gamemodes){
			if(game.getName().equalsIgnoreCase(name)){
				return game;
			}
		}

		return null;
	}

	public List<Gamemode> getGamemodes(){
		return ImmutableList.copyOf(gamemodes);
	}

	public int getNumberOfGamemodes(){
		return gamemodes.size();
	}

	public Set<Gamemode> getEnabledGamemodes(){
		Set<Gamemode> list = new HashSet<Gamemode>();
		for(Gamemode gameMode : gamemodes){
			if(gameMode.isEnabled()){
				list.add(gameMode);
			}
		}
		return list;
	}
}
