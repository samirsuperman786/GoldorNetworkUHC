package com.goldornetwork.uhc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;

public class LeaveEvent implements Listener{

	//TODO if match has started and player leaves, spawn a chicken with their name, if afk for more than 5 mins then kill them and remove from team/game

	//instances
	private TeamManager teamM;
	private TimerManager timerM;
	//private ScatterManager scatterM = ScatterManager.getInstance();

	public LeaveEvent(TeamManager teamM, TimerManager timerM) {
		this.teamM=teamM;
		this.timerM=timerM;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeave(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(timerM.hasMatchStarted()==true && timerM.hasCountDownEnded()==false){
			if(teamM.isPlayerInGame(p)){
				if(teamM.isFFAEnabled()){
					teamM.removePlayerFromFFA(p);
				}
				else if(teamM.isTeamsEnabled()){
					if(teamM.isPlayerOwner(p)){
						teamM.removePlayerFromOwner(p);
					}
					teamM.removePlayerFromTeam(p);
				}

			}
		}
		//TODO make below false
		/*		else if(timerM.hasCountDownEnded()==false){
			if(teamM.isPlayerInGame(p)==false){
				LivingEntity chicken = (LivingEntity) scatterM.getUHCWorld().spawnEntity(p.getLocation(), EntityType.CHICKEN);
				chicken.setCustomName(p.getName());
				chicken.setHealth(p.getHealth());
			}

		}*/

	}

}
