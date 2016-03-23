package com.goldornetwork.uhc.managers.ModifierManager.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;

public class PotionSwap implements Runnable {
//TODO give new effect to offline players through onjoinevent
	private static PotionSwap instance = new PotionSwap();
	private TeamManager teamM = TeamManager.getInstance();
	private TimerManager timerM = TimerManager.getInstance();
	private ScatterManager scatterM = ScatterManager.getInstance();
	private boolean enablePotionSwap = true;
	private List<UUID> latePotionPlayers = new ArrayList<UUID>();
	
	
	public static PotionSwap getInstance(){
		return instance;
	}
	public void setup(){
		enablePotionSwap=false;
		latePotionPlayers.clear();
	}
	
	
	public void enablePotionSwap(boolean val){
		this.enablePotionSwap=val;
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
	
	public void removePlayerFromLateGive(Player p){
		latePotionPlayers.remove(p.getUniqueId());
	}
	
	
	private PotionEffectType getRandomPotion(){
		Random random = new Random();
		PotionEffectType potion = null;
		switch(random.nextInt(13)){
		
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
		default: Bukkit.getServer().getLogger().info("Unexpected error at executing PotionSwap");
		
		}
		return potion;
	}
	
	
	@Override
	public void run() {
		if(timerM.hasCountDownEnded() && scatterM.isScatteringComplete()){
			if(enablePotionSwap){
				for(UUID u : teamM.getPlayersInGame()){
					if(Bukkit.getServer().getPlayer(u).isOnline()){
						giveAPlayerARandomPotion(Bukkit.getServer().getPlayer(u));
					}
					else{
						latePotionPlayers.add(u);
					}
					
				}
			}
		}
		
		
	}

	
	
	
}
