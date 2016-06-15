package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.world.events.PVPEnableEvent;
import com.goldornetwork.uhc.utils.MessageSender;

public class LandIsBad extends Gamemode implements Listener{

	//instances
	private TeamManager teamM;
	//storage
	private UHC plugin;
	private Map<UUID, BukkitTask> playersToDamage= new HashMap<UUID, BukkitTask>();

	public LandIsBad(UHC plugin, TeamManager teamM) {
		super("Land is Bad", "LandIsBad", "After PVP is enabled, players who are not underwater will take a heart of damage every ten seconds.");
		this.plugin=plugin;
		this.teamM=teamM;
	}
	@Override
	public void onEnable() {

	}

	@EventHandler
	public void on(PVPEnableEvent e){
		plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){
			@Override
			public void run() {
				runTask();
			}

		}, 0L, 20L);

	}
	private void runTask() {
		for(UUID u : teamM.getPlayersInGame()){
			if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
				Player p = Bukkit.getServer().getPlayer(u);
				if(p.getRemainingAir()!=p.getMaximumAir()){
					p.setRemainingAir(p.getMaximumAir()-1);
					if(playersToDamage.containsKey(u)){
						if(teamM.isPlayerInGame(u)){
							MessageSender.send(ChatColor.GREEN, Bukkit.getServer().getPlayer(u), "You are now breathing water.");
							playersToDamage.get(u).cancel();
							playersToDamage.remove(u);
						}
						else{
							playersToDamage.get(u).cancel();
							playersToDamage.remove(u);
						}
					}
				}
				else{
					if(playersToDamage.containsKey(u)==false){
						if(teamM.isPlayerInGame(u)){
							BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){
								@Override
								public void run() {
									if(teamM.isPlayerInGame(u)){
										if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
											Bukkit.getServer().getPlayer(u).damage(2);
											MessageSender.send(ChatColor.RED, Bukkit.getServer().getPlayer(u), "Get to water.");
										}
									}
									else{
										playersToDamage.get(u).cancel();
										playersToDamage.remove(u);
									}
								}

							}, 200L, 200L);
							playersToDamage.put(u, task);
						}
						
					}
				}
			}
		}	

	}
}
