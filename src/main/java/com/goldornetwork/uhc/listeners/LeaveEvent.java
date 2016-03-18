package com.goldornetwork.uhc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;

public class LeaveEvent implements Listener{

	//TODO if match has started and player leaves, spawn a chicken with their name, if afk for more than 5 mins then kill them and remove from team/game
	private TeamManager teamM = TeamManager.getInstance();
	private TimerManager timerM = TimerManager.getInstance();
	private ScatterManager scatterM = ScatterManager.getInstance();
	
	public LeaveEvent(UHC plugin){
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeave(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(timerM.hasMatchStarted()==true && timerM.hasCountDownEnded()==false){
			//nothing
		}
		else if(timerM.hasCountDownEnded()==true){
			//p.getLocation().
		}
		
	}
	
}
