package com.goldornetwork.uhc.managers.ModifierManager.gamemodes;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;

public class BowListener implements Listener {

	//instances
	private TeamManager teamM = TeamManager.getInstance();
	private TimerManager timerM = TimerManager.getInstance();
	private Switcheroo switcherooM = Switcheroo.getInstance();
	private RewardingLongshots rewardingLongshotsM = RewardingLongshots.getInstance();
	
	//storage
	private boolean enableSwitcheroo;
	private boolean enableRewardingLongshots;
	
	private BowListener(){}
	
	private static class InstanceHolder{
		private static final BowListener INSTANCE = new BowListener();
	}
	public static BowListener getInstance(){
		return InstanceHolder.INSTANCE;
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
