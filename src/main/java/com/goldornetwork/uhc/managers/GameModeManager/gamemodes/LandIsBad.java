package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

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
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class LandIsBad extends Gamemode{

	//instances
	private TeamManager teamM;
	private TimerManager timerM;
	//storage
	private UHC plugin;
	private Map<UUID, BukkitTask> playersToDamage= new HashMap<UUID, BukkitTask>();

	public LandIsBad(UHC plugin, TeamManager teamM, TimerManager timerM) {
		super("LandIsBad", "After PVP is enabled, players who are not underwater will take a heart of damage every ten seconds!");
		this.plugin=plugin;
		this.teamM=teamM;
		this.timerM=timerM;
	}
	@Override
	public void onEnable() {
		plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){
			@Override
			public void run() {
				runTask();
			}

		}, 0L, 20L);
	}
	private void runTask() {
		if(State.getState().equals(State.INGAME)){
			if(timerM.isPVPEnabled()){
				for(UUID u : teamM.getPlayersInGame()){
					if(Bukkit.getServer().getPlayer(u).isOnline()){
						Player p = Bukkit.getServer().getPlayer(u);
						if(p.getRemainingAir()!=p.getMaximumAir()){
							p.setRemainingAir(p.getMaximumAir()-1);
							if(playersToDamage.containsKey(u)){
								MessageSender.send(ChatColor.GREEN, Bukkit.getServer().getPlayer(u), "You are now breathing water!");
								playersToDamage.get(u).cancel();
								playersToDamage.remove(u);
							}
						}
						else{
							if(playersToDamage.containsKey(u)==false){
								BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){
									@Override
									public void run() {
										Bukkit.getServer().getPlayer(u).damage(2);
										MessageSender.send(ChatColor.RED, Bukkit.getServer().getPlayer(u), "Get to water!");
									}

								}, 0L, 200L);
								playersToDamage.put(u, task);
							}
						}
					}
				}
			}
		}

	}
}
