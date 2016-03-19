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

import com.goldornetwork.uhc.managers.TeamManager;

public class PotionSwap implements Runnable {
//TODO give new effect to offline players through onjoinevent
	private static PotionSwap instance = new PotionSwap();
	private TeamManager teamM = TeamManager.getInstance();
	private boolean enablePotionSwap = true;
	private List<UUID> latePotionPlayers = new ArrayList<UUID>();
	
	public static PotionSwap getInstance(){
		return instance;
	}
	
	public void enablePotionSwap(boolean val){
		this.enablePotionSwap=val;
	}
	public List<UUID> getLatePotionPlayers(){
		return latePotionPlayers;
	}
	public void lateGiveAPlayerAPotion(Player p){
		p.addPotionEffect(new PotionEffect(getRandomPotion(), 6000, 1));
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
		
		}
		return potion;
	}
	
	
	@Override
	public void run() {
		if(enablePotionSwap){
			for(UUID u : teamM.getPlayersInGame()){
				if(Bukkit.getServer().getPlayer(u).isOnline()){
					PotionEffectType potion = getRandomPotion();
					Bukkit.getServer().getPlayer(u).addPotionEffect(new PotionEffect(potion, 6000, 1));
					Bukkit.getServer().getPlayer(u).getWorld().playEffect(Bukkit.getServer().getPlayer(u).getLocation(), Effect.POTION_BREAK, 10);
				}
				else{
					latePotionPlayers.add(u);
				}
				
			}
		}
		
	}

	
	
	
}
