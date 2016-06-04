package com.goldornetwork.uhc.managers.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
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
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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
	private Random random = new Random();
	
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
		//fireworks(scatterM.getLobby());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player target = e.getPlayer();
		e.setJoinMessage(ChatColor.GREEN + "\u2713" + ChatColor.DARK_GRAY +  target.getName());
		target.setHealth(target.getHealth());
		for(Player online : Bukkit.getOnlinePlayers()){
			online.hidePlayer(target);
			online.showPlayer(target);
		}
		if(State.getState().equals(State.INGAME)|| State.getState().equals(State.SCATTER)){
			if(teamM.isPlayerInGame(e.getPlayer())){
					if(scatterM.getLateScatters().contains(target.getUniqueId())){
						scatterM.handleLateScatter(target);
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
		
		if(winners!=null){
			List<String> toBroadcast = new ArrayList<String>();
			toBroadcast.add("Winners are: ");
			
			fireworks(scatterM.getUHCWorld());
			
			
			
			for(UUID u : winners){
				toBroadcast.add(teamM.getColorOfPlayer(Bukkit.getOfflinePlayer(u)) + Bukkit.getServer().getOfflinePlayer(u).getName());
				if(Bukkit.getOfflinePlayer(u).isOnline()){
					Player target = Bukkit.getServer().getPlayer(u);
					target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4));
				}
			}
			
			MessageSender.broadcast(toBroadcast);
		}
		
	}
	
	private void fireworks(World world){
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(Player online : Bukkit.getServer().getOnlinePlayers()){
					Location pLoc = online.getLocation();
					int variation = random.nextInt(10);
					Color color = Color.RED;
					Location fireLoc = new Location(world, pLoc.getBlockX() + variation, pLoc.getBlockY(), pLoc.getBlockZ() + variation);
					Firework fw = (Firework) world.spawn(fireLoc, Firework.class);
					FireworkMeta fwMeta = fw.getFireworkMeta();
					fwMeta.addEffect(FireworkEffect.builder()
							.flicker(false)
							.trail(true)
							.with(FireworkEffect.Type.BALL_LARGE)
							.withColor(color)
							.withFade(Color.BLUE)
							.build());
					fwMeta.setPower(3);
					fw.setFireworkMeta(fwMeta);
					fw.detonate();
					
				}
				
			}
		}.runTaskTimer(plugin, 0L, 30L);
		
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
