package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class Switcheroo {
	
	//instances
	private TeamManager teamM;
	private BowListener bowListener;

	public Switcheroo(TeamManager teamM, BowListener bowListener) {
		this.teamM=teamM;
		this.bowListener=bowListener;
	}
	
	public void setup(){
		bowListener.enableSwitcheroo(false);
	}
	public void enableSwitcheroo(boolean val){
		bowListener.enableSwitcheroo(val);
	}
	public void run(Player target, Player shooter){
		Location targetLocation = target.getLocation();
		Location shooterLocation = shooter.getLocation();
		target.teleport(shooterLocation);
		shooter.teleport(targetLocation);
		MessageSender.send(ChatColor.RED, target, "You have switched places with " + teamM.getColorOfPlayer(shooter )+ shooter.getName());
		MessageSender.send(ChatColor.RED, shooter, "You have switched places with " + teamM.getColorOfPlayer(target) + target.getName());
	}
	
	
}
