package com.goldornetwork.uhc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class JoinEvent implements Listener{

	private TeamManager teamM =  TeamManager.getInstance();
	private TimerManager timerM =  TimerManager.getInstance();
	private ScatterManager scatterM = ScatterManager.getInstance();
	private MessageSender ms = new MessageSender();
	
	
	public JoinEvent(UHC plugin){
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(timerM.hasMatchStarted()){
			if(teamM.isFFAEnabled()){
				if(scatterM.getLateScatters().contains(p.getUniqueId())){
					scatterM.lateScatterAPlayerInFFA(p);
					ms.send(ChatColor.GREEN, p, "You have been late scattered!");
				}
			}
			else if(teamM.isTeamsEnabled()){
				if(teamM.isPlayerInGame(p) && scatterM.getLateScatters().contains(p.getUniqueId())){
					scatterM.lateScatterAPlayerInATeam(teamM.getTeamOfPlayer(p), p);
					ms.send(ChatColor.GREEN, p, "You have been late scattered to your team spawn!");
				}
			}
			
			else if(teamM.isPlayerInGame(p)==false){
				if(!(p.getWorld().equals(scatterM.getUHCWorld()))){
					p.teleport(scatterM.getUHCWorld().getSpawnLocation());
				}
				if(teamM.isPlayerAnObserver(p)==false){
					teamM.addPlayerToObservers(p);
					
				}
				ms.send(ChatColor.AQUA, p, "You are now spectating the game");
				
			}
			
		}
		
	}
	
	
}
