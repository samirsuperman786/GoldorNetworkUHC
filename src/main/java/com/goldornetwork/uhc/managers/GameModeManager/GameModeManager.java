package com.goldornetwork.uhc.managers.GameModeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;

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

	//instances
	Random random = new Random();

	//storage
	//private List<>
	private Map<String, List<String>> options = new HashMap<String, List<String>>();
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
	/**
	 * Used to get the class of a given game mode
	 * @param name - the name of a game mode
	 * @return <code> Gamemode </code> the class of the gamemode
	 */
	public Gamemode getGameMode(String name){
		for(Gamemode gameMode : gamemodes){
			if(gameMode.getName().equalsIgnoreCase(name)){
				return gameMode;
			}
		}
		return null;
	}
	public void setup(){
		options.clear();
	}

	/**
	 * List of game modes
	 * @author GOLD
	 *
	 */
	public enum Gamemodes{
		SKYHIGH, SWITCHEROO, REWARDINGLONGSHOTS, POTIONSWAP, KINGS, 
		KILLSWITCH, THEHOBBIT, GONEFISHING, FLOWERPOWER, LANDISBAD, 
		BLOCKRUSH, TICKTOCK

	}
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

	/**
	 * Used to retrieve a list of all game modes
	 * @return <code> List[Gamemode] </code> of all game modes
	 */
	public List<Gamemode> getGamemodes(){
		return ImmutableList.copyOf(gamemodes);
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


	/**
	 * Used to enable a specified game mode
	 * @param gamemode - the game mode to enable
	 */
	public void enableGamemode(Gamemodes gamemode){

		switch(gamemode){

		case SKYHIGH: startGamemode("SKYHIGH");//after pvp has started, all players must be at or above y=101, for every 30 seconds they are not, they take 1 heart of damage
		break;		//they receive a diamond shovel, snowballs, pumpkin

		case SWITCHEROO: startGamemode("SWITCHEROO");//when someone shoots a bow and hits someone, they switch places with that person
		break;
		case REWARDINGLONGSHOTS: startGamemode("REWARDINGLONGSHOTS");//get rewards for hitting someone
		break;
		case POTIONSWAP: startGamemode("POTIONSWAP"); //new potion effects every 5 minutes
		break;
		case KINGS: startGamemode("KINGS");//player on team gets op effect and if he dies, teamates get debuff
		break;
		case KILLSWITCH: startGamemode("KILLSWITCH"); //you take your victim's inventory when you kill them
		break;
		case THEHOBBIT: startGamemode("THEHOBBIT"); //everyone gets a golden nugget, and when clicked you get invis for 30 sec and its gone
		break;
		case GONEFISHING: startGamemode("GONEFISHING"); //INFINITE LEVELS, 20 ANVILS, FISHING ROD WITH LOFTS 250 & UNBREAKING 150, ENCHANT TABLES DISABLED
		break;
		case FLOWERPOWER: startGamemode("FLOWERPOWER");//WHEN YOU BREAK A FLOWER YOU GET A RANDOM AMOUNT OF A RANDOM ITEM
		break;
		case LANDISBAD: startGamemode("LANDISBAD");//after pvp, players who are not underwater will take damage	
		break;
		case BLOCKRUSH: startGamemode("BLOCKRUSH");//first player to break a unique block receives 1 diamond
		break;
		case TICKTOCK: startGamemode("TICKTOCK");//last player to have killed someone will regenerate 1 heart every minute
			break;
		default: Bukkit.getServer().getLogger().info("Unexpected error at executing enableGamemode");
		}


	}
	
	/**
	 * Used to actually start the game mode
	 * @param input
	 */
	private void startGamemode(String input){
		getGameMode(input).enable(plugin);
	}
}
