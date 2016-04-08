package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.GameStartEvent;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;

public class PotionSwap extends Gamemode implements Listener{

	
	//instances
	private UHC plugin;
	private TeamManager teamM;
	//storage
	private List<UUID> latePotionPlayers = new ArrayList<UUID>();
	
	public PotionSwap(UHC plugin, TeamManager teamM) {
		super("PotionSwap", "Every 5 minutes, players will receive a new potion effect!");
		this.plugin=plugin;
		this.teamM=teamM;
	}

	@Override
	public void onEnable() {
		latePotionPlayers.clear();
	}
	
	@Override
	public void onDisable() {}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(State.getState().equals(State.INGAME)){
			if(latePotionPlayers.contains(p.getUniqueId())){
				giveAPlayerARandomPotion(p);
				removePlayerFromLateGive(p);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(GameStartEvent e){
		run();
	}
	
	private void run(){
		plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){
			@Override
			public void run() {
				for(UUID u : teamM.getPlayersInGame()){
					if(Bukkit.getServer().getPlayer(u).isOnline()){
						giveAPlayerARandomPotion(Bukkit.getServer().getPlayer(u));
					}
					else{
						latePotionPlayers.add(u);
					}
				}
			}
			
		}, 0L, 6000L); //5 minutes
	}
	
	private void giveAPlayerARandomPotion(Player p){
		p.addPotionEffect(new PotionEffect(getRandomPotion(), 5980, 1));
		p.getWorld().playEffect(p.getLocation(), Effect.POTION_BREAK, 10);
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
