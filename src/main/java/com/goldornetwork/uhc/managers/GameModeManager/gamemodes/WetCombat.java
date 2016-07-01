package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.world.customevents.MeetupEvent;
import com.goldornetwork.uhc.utils.MessageSender;

public class WetCombat extends Gamemode implements Listener{


	private TeamManager teamM;
	
	private UHC plugin;
	private Map<UUID, BukkitTask> playersToDamage= new HashMap<UUID, BukkitTask>();

	
	public WetCombat(UHC plugin, TeamManager teamM){
		super("Wet Combat", "WetCombat", "At meetup, players who are not underwater will take a heart of damage every thirty seconds.");
		this.plugin=plugin;
		this.teamM=teamM;
	}

	@EventHandler
	public void on(MeetupEvent e){

		new BukkitRunnable(){
			@Override
			public void run(){
				runChecker();
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	private void runChecker(){

		for(UUID u : teamM.getPlayersInGame()){
			if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
				Player p = Bukkit.getServer().getPlayer(u);
				if(p.getRemainingAir()!=p.getMaximumAir()){
					p.setRemainingAir(p.getMaximumAir()-1);
					if(playersToDamage.containsKey(u)){

						if(teamM.isPlayerInGame(u)){
							MessageSender.send(p, ChatColor.GREEN + "You are now breathing water.");
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
							if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
								MessageSender.send(Bukkit.getServer().getPlayer(u), ChatColor.RED + "Get to water.");
							}
							
							BukkitTask task = new BukkitRunnable(){
								@Override
								public void run(){
									if(teamM.isPlayerInGame(u)){
										if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
											Bukkit.getServer().getPlayer(u).damage(2);
											MessageSender.send(Bukkit.getServer().getPlayer(u), ChatColor.RED + "Get to water.");
										}
									}
									else{
										playersToDamage.get(u).cancel();
										playersToDamage.remove(u);
									}
								}
							}.runTaskTimer(plugin, 600L, 600L);

							playersToDamage.put(u, task);
						}
					}
				}
			}
		}	
	}
}
