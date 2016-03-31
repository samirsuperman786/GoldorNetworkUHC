package com.goldornetwork.uhc.managers.ModifierManager.gamemodes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.listeners.JoinEvent;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class KillSwitch {

	private static volatile KillSwitch instance;
	private MessageSender ms = new MessageSender();
	private TeamManager teamM = TeamManager.getInstance();
	private DeathEvent deathE = DeathEvent.getInstance();
	private JoinEvent joinE = JoinEvent.getInstance();
	private TimerManager timerM = TimerManager.getInstance();
	
	private KillSwitch(){}
	
	private static class InstanceHolder{
		private static final KillSwitch INSTANCE = new KillSwitch();
	}
	public static KillSwitch getInstance(){
		
		return InstanceHolder.INSTANCE;
	}
	public void setup(){
		deathE.enableKillSwitch(false);
		joinE.enableTheHobbit(false);
		timerM.enableTheHobbit(false);
	}
	public void enableKillSwitch(boolean val){
		deathE.enableKillSwitch(val);
		joinE.enableTheHobbit(val);
		timerM.enableTheHobbit(val);
	}
	public void run(Player target, Player killer, PlayerDeathEvent e){
		ItemStack[] targetInventory = target.getInventory().getContents();
		Inventory killerInventory = killer.getInventory();
		killerInventory.clear();
		killerInventory.setContents(targetInventory);
		e.getDrops().clear();
		ms.alertMessage(killer, ChatColor.GOLD, "You have switched inventories with " + teamM.getColorOfPlayer(target) + target.getName());
		deathE.playerDied(target, e);
	}
	
}
