package com.goldornetwork.uhc.managers.GameModeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.listeners.BreakEvent;
import com.goldornetwork.uhc.listeners.JoinEvent;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.BlockRush;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.BowListener;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.DeathEvent;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.DisabledCrafting;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.FlowerPower;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.GoneFishing;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.KingsManager;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.LandIsBadManager;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.LiveWithRegret;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.PotionSwap;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.SkyHigh;
import com.goldornetwork.uhc.managers.GameModeManager.gamemodes.TheHobbitManager;
import com.google.common.collect.ImmutableList;

public class GameModeManager {

	//instances
	private BowListener bowListener;
	private PotionSwap potionS;
	private KingsManager kingM;
	private DeathEvent deathE;
	private TheHobbitManager hobbitM;
	private DisabledCrafting disabledC;
	private JoinEvent joinE;
	private BreakEvent breakE;
	private FlowerPower flowerPowerM;
	private GoneFishing goneFishingM;
	private SkyHigh skyHighM;
	private BlockRush blockRushM;
	private LiveWithRegret liveWithRegretM;
	private LandIsBadManager landIsBadM;	
	private LocationListener locationL;
	Random random = new Random();

	//storage
	//private List<>
	private Map<String, List<String>> options = new HashMap<String, List<String>>();
	private List<Gamemode> gamemodes = new ArrayList<Gamemode>();
	private UHC plugin;

	//TODO work on constructor
	public GameModeManager(UHC plugin) {
		this.plugin=plugin;
	}
	public void setupGamemodes(TimerManager timerM, TeamManager teamM){
		
	}
	public Gamemode getGameMode(String name){
		for(Gamemode gameMode : gamemodes){
			if(gameMode.getName().equalsIgnoreCase(name)){
				return gameMode;
			}
		}
		return null;
	}
	public void setup(UHC plugin){
		this.plugin = plugin;
		options.clear();
	}

	public enum Gamemodes{
		SKYHIGH, SWITCHEROO, REWARDINGLONGSHOTS, POTIONSWAP, LIVEWITHREGRET, 
		KINGS, KILLSWITCH, THEHOBBIT, GONEFISHING, FLOWERPOWER, LANDISBAD, 
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

	public List<Gamemode> getGamemodes(){
		return ImmutableList.copyOf(gamemodes);
	}
	public List<Gamemode> getEnabledGamemodes(){
		List<Gamemode> list = new ArrayList<Gamemode>();
		for(Gamemode gameMode : gamemodes){
			if(gameMode.isEnabled()){
				list.add(gameMode);
			}
		}
		return list;
	}


	public void enableGamemode(Gamemodes gamemode){

		switch(gamemode){

		case SKYHIGH: skyHigh(true);//after pvp has started, all players must be at or above y=101, for every 30 seconds they are not, they take 1 heart of damage
		break;		//they receive a diamond shovel, snowballs, pumpkin

		case SWITCHEROO: switcheroo(true);//when someone shoots a bow and hits someone, they switch places with that person
		break;
		case REWARDINGLONGSHOTS: rewardingLongShots(true);//get rewards for hitting someone
		break;
		case POTIONSWAP: potionSwap(true); //new potion effects every 5 minutes
		break;
		case LIVEWITHREGRET: liveWithRegret(true); //If you die before pvp, you respawn with a random debuff
		break;
		case KINGS: kings(true); //player on team gets op effect and if he dies, teamates get debuff
		break;
		case KILLSWITCH: killSwitch(true); //you take your victim's inventory when you kill them
		break;
		case THEHOBBIT: theHobbit(true); //everyone gets a golden nugget, and when clicked you get invis for 30 sec and its gone
		break;
		case GONEFISHING: goneFishing(true); //INFINITE LEVELS, 20 ANVILS, FISHING ROD WITH LOFTS 250 & UNBREAKING 150, ENCHANT TABLES DISABLED
		break;
		case FLOWERPOWER: flowerPower(true);//WHEN YOU BREAK A FLOWER YOU GET A RANDOM AMOUNT OF A RANDOM ITEM
		break;
		case LANDISBAD: LandIsBad(true);//after pvp, players who are not underwater will take damage	
		break;
		case BLOCKRUSH: blockRush(true);//first player to break a unique block receives 1 diamond
		break;
		case TICKTOCK: //last player to have killed someone will regenerate 1 heart every minute
			break;
		default: Bukkit.getServer().getLogger().info("Unexpected error at executing enableGamemode");
		}


	}

	private void skyHigh(boolean val){
		skyHighM.setup(plugin);
		//timerM.enableSkyHigh(val);
		joinE.enableSkyHigh(val);
		locationL.enableSkyHigh(val);
	}
	private void switcheroo(boolean val){
		bowListener.enableSwitcheroo(val);
	}
	private void rewardingLongShots(boolean val){
		bowListener.enableRewardingLongshots(val);
	}

	private void potionSwap(boolean val){
		potionS.setup();
		//timerM.enablePotionSwap(val);
		joinE.enablePotionSwap(val);
	}
	private void liveWithRegret(boolean val){
		liveWithRegretM.setup();
		deathE.enableLiveWithRegret(val);
	}
	private void kings(boolean val){
		kingM.setup();
		kingM.enableKings(val);
	}

	private void killSwitch(boolean val){
		deathE.enableKillSwitch(val);
	}

	private void theHobbit(boolean val){
		hobbitM.setup();
		joinE.enableTheHobbit(val);
		//timerM.enableTheHobbit(val);
		hobbitM.enableTheHobbit(val);
	}

	private void goneFishing(boolean val){
		goneFishingM.setup();
		disabledC.enableGoneFishing(val);
		joinE.enableGoneFishing(val);
		//timerM.enableGoneFishing(val);
		flowerPowerM.addDisallowedItems(new ItemStack(125));//enchant table
	}
	private void flowerPower(boolean val){
		flowerPowerM.setup();
		breakE.enableFlowerPower(val);
	}
	private void LandIsBad(boolean val){
		landIsBadM.setup(plugin);
		locationL.enableLandIsBad(val);
	}
	private void blockRush(boolean val){
		blockRushM.setup();
		breakE.enableBlockRush(val);
	}

}
