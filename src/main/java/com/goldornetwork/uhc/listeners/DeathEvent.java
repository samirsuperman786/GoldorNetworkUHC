package com.goldornetwork.uhc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class DeathEvent implements Listener {

	//instances
	private TeamManager teamM;
	private ScatterManager scatterM;
	
	public DeathEvent(UHC plugin, TeamManager teamM, ScatterManager scatterM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.teamM=teamM;
		this.scatterM=scatterM;
	}

	/**
	 * Handles the death of the player and removes them from an in-game state. 
	 * @param p - player who has just died
	 */
	public void playerDied(Player p){
		if(teamM.isTeamsEnabled()){
			if(teamM.getOwnerOfTeam(teamM.getTeamOfPlayer(p)).equals(p.getUniqueId())){
				teamM.removePlayerFromOwner(p);
			}
			teamM.removePlayerFromTeam(p);
		}
		else if(teamM.isFFAEnabled()){
			teamM.removePlayerFromFFA(p);
			if(teamM.getPlayersInGame().size()<=1){
				MessageSender.broadcast("Game over!");
			}
		}
		p.setHealth(p.getMaxHealth());
		teamM.addPlayerToObservers(p);
		p.teleport(scatterM.getCenter());
	}

	@EventHandler
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
