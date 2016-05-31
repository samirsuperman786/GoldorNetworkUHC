package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.GameStartEvent;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.PVPEnableEvent;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class SkyHigh extends Gamemode implements Listener{

	//instances
	private TeamManager teamM;
	
	//storage
	private Map<UUID, BukkitTask> playersToDamage= new HashMap<UUID, BukkitTask>();
	private List<UUID> lateSkyHigh = new ArrayList<UUID>();
	UHC plugin;
	
	public SkyHigh(UHC plugin, TeamManager teamM) {
		super("SkyHigh", "After PVP, players who are not above y=100 will take a heart of damage every 30 seconds!");
		this.plugin=plugin;
		this.teamM=teamM;
	}
	@Override
	public void onEnable() {
		playersToDamage.clear();
		lateSkyHigh.clear();
		
	}
	@Override
	public void onDisable() {}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(GameStartEvent e){
		distributeItems();
		
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PVPEnableEvent e){
		plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){

			@Override
			public void run() {
				runTask();
			}
			
		}, 0L, 20L);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(State.getState().equals(State.INGAME)){
			if(lateSkyHigh.contains(p.getUniqueId())){
				giveAPlayerSkyHighItems(p);
				removePlayerFromLateSkyHigh(p);
			}
		}
	}
	private void removePlayerFromLateSkyHigh(Player p){
		lateSkyHigh.remove(p.getUniqueId());
	}
	
	private void giveAPlayerSkyHighItems(Player p){
		p.getInventory().addItem(new ItemStack(Material.DIAMOND_SPADE, 1));
		p.getInventory().addItem(new ItemStack(Material.PUMPKIN, 10));
		p.getInventory().addItem(new ItemStack(Material.STAINED_CLAY, 64, (short) 13)); //green
		p.getInventory().addItem(new ItemStack(Material.STAINED_CLAY, 64, (short) 11));	//blue
		p.getInventory().addItem(new ItemStack(Material.SNOW_BLOCK, 64));
		p.getInventory().addItem(new ItemStack(Material.STRING, 2));
	}
	
	private void runTask(){
			for(UUID u : teamM.getPlayersInGame()){
				if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
				if(Bukkit.getServer().getPlayer(u).getLocation().getBlockY()<=100){
					if(playersToDamage.containsKey(u)==false){
						BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){
							@Override
							public void run() {
								if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
									Bukkit.getServer().getPlayer(u).damage(2);
									MessageSender.send(ChatColor.RED, Bukkit.getServer().getPlayer(u), "You are below Y: 101");
								}
								
							}
							
						}, 0L, 600L);
						playersToDamage.put(u, task);
					}
				}
				else if(Bukkit.getServer().getPlayer(u).getLocation().getBlockY()>=101){
					if(playersToDamage.containsKey(u)){
						MessageSender.send(ChatColor.GREEN, Bukkit.getServer().getPlayer(u), "You are now above Y: 100");
						playersToDamage.get(u).cancel();
						playersToDamage.remove(u);
					}
				}
			}
		}
		
	}
	
	private void distributeItems(){
		for(UUID u : teamM.getPlayersInGame()){
			if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
				Player p = Bukkit.getServer().getPlayer(u);
				giveAPlayerSkyHighItems(p);
			}
			else{
				lateSkyHigh.add(u);
			}
		}
	}
	
}
