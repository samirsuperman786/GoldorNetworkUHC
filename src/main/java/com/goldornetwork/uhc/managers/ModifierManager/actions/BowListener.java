package com.goldornetwork.uhc.managers.ModifierManager.actions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class BowListener implements Listener {

	//instances
	private static BowListener instance = new BowListener();
	private MessageSender ms = new MessageSender();
	private TeamManager teamM = TeamManager.getInstance();
	private TimerManager timerM = TimerManager.getInstance();
	private Switcheroo switcherooM = new Switcheroo();
	private RewardingLongshots rewardingLongshotsM = new RewardingLongshots();
	
	//storage
	private boolean enableSwitcheroo;
	private boolean enableRewardingLongshots;
	
	
	public static BowListener getInstance(){
		return instance;
	}
	
	public void setup(){
		enableSwitcheroo=false;
		enableRewardingLongshots=false;
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
