package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;

public class Bows extends Gamemode implements Listener{

	private TeamManager teamM;
	public Bows(TeamManager teamM) {
		super("Bows", "Bows", "All melee, against players, is disabled, only bows are permitted!");
		this.teamM=teamM;
	}
	@EventHandler
	public void on(EntityDamageByEntityEvent e){
		if(State.getState().equals(State.INGAME)==false){
			return;
		}
		Entity damager = e.getDamager();
		Entity target = e.getEntity();
		//apparently the damager will return an arrow if its a bow, this is a shortcut
		if(!(damager instanceof Player) || !(target instanceof Player)){
			return;
		}
		Player pDamager = (Player) damager;
		Player pTarget = (Player) target;
		if(!(teamM.isPlayerInGame(pDamager.getUniqueId())) || !(teamM.isPlayerInGame(pTarget.getUniqueId()))){
			return;
		}
		e.setCancelled(true);
	}
}
