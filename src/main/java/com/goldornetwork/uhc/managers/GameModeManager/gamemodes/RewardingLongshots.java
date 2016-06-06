package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class RewardingLongshots extends Gamemode implements Listener{
	
	//instances
	private TeamManager teamM;
	
	public RewardingLongshots(TeamManager teamM) {
		super("RewardingLongshots", "Players get higher rewards for longer shots!");
		this.teamM=teamM;
	}
	
	@Override
	public void onEnable() {}
	
	@Override
	public void onDisable() {}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(EntityDamageByEntityEvent e){
		if(State.getState().equals(State.INGAME)){
			if(e.getEntity() instanceof Player){
				if(e.getDamager() instanceof Arrow){
					Arrow arrow = (Arrow) e.getDamager();
					if(arrow.getShooter() instanceof Player){
						Player target = (Player) e.getEntity();
						Player shooter = (Player) arrow.getShooter();
						if(teamM.isPlayerInGame(target.getUniqueId()) && teamM.isPlayerInGame(shooter.getUniqueId())){
							run(target, shooter);
						}
					}
				}
			}
		}
	}
	
	private void run(Player target, Player shooter){
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
		MessageSender.send(ChatColor.GREEN, shooter, "You hit " + teamM.getColorOfPlayer(target.getUniqueId()) + target.getName() + ChatColor.GREEN +  " at a distance of " + ChatColor.GRAY + distance + ChatColor.GREEN + " blocks!");
		MessageSender.send(ChatColor.RED, target, "You got shot from a distance of " + ChatColor.GRAY + distance + ChatColor.RED + " blocks!");
	}

	
}
