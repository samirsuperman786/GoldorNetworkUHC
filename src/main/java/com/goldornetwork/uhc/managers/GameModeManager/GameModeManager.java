package com.goldornetwork.uhc.managers.GameModeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.BlockRush;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.FlowerPower;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.KillSwitch;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.KingsManager;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.LandIsBad;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.PotionSwap;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.RewardingLongshots;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.SkyHigh;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.Switcheroo;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.TheHobbitManager;
import com.google.common.collect.ImmutableList;

public class GameModeManager {

	/* Skyhigh is done
	 * switcheroo is done	
	 * rewardinglongshots is done
	 * potionswap is done
	 * livewith regret needs work on custom death messages
	 * kings is done
	 * killswitch is done
	 * thehobbit is done
	 * gonefishing is pretty much done TODO edit server config
	 * flowerpower almost done TODO add more restrictions on drops
	 * landisbad is done
	 * blockrush is done
	 * ticktock not done
	 */
	
	//instances
	Random random = new Random();

	//storage
	private List<Gamemode> gamemodes = new ArrayList<Gamemode>();
	private UHC plugin;

	public GameModeManager(UHC plugin) {
		this.plugin=plugin;
	}
	
	/**
	 * Used to initialize all game modes
	 * @param timerM - the TimerManager
	 * @param teamM - the TeamManager
	 * @param scatterM - the ScatterManager
	 */
	public void setupGamemodes(TimerManager timerM, TeamManager teamM, ScatterManager scatterM){
		gamemodes.add(new BlockRush(scatterM));
		gamemodes.add(new FlowerPower(teamM));
		gamemodes.add(new KillSwitch());
		gamemodes.add(new KingsManager(teamM));
		gamemodes.add(new LandIsBad(plugin, teamM, timerM));
		gamemodes.add(new PotionSwap(plugin, teamM));
		gamemodes.add(new RewardingLongshots(teamM));
		gamemodes.add(new SkyHigh(plugin, teamM));
		gamemodes.add(new Switcheroo(teamM));
		gamemodes.add(new TheHobbitManager(teamM));
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


	

	/**
	 * Used to retrieve a list of all game modes
	 * @return <code> List[Gamemode] </code> of all game modes
	 */
	public List<Gamemode> getGamemodes(){
		return ImmutableList.copyOf(gamemodes);
	}
	public int getNumberOfGamemodes(){
		return gamemodes.size();
	}

	/** 
	 * Used to retrieve all game modes that are enabled
	 * @return <code> List[Gamemode] </code> of enabled game modes
	 */
	public List<Gamemode> getEnabledGamemodes(){
		List<Gamemode> list = new ArrayList<Gamemode>();
		for(Gamemode gameMode : gamemodes){
			if(gameMode.isEnabled()){
				list.add(gameMode);
			}
		}
		return list;
	}





	// SKYHIGH - after pvp has started, all players must be at or above y=101, for every 30 seconds they are not, they take 1 heart of damage
		//they receive a diamond shovel, snowballs, pumpkin
	// SWITCHEROO- when someone shoots a bow and hits someone, they switch places with that person
	//REWARDING LONGSHOTS - get rewards for hitting someone
	//POTIONSWAP - new potion effects every 5 minutes
	//KINGS - player on team gets op effect and if he dies, teamates get debuff
	//KILLSWITCH - you take your victim's inventory when you kill them
	//THEHOBBIT - everyone gets a golden nugget, and when clicked you get invis for 30 sec and its gone
	//GONEFISHING - INFINITE LEVELS, 20 ANVILS, FISHING ROD WITH LOFTS 250 & UNBREAKING 150, ENCHANT TABLES DISABLED
	//FLOWERPOWER - WHEN YOU BREAK A FLOWER YOU GET A RANDOM AMOUNT OF A RANDOM ITEM
	//LANDISBAD - after pvp, players who are not underwater will take damage	
	//BLOCKRUSh -first player to break a unique block receives 1 diamond
	//TICKTOCK - last player to have killed someone will regenerate 1 heart every minute


}
