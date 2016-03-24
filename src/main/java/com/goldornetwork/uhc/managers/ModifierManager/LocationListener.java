package com.goldornetwork.uhc.managers.ModifierManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class LocationListener implements Runnable {

	private static LocationListener instance = new LocationListener();
	private TeamManager teamM = TeamManager.getInstance();
	private TimerManager timerM = TimerManager.getInstance();
	private MessageSender ms = new MessageSender();
	
	
	private Map<UUID, BukkitTask> playersToDamage= new HashMap<UUID, BukkitTask>();
	private boolean enableSkyHigh;
	private UHC plugin;
	public static LocationListener getInstance(){
		return instance;
	}
	public void setup(UHC plugin){
		this.plugin=plugin;
		this.enableSkyHigh=false;
	}
	
	public void enableSkyHigh(boolean val){
		this.enableSkyHigh=val;
	}

	
	@Override
	public void run() {
		if(enableSkyHigh){
			if(timerM.hasMatchStarted()){
				if(timerM.isPVPEnabled()){
					for(UUID u : teamM.getPlayersInGame()){
							if(Bukkit.getServer().getPlayer(u).isOnline()){
							if(Bukkit.getServer().getPlayer(u).getLocation().getBlockY()<=100){
								if(playersToDamage.containsKey(u)==false){
									BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){
										@Override
										public void run() {
											Bukkit.getServer().getPlayer(u).damage(2);
											ms.send(ChatColor.RED, Bukkit.getServer().getPlayer(u), "You are below y = 101!");
										}
										
									}, 0L, 600L);
									playersToDamage.put(u, task);
								}
								

							}
							else if(Bukkit.getServer().getPlayer(u).getLocation().getBlockY()>=101){
								if(playersToDamage.containsKey(u)){
									ms.send(ChatColor.GREEN, Bukkit.getServer().getPlayer(u), "You are now above y =100!");
									playersToDamage.get(u).cancel();
									playersToDamage.remove(u);
								}
							}
						}
					}
				}
			}
			
		}
		
		
	}

	
	
}
