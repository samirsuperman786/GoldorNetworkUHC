package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;
import com.goldornetwork.uhc.managers.world.customevents.MeetupEvent;
import com.goldornetwork.uhc.managers.world.customevents.UHCEnterMapEvent;
import com.goldornetwork.uhc.utils.MessageSender;

public class SkyHigh extends Gamemode implements Listener{

	
	private TeamManager teamM;

	private Map<UUID, BukkitTask> playersToDamage= new HashMap<UUID, BukkitTask>();
	private Set<UUID> lateSkyHigh = new HashSet<UUID>();
	private UHC plugin;
	
	
	public SkyHigh(UHC plugin, TeamManager teamM) {
		super("Sky High", "SkyHigh", "At meetup, players who are not above y=100 will take a heart of damage every 30 seconds.");
		this.plugin=plugin;
		this.teamM=teamM;
	}

	@Override
	public void onEnable() {
		playersToDamage.clear();
		lateSkyHigh.clear();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(GameStartEvent e){
		distributeItems();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(MeetupEvent e){

		new BukkitRunnable() {
			@Override
			public void run() {
				runChecker();
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(UHCEnterMapEvent e){
		Player p = e.getPlayer();
		if(lateSkyHigh.contains(p.getUniqueId())){
			giveAPlayerSkyHighItems(p);
			removePlayerFromLateSkyHigh(p);
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

	private void runChecker(){
		for(UUID u : teamM.getPlayersInGame()){
			if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
				if(Bukkit.getServer().getPlayer(u).getLocation().getBlockY()<=100){
					if(playersToDamage.containsKey(u)==false){
						if(teamM.isPlayerInGame(u)){
							MessageSender.send(Bukkit.getServer().getPlayer(u), ChatColor.RED + "You are below Y: 100");
						}
						
						BukkitTask task = new BukkitRunnable(){
							
							@Override
							public void run() {
								if(teamM.isPlayerInGame(u)){
									if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
										MessageSender.send(Bukkit.getServer().getPlayer(u), ChatColor.RED + "You are below Y: 100");
										Bukkit.getServer().getPlayer(u).damage(2);
									}
								}
							}
						}.runTaskTimer(plugin, 600L, 600L);

						playersToDamage.put(u, task);
					}
				}
				else if(Bukkit.getServer().getPlayer(u).getLocation().getBlockY()>=101){
					if(playersToDamage.containsKey(u)){
						if(teamM.isPlayerInGame(u)){
							MessageSender.send(Bukkit.getServer().getPlayer(u), ChatColor.GREEN + "You are now above Y: 100");
							playersToDamage.get(u).cancel();
							playersToDamage.remove(u);
						}
						else{
							playersToDamage.remove(u);
						}
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
