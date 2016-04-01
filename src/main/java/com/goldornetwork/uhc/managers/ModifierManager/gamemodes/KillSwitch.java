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

	private TeamManager teamM;
	private DeathEvent deathE;
	private JoinEvent joinE;
	private TimerManager timerM;
	
	public KillSwitch(TeamManager teamM, DeathEvent deathE, JoinEvent joinE, TimerManager timerM) {
		this.teamM= teamM;
		this.deathE=deathE;
		this.joinE=joinE;
		this.timerM=timerM;
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
		MessageSender.alertMessage(killer, ChatColor.GOLD, "You have switched inventories with " + teamM.getColorOfPlayer(target) + target.getName());
		deathE.playerDied(target, e);
	}
	
}
