package com.goldornetwork.uhc.managers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.GameStartEvent;
import com.goldornetwork.uhc.utils.MessageSender;

public class Medic implements Listener {

	private UHC plugin;
	private TeamManager teamM;

	private int timeTillHeal;
	public Medic(UHC plugin, TeamManager teamM) {
		this.plugin=plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.teamM=teamM;
		timeTillHeal=15;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(GameStartEvent e){
		new BukkitRunnable() {

			@Override
			public void run() {
				if(timeTillHeal == 0){
					MessageSender.broadcast(ChatColor.GOLD + "All players have been healed.");
					for(UUID u : teamM.getPlayersInGame()){
						if(Bukkit.getOfflinePlayer(u).isOnline()){
							Bukkit.getPlayer(u).setHealth(Bukkit.getPlayer(u).getMaxHealth());
							Bukkit.getPlayer(u).setFoodLevel(20);
							Bukkit.getPlayer(u).setSaturation(20L);
						}
					}
					cancel();
				}
				else if(timeTillHeal>0){
					if(timeTillHeal<=5){
						MessageSender.broadcast(ChatColor.GOLD + "Final heal in " + ChatColor.GRAY + timeTillHeal);
					}
				}
				timeTillHeal--;

			}
		}.runTaskTimer(plugin, 0L, 20L);
	}
}
