package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.events.GameStartEvent;
import com.google.common.collect.ImmutableSet;

public class PotionSwap extends Gamemode implements Listener{

	
	//instances
	private UHC plugin;
	private TeamManager teamM;
	private Random random = new Random();
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
					if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
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
		for(PotionEffect effect : p.getActivePotionEffects()){
			p.removePotionEffect(effect.getType());
		}
		p.addPotionEffect(new PotionEffect(getRandomPotion(), 6200, 0));
		p.getWorld().playEffect(p.getLocation(), Effect.POTION_BREAK, 10);
	}
	public void removePlayerFromLateGive(Player p){
		latePotionPlayers.remove(p.getUniqueId());
	}
	
	private static final Set<PotionEffectType> ValidPotions = ImmutableSet.of(
			PotionEffectType.BLINDNESS, 
			PotionEffectType.CONFUSION,
			PotionEffectType.DAMAGE_RESISTANCE,
			PotionEffectType.FAST_DIGGING,
			PotionEffectType.FIRE_RESISTANCE,
			PotionEffectType.HUNGER,
			PotionEffectType.JUMP,
			PotionEffectType.NIGHT_VISION,
			PotionEffectType.SLOW,
			PotionEffectType.SLOW_DIGGING,
			PotionEffectType.SPEED,
			PotionEffectType.WATER_BREATHING,
			PotionEffectType.WEAKNESS,
			PotionEffectType.INVISIBILITY,
			PotionEffectType.INCREASE_DAMAGE
			);
	private PotionEffectType getRandomPotion(){
		List<PotionEffectType> toReturn = new ArrayList<PotionEffectType>();
		toReturn.addAll(ValidPotions);
		int index = random.nextInt(toReturn.size());
		return toReturn.get(index);
	}
	
	

	
	
	
}
