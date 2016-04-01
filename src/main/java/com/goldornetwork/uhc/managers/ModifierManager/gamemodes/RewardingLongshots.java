package com.goldornetwork.uhc.managers.ModifierManager.gamemodes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class RewardingLongshots {
	
	//instances
	private TeamManager teamM;
	private BowListener bowListener;
	
	public RewardingLongshots(TeamManager teamM, BowListener bowListener) {
		this.teamM=teamM;
		this.bowListener=bowListener;
	}
	
	public void setup(){
		bowListener.enableRewardingLongshots(false);
	}
	public void enableRewardingLongshots(boolean val){
		bowListener.enableRewardingLongshots(val);
	}
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
		MessageSender.send(ChatColor.GREEN, shooter, "You hit " + teamM.getColorOfPlayer(target) + target.getName() + ChatColor.GREEN +  " at a distance of " + ChatColor.GRAY + distance + ChatColor.GREEN + " blocks!");
		MessageSender.send(ChatColor.RED, target, "You got shot from a distance of " + ChatColor.GRAY + distance + ChatColor.RED + " blocks!");
	}

	
}
