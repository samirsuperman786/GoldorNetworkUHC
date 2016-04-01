package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.listeners.JoinEvent;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;

import io.netty.util.Timer;

public class PotionSwap{

	
	//instances
	private UHC plugin;
	private TeamManager teamM;
	private TimerManager timerM;
	private JoinEvent joinE;
	//storage
	private List<UUID> latePotionPlayers = new ArrayList<UUID>();
	
	public PotionSwap(UHC plugin, TeamManager teamM, TimerManager timerM, JoinEvent joinE) {
		this.plugin=plugin;
		this.teamM=teamM;
		this.timerM=timerM;
		this.joinE=joinE;
	}

	
	public void setup(){
		latePotionPlayers.clear();
		timerM.enablePotionSwap(false);
		joinE.enablePotionSwap(false);
	}
	public void enablePotionSwap(boolean val){
		timerM.enablePotionSwap(val);
		joinE.enablePotionSwap(val);
	}
	public void run(){
		plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){
			@Override
			public void run() {
				for(UUID u : teamM.getPlayersInGame()){
					if(Bukkit.getServer().getPlayer(u).isOnline()){
						giveAPlayerARandomPotion(Bukkit.getServer().getPlayer(u));
					}
					else{
						addAPlayerToLateGive(u);
					}
				}
			}
			
		}, 0L, 6000L); //5 minutes
	}
	
	public List<UUID> getLatePotionPlayers(){
		return latePotionPlayers;
	}
	public void giveAPlayerARandomPotion(Player p){
		p.addPotionEffect(new PotionEffect(getRandomPotion(), 6000, 1));
		p.getWorld().playEffect(p.getLocation(), Effect.POTION_BREAK, 10);
	}
	public void giveAPlayerARandomPotion(Player p, int durationInSeconds){
		p.addPotionEffect(new PotionEffect(getRandomPotion(), durationInSeconds*20, 1));
	}
	public void addAPlayerToLateGive(UUID u){
		this.latePotionPlayers.add(u);
	}
	public void removePlayerFromLateGive(Player p){
		latePotionPlayers.remove(p.getUniqueId());
	}
	
	
	private PotionEffectType getRandomPotion(){
		Random random = new Random();
		PotionEffectType potion = null;
		switch(random.nextInt(15)){
		
		case 1: potion = PotionEffectType.BLINDNESS;
				break;
		case 2: potion = PotionEffectType.CONFUSION;
				break;
		case 3: potion = PotionEffectType.DAMAGE_RESISTANCE;
				break;
		case 4: potion = PotionEffectType.FAST_DIGGING;
				break;
		case 5: potion = PotionEffectType.FIRE_RESISTANCE;
				break;
		case 6: potion = PotionEffectType.HUNGER;
				break;
		case 7: potion = PotionEffectType.JUMP;
				break;
		case 8: potion = PotionEffectType.NIGHT_VISION;
				break;
		case 9: potion = PotionEffectType.SLOW;
				break;
		case 10: potion = PotionEffectType.SLOW_DIGGING;
				break;
		case 11: potion = PotionEffectType.SPEED;
				break;
		case 12: potion = PotionEffectType.WATER_BREATHING;
				break;
		case 13: potion = PotionEffectType.WEAKNESS;
				break;
		case 14: potion = PotionEffectType.INVISIBILITY;
				break;
		case 15: potion = PotionEffectType.INCREASE_DAMAGE;
				break;
		default: Bukkit.getServer().getLogger().info("Unexpected error at executing PotionSwap");
		
		}
		return potion;
	}
	
	

	
	
	
}
