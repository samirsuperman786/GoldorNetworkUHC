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

	private static BowListener instance = new BowListener();
	private MessageSender ms = new MessageSender();
	private TeamManager teamM = new TeamManager();
	private TimerManager timerM = TimerManager.getInstance();
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
						Location targetLocation = target.getLocation();
						Location shooterLocation = shooter.getLocation();
						int distance = (int) shooterLocation.distance(targetLocation);
						if(enableSwitcheroo){
							target.teleport(shooterLocation);
							shooter.teleport(targetLocation);
							if(teamM.isFFAEnabled()){
								ms.send(ChatColor.RED, target, "You have switched places with " + ChatColor.YELLOW + shooter.getName());
								ms.send(ChatColor.RED, shooter, "You have switched places with " + ChatColor.YELLOW + target.getName());
							}
							else if(teamM.isTeamsEnabled()){
								ms.send(ChatColor.RED, target, "You have switched places with " + teamM.getTeamOfPlayer(shooter)+ shooter.getName());
								ms.send(ChatColor.RED, shooter, "You have switched places with " + teamM.getTeamOfPlayer(target)+ target.getName());
							}
						}
						if(enableRewardingLongshots){
							if(distance > 60 && distance < 100){
								shooter.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,1));
								ms.send(ChatColor.GREEN, shooter, "You hit " + target.getName() + " at a distance of " + ChatColor.GRAY + distance + ChatColor.GREEN + " blocks!");
								ms.send(ChatColor.RED, target, "You got shot from a distance of " + ChatColor.GRAY + distance + ChatColor.RED + " blocks!");
							}
							else if(distance>= 100 && distance < 160){
								shooter.getInventory().addItem(new ItemStack(Material.DIAMOND,1));
								ms.send(ChatColor.GREEN, shooter, "You hit " + target.getName() + " at a distance of " + ChatColor.GRAY + distance + ChatColor.GREEN + " blocks!");
								ms.send(ChatColor.RED, target, "You got shot from a distance of " + ChatColor.GRAY + distance + ChatColor.RED + " blocks!");
							}
							else if(distance>= 160){
								shooter.getInventory().addItem(new ItemStack(Material.DIAMOND,2));
								ms.send(ChatColor.GREEN, shooter, "You hit " + target.getName() + " at a distance of " + ChatColor.GRAY + distance + ChatColor.GREEN + " blocks!");
								ms.send(ChatColor.RED, target, "You got shot from a distance of " + ChatColor.GRAY + distance + ChatColor.RED + " blocks!");
							}
						}
					}
					
				}


			}
		}

	}
}
