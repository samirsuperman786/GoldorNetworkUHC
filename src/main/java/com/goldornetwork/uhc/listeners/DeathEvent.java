package com.goldornetwork.uhc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;

public class DeathEvent implements Listener {

	//instances
	private TeamManager teamM;
	
	public DeathEvent(UHC plugin, TeamManager teamM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.teamM=teamM;
	}

	public void playerDied(Player p){
		p.setHealth(p.getMaxHealth());
		teamM.addPlayerToObservers(p);
		if(teamM.isTeamsEnabled()){
			if(teamM.getOwnerOfTeam(teamM.getTeamOfPlayer(p)).equals(p.getUniqueId())){
				teamM.removePlayerFromOwner(p);
			}
			teamM.removePlayerFromTeam(p);
		}
		else if(teamM.isFFAEnabled()){
			teamM.removePlayerFromFFA(p);
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		if(teamM.isPlayerInGame(p)==true && State.getState().equals(State.INGAME)){
			p.getWorld().strikeLightningEffect(p.getLocation());
			playerDied(p);
		}
		else{
			e.setDeathMessage(null);
		}
	}


}
