package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.BoardManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;
import com.goldornetwork.uhc.managers.world.customevents.UHCJoinEvent;
import com.goldornetwork.uhc.utils.PlayerUtils;
import com.google.common.collect.ImmutableSet;

public class PotionSwap extends Gamemode implements Listener{


	private UHC plugin;
	private TeamManager teamM;
	private BoardManager boardM;
	private Random random = new Random();
	
	private int timer;
	
	private Set<UUID> latePotionPlayers = new HashSet<UUID>();
	private Map<UUID, PotionEffect> potionOfPlayer = new HashMap<UUID, PotionEffect>();
	
	public PotionSwap(UHC plugin, TeamManager teamM, BoardManager boardM) {
		super("Potion Swap","PotionSwap", "Every 5 minutes, players will receive a new potion effect.");
		this.plugin=plugin;
		this.teamM=teamM;
		this.boardM=boardM;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(UHCJoinEvent e){
		Player p = e.getPlayer();
		if(latePotionPlayers.contains(p.getUniqueId())){
			giveAPlayerARandomPotion(p);
			removePlayerFromLateGive(p);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(GameStartEvent e){
		run();
	}
	
	private int getInterval(){
		return 5*60;
	}

	private void startTimer(){

		boardM.customTimer(ChatColor.YELLOW + "Next Potion: ", new Callable<Integer>() {
			public Integer call(){
				return timer;
			}
		});
	}
	private void run(){
		timer = getInterval();
		
		startTimer();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(timer==0){
					distributePotions();
					timer = getInterval();
					startTimer();
				}
				else{
					timer--;
				}
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	private void distributePotions(){
		for(UUID u : teamM.getPlayersInGame()){
			if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
				giveAPlayerARandomPotion(Bukkit.getServer().getPlayer(u));
			}
			else{
				latePotionPlayers.add(u);
			}
		}
	}
	private void giveAPlayerARandomPotion(Player p){
		if(potionOfPlayer.containsKey(p.getUniqueId())){
			for(PotionEffect playerEffects : p.getActivePotionEffects()){
				if(PlayerUtils.arePotionsSimilar(playerEffects, potionOfPlayer.get(p.getUniqueId()))){
					p.removePotionEffect(playerEffects.getType());
				}
			}
		}
		
		PotionEffect effect = new PotionEffect(getRandomPotion(), Integer.MAX_VALUE, 0);
		p.addPotionEffect(effect, false);
		p.getWorld().playEffect(p.getLocation(), Effect.POTION_BREAK, 10);
		potionOfPlayer.put(p.getUniqueId(), effect);
	}

	public void removePlayerFromLateGive(Player p){
		latePotionPlayers.remove(p.getUniqueId());
	}

	private static final Set<PotionEffectType> ValidPotions = ImmutableSet.of(
			PotionEffectType.ABSORPTION,
			PotionEffectType.DAMAGE_RESISTANCE,
			PotionEffectType.FAST_DIGGING,
			PotionEffectType.FIRE_RESISTANCE,
			PotionEffectType.HUNGER,
			PotionEffectType.SATURATION,
			PotionEffectType.JUMP,
			PotionEffectType.NIGHT_VISION,
			PotionEffectType.SLOW,
			PotionEffectType.SLOW_DIGGING,
			PotionEffectType.SPEED,
			PotionEffectType.WATER_BREATHING,
			PotionEffectType.WEAKNESS,
			PotionEffectType.INVISIBILITY
			);
	
	private PotionEffectType getRandomPotion(){
		List<PotionEffectType> toReturn = new ArrayList<PotionEffectType>();
		toReturn.addAll(ValidPotions);
		int index = random.nextInt(toReturn.size());
		return toReturn.get(index);
	}
	

	@EventHandler
	public void on(PlayerItemConsumeEvent e){

		if(!(State.getState().equals(State.INGAME))){
			return;
		}
		if(e.getItem().getType().equals(Material.MILK_BUCKET)){
			if(teamM.isPlayerInGame(e.getPlayer().getUniqueId())){
				e.setCancelled(true);
			}
		}
	}	
}
