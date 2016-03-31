package com.goldornetwork.uhc.managers.ModifierManager.gamemodes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.ModifierManager.LocationListener;
import com.goldornetwork.uhc.utils.MessageSender;

public class LandIsBadManager{

	//instances
	private TeamManager teamM = TeamManager.getInstance();
	private MessageSender ms = new MessageSender();
	private LocationListener locationL = LocationListener.getInstance();
	
	//storage
	private UHC plugin;
	private Map<UUID, BukkitTask> playersToDamage= new HashMap<UUID, BukkitTask>();

	private LandIsBadManager(){}
	
	private static class InstanceHolder{
		private static final LandIsBadManager INSTANCE = new LandIsBadManager();
	}
	public static LandIsBadManager getInstance(){
		
		return InstanceHolder.INSTANCE;
	}
	public void setup(UHC plugin){
		this.plugin = plugin;
		locationL.enableLandIsBad(false);
	}
	public void enableLandIsBad(boolean val){
		locationL.enableLandIsBad(val);
	}

	public void run() {
		for(UUID u : teamM.getPlayersInGame()){
			if(Bukkit.getServer().getPlayer(u).isOnline()){
				Player p = Bukkit.getServer().getPlayer(u);
				if(p.getRemainingAir()!=p.getMaximumAir()){
					p.setRemainingAir(p.getMaximumAir()-1);
					if(playersToDamage.containsKey(u)){
						ms.send(ChatColor.GREEN, Bukkit.getServer().getPlayer(u), "You are now breathing water");
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
								ms.send(ChatColor.RED, Bukkit.getServer().getPlayer(u), "Get to water!");
							}

						}, 0L, 200L);
						playersToDamage.put(u, task);
					}


				}
				
			}
		}

	}

}
