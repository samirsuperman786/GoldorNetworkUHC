package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class Switcheroo extends Gamemode implements Listener{

	//instances
	private TeamManager teamM;

	public Switcheroo(TeamManager teamM) {
		super("Switcheroo", "Switcheroo", "When a player shoots another player, they switch places");
		this.teamM=teamM;
	}
	@Override
	public void onEnable() {}

	@Override
	public void onDisable() {}

	@EventHandler
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
		Location targetLocation = target.getLocation();
		Location shooterLocation = shooter.getLocation();
		target.teleport(shooterLocation);
		shooter.teleport(targetLocation);
		MessageSender.send(ChatColor.RED, target, "You have switched places with " + teamM.getColorOfPlayer(shooter.getUniqueId())+ shooter.getName());
		MessageSender.send(ChatColor.RED, shooter, "You have switched places with " + teamM.getColorOfPlayer(target.getUniqueId()) + target.getName());
	}


}
