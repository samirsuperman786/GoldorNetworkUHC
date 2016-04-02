package com.goldornetwork.uhc.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class JoinEvent implements Listener{

	//instances
	private TeamManager teamM;
	private ScatterManager scatterM;
	
	public JoinEvent(UHC plugin, TeamManager teamM, ScatterManager scatterM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.teamM=teamM;
		this.scatterM=scatterM;
		
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
			if(State.getState().equals(State.INGAME)|| State.getState().equals(State.SCATTER)){
				if(teamM.isPlayerInGame(e.getPlayer())){
					if(teamM.isFFAEnabled()){
						if(scatterM.getLateScatters().contains(p.getUniqueId())){
							scatterM.lateScatterAPlayerInFFA(p);
							scatterM.removePlayerFromLateScatters(p);
							MessageSender.send(ChatColor.GREEN, p, "You have been late scattered!");
						}
					}
					else if(teamM.isTeamsEnabled()){
						if(scatterM.getLateScatters().contains(p.getUniqueId())){
							scatterM.lateScatterAPlayerInATeam(teamM.getTeamOfPlayer(p), p);
							scatterM.removePlayerFromLateScatters(p);
							MessageSender.send(ChatColor.GREEN, p, "You have been late scattered to your teams spawn!");
						}
					}
				}
				else if(teamM.isPlayerInGame(e.getPlayer())==false){
					if(p.getWorld().equals(scatterM.getUHCWorld())==false){
						p.teleport(scatterM.getUHCWorld().getSpawnLocation());
					}
					if(teamM.isPlayerAnObserver(p)==false){
						teamM.addPlayerToObservers(p);
					}
					MessageSender.send(ChatColor.AQUA, p, "You are now spectating the game");
				}
			}
				
			
			
		

	}


}
