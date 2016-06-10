package com.goldornetwork.uhc.managers.world.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;

public class LeaveEvent implements Listener{

	//instances
	private TeamManager teamM;
	private ScatterManager scatterM;

	public LeaveEvent(UHC plugin, TeamManager teamM, ScatterManager scatterM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.teamM=teamM;
		this.scatterM = scatterM;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(State.getState().equals(State.OPEN)){
			if(teamM.isPlayerInGame(p.getUniqueId())){
				 if(teamM.isTeamsEnabled()){
					if(teamM.isTeamInactive(teamM.getTeamOfPlayer(p.getUniqueId()))){
						//teamM.disbandTeam(teamM.getTeamOfPlayer(p.getUniqueId()));
					}
				}
			}
		}
		

	}

}
