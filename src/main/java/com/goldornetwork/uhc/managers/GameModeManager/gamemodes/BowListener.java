package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;

public class BowListener implements Listener {

	//instances
	private TeamManager teamM;
	private TimerManager timerM;
	private Switcheroo switcherooM;
	private RewardingLongshots rewardingLongshotsM;
	
	//storage
	private boolean enableSwitcheroo;
	private boolean enableRewardingLongshots;
	
	public BowListener(TeamManager teamM, TimerManager timerM, Switcheroo switcherooM, RewardingLongshots rewardingLongshotsM) {
		this.teamM=teamM;
		this.timerM=timerM;
		this.switcherooM=switcherooM;
		this.rewardingLongshotsM=rewardingLongshotsM;
	}
	
	public void enableSwitcheroo(boolean val){
		this.enableSwitcheroo = val;
	}
	
	public void enableRewardingLongshots(boolean val){
		this.enableRewardingLongshots = val;
	}

	@EventHandler
	public void onPlayerShot(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player){
			if(e.getDamager() instanceof Arrow){
				Arrow arrow = (Arrow) e.getDamager();
				if(arrow.getShooter() instanceof Player){
					Player target = (Player) e.getEntity();
					Player shooter = (Player) arrow.getShooter();
					if(teamM.isPlayerInGame(target)&& teamM.isPlayerInGame(shooter) && timerM.hasCountDownEnded()){
						if(enableSwitcheroo){
							switcherooM.run(target, shooter);
						}
						if(enableRewardingLongshots){
							rewardingLongshotsM.run(target, shooter);
						}
					}
					
				}


			}
		}

	}
}
