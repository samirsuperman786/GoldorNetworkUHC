package com.goldornetwork.uhc.managers.ModifierManager.actions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class Switcheroo {
	
	private MessageSender ms = new MessageSender();
	private TeamManager teamM = TeamManager.getInstance();
	
	public void run(Player target, Player shooter){
		Location targetLocation = target.getLocation();
		Location shooterLocation = shooter.getLocation();
		target.teleport(shooterLocation);
		shooter.teleport(targetLocation);
		ms.send(ChatColor.RED, target, "You have switched places with " + teamM.getColorOfPlayer(shooter )+ shooter.getName());
		ms.send(ChatColor.RED, shooter, "You have switched places with " + teamM.getColorOfPlayer(target) + target.getName());
	}
	
	
}
