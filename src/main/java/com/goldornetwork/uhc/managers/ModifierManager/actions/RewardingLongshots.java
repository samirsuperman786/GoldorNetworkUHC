package com.goldornetwork.uhc.managers.ModifierManager.actions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class RewardingLongshots {
	
	private MessageSender ms = new MessageSender();
	private TeamManager teamM = TeamManager.getInstance();

	public void run(Player target, Player shooter){
		Location shooterLocation = shooter.getLocation();
		Location targetLocation = target.getLocation();
		int distance = (int) shooterLocation.distance(targetLocation);
		
		if(distance > 60 && distance < 100){
			shooter.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,1));
			send(shooter, target, distance);
		}
		else if(distance>= 100 && distance < 160){
			shooter.getInventory().addItem(new ItemStack(Material.DIAMOND,1));
			send(shooter, target, distance);
		}
		else if(distance>= 160){
			shooter.getInventory().addItem(new ItemStack(Material.DIAMOND,2));
			send(shooter, target, distance);
		}
	}
	
	private void send(Player shooter, Player target, int distance){
		ms.send(ChatColor.GREEN, shooter, "You hit " + teamM.getColorOfPlayer(target) + target.getName() + ChatColor.GREEN +  " at a distance of " + ChatColor.GRAY + distance + ChatColor.GREEN + " blocks!");
		ms.send(ChatColor.RED, target, "You got shot from a distance of " + ChatColor.GRAY + distance + ChatColor.RED + " blocks!");
	}

	
}
