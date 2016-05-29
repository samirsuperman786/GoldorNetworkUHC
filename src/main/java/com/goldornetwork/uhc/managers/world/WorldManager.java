package com.goldornetwork.uhc.managers.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.Medic;
import com.goldornetwork.uhc.utils.MessageSender;

public class WorldManager implements Listener{

	private UHC plugin;
	private ScatterManager scatterM;
	private TeamManager teamM;
	
	private Map<UUID, Location> lastLocation = new HashMap<UUID, Location>();
	
	public WorldManager(UHC plugin, ScatterManager scatterM, TeamManager teamM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin=plugin;
		this.scatterM=scatterM;
		this.teamM=teamM;
	}
	
	public void setup(){
		for(Player all : Bukkit.getOnlinePlayers()){
			all.setGameMode(GameMode.ADVENTURE);
			all.setMaxHealth(20);
			all.setLevel(0);
			all.setExp(0L);
			Medic.heal(all);
			for(PotionEffect effect : all.getActivePotionEffects()){
				all.removePotionEffect(effect.getType());
			}
			all.setDisplayName(all.getName());
			all.getInventory().clear();
			all.getInventory().setArmorContents(null);
			all.teleport(scatterM.getLobby().getSpawnLocation());
			
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player target = e.getPlayer();
		e.setJoinMessage(ChatColor.GREEN + "\u2713" + ChatColor.DARK_GRAY +  target.getName());
		
		if(State.getState().equals(State.INGAME)|| State.getState().equals(State.SCATTER)){
			if(teamM.isPlayerInGame(e.getPlayer())){
					if(scatterM.getLateScatters().contains(target.getUniqueId())){
						scatterM.handleLateScatter(target);
					}
					else if(lastLocation.containsKey(target.getUniqueId())){
						target.teleport(lastLocation.get(target.getUniqueId()));
						lastLocation.remove(target.getUniqueId());
					}
				
			}
			else if(teamM.isPlayerInGame(e.getPlayer())==false){
				if(target.getWorld().equals(scatterM.getUHCWorld())==false){
					target.teleport(scatterM.getUHCWorld().getSpawnLocation());
				}
				if(teamM.isPlayerAnObserver(target)==false){
					teamM.addPlayerToObservers(target);
				}
				else{
					MessageSender.send(ChatColor.AQUA, target, "You are now spectating the game");
				}
				
			}
		}
		
	}
	

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e){
		Player target = e.getPlayer();
		e.setQuitMessage(ChatColor.RED + "\u2717" + ChatColor.DARK_GRAY+ target.getName());
		if(State.getState().equals(State.INGAME)){
			if(teamM.isPlayerInGame(target)){
				lastLocation.put(target.getUniqueId(), target.getLocation());
			}
		}
		else if(State.getState().equals(State.SCATTER)){
			if(!(scatterM.getLateScatters().contains(target))){
				lastLocation.put(target.getUniqueId(), target.getLocation());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void on(FoodLevelChangeEvent e){
		if(!(State.getState().equals(State.INGAME))){
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void on(EntityDamageEvent e){
		if(!(State.getState().equals(State.INGAME))){
			if(e.getEntity() instanceof Player){
				e.setCancelled(true);	
			}
		}
		
	}
	
	@EventHandler
	public void on(PlayerLoginEvent e){
		if(e.getResult().equals(PlayerLoginEvent.Result.KICK_OTHER) || e.getResult().equals(PlayerLoginEvent.Result.KICK_WHITELIST)|| e.getResult().equals(PlayerLoginEvent.Result.KICK_FULL)){
			if(e.getPlayer().hasPermission("uhc.whitelist.bypass")){
				e.allow();
			}
		}
	}
	
	
	public void endGame(List<UUID> winners){
		MessageSender.broadcast("Game has ended!");
		MessageSender.broadcast("Winners are: ");
		for(UUID u : winners){
			MessageSender.broadcast(Bukkit.getServer().getOfflinePlayer(u).getName());
			if(Bukkit.getOfflinePlayer(u).isOnline()){
				Player target = Bukkit.getServer().getPlayer(u);
				target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4));
			}
		}
	}
	
	public void endGame(){
		MessageSender.broadcast("Game has ended!");
		MessageSender.broadcast("No one has won!");
		
	}
	@EventHandler
	public void on(PlayerChangedWorldEvent e){
		if(e.getFrom().getPlayers().isEmpty()){
			Bukkit.unloadWorld(e.getFrom(), false);
		}
	}
}
