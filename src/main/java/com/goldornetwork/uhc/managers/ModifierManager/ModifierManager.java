package com.goldornetwork.uhc.managers.ModifierManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import com.goldornetwork.uhc.managers.ModifierManager.actions.BowListener;
import com.goldornetwork.uhc.managers.ModifierManager.actions.DeathEvent;
import com.goldornetwork.uhc.managers.ModifierManager.actions.KingsManager;
import com.goldornetwork.uhc.managers.ModifierManager.actions.PotionSwap;

public class ModifierManager {

	private static ModifierManager instance = new ModifierManager();
	//instances of managers
	private BowListener bowListener = BowListener.getInstance();
	private PotionSwap potionS = PotionSwap.getInstance();
	private KingsManager kingM = KingsManager.getInstance();
	private DeathEvent deathE = DeathEvent.getInstance();
	//
	
	private Map<String, List<String>> options = new HashMap<String, List<String>>();
	private Map<Enum<Gamemodes>, Boolean> gameModesThatAreEnabled = new HashMap<Enum<Gamemodes>, Boolean> ();
	private int numberOfGamemodesToEnable = 3;
	
	
	
	public static ModifierManager getInstance(){
		return instance;
	}
	
	public void setup(){
		options.clear();
		gameModesThatAreEnabled.clear();
	}
	
	public enum Gamemodes{
		SKYHIGH, SWITCHEROO, REWARDINGLONGSHOTS, POTIONSWAP, LIVEWITHREGRET, 
		KINGS, KILLSWITCH, THEHOBBIT, GONEFISHING, FLOWERPOWER, LANDISBAD, 
		BLOCKRUSH, BIGCRACK, TICKTOCK
		
	}
	/* Skyhigh needs work on damagetick
	 * switcheroo is done	
	 * rewardinglongshots is done
	 * potioonswap is done
	 * livewith regret needs work on custom death messages
	 * kings not done
	 * killswitch not done
	 * thehobbit not done
	 * gonefishing not done
	 * flowerpower not done
	 * landisbad not done
	 * blockrush not done
	 * bigcrack not dome
	 * ticktock not done
	 */
	
	public Map<Enum<Gamemodes>, Boolean> getModesThatAreEnabled(){
		return gameModesThatAreEnabled;
	}
	
	
	public void enableGamemode(Gamemodes gamemode){
		
			switch(gamemode){
			
			case SKYHIGH: //after pvp has started, all players must be at or above y=101, for every 30 seconds they are not, they take 1 heart of damage
					break;		//they receive a diamond shovel, snowballs, pumpkin
					
			case SWITCHEROO: switcheroo(true);//when someone shoots a bow and hits someone, they switch places with that person
					break;
			case REWARDINGLONGSHOTS: rewardingLongShots(true);//get rewards for hitting someone
					break;
			case POTIONSWAP: potionSwap(true); //new potion effects every 5 minutes
					break;
			case LIVEWITHREGRET: //If you die before pvp, you respawn with a random debuff
					break;
			case KINGS: kings(true); //player on team gets op effect and if he dies, teamates get debuff
					break;
			case KILLSWITCH: killSwitch(true); //you take your victim's inventory when you kill them
					break;
			case THEHOBBIT:  //everyone gets a golden nugget, and when clicked you get invis for 30 sec and its gone
					break;
			case GONEFISHING: //INFINITE LEVELS, 20 ANVILS, FISHING ROD WITH LOFTS 250 & UNBREAKING 150, ENCHANT TABLES DISABLED
					break;
			case FLOWERPOWER: //WHEN YOU BREAK A FLOWER YOU GET A RANDOM AMOUNT OF A RANDOM ITEM
					break;
			case LANDISBAD: //overpowered mobs spawn on land, the only safe place is underwater, NO Breathing damage		
					break;
			case BLOCKRUSH: //first player to break a unique block receives 1 diamond
					break;
			case BIGCRACK: //entire meetup area will be void and players must bridge over it
					break;
			case TICKTOCK: //last player to have killed someone will regenerate 1 heart every minute
					break;
			
		}
		
		
	}
	
	private void SkyHigh(){
		
	}
	private void switcheroo(boolean val){
		bowListener.enableSwitcheroo(val);
	}
	private void rewardingLongShots(boolean val){
		bowListener.enableRewardingLongshots(val);
	}
	
	private void potionSwap(boolean val){
		potionS.enablePotionSwap(val);
		
	}
	private void liveWithRegret(boolean val){
		
	}
	private void kings(boolean val){
		kingM.enableKings(val);
	}
	
	private void killSwitch(boolean val){
		deathE.enableKillSwitch(val);
	}
	
}
