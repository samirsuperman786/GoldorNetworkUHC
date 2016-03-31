package com.goldornetwork.uhc.managers.ModifierManager.gamemodes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class Switcheroo {
	
	//instances
	private MessageSender ms = new MessageSender();
	private TeamManager teamM = TeamManager.getInstance();
	private BowListener bowListener = BowListener.getInstance();

	private Switcheroo(){}
	
	private static class InstanceHolder{
		private static final Switcheroo INSTANCE = new Switcheroo();
	}
	public static Switcheroo getInstance(){
		
		return InstanceHolder.INSTANCE;
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
		ms.send(ChatColor.RED, target, "You have switched places with " + teamM.getColorOfPlayer(shooter )+ shooter.getName());
		ms.send(ChatColor.RED, shooter, "You have switched places with " + teamM.getColorOfPlayer(target) + target.getName());
	}
	
	
}
