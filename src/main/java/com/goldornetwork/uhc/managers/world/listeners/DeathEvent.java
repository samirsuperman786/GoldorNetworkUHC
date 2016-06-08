package com.goldornetwork.uhc.managers.world.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.managers.world.events.GameEndEvent;

public class DeathEvent implements Listener {

	//instances
	private UHC plugin;
	private TeamManager teamM;
	private ScatterManager scatterM;
	private WorldManager worldM;

	public DeathEvent(UHC plugin, TeamManager teamM, ScatterManager scatterM, WorldManager worldM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin=plugin;
		this.teamM=teamM;
		this.scatterM=scatterM;
		this.worldM=worldM;
	}

	/**
	 * Handles the death of the player and removes them from an in-game state. 
	 * @param p - player who has just died
	 */
	public void playerDied(Player p){
		String team = teamM.getTeamOfPlayer(p.getUniqueId());
		if(teamM.isTeamsEnabled()){
			if(teamM.getOwnerOfTeam(team).equals(p.getUniqueId())){
				teamM.removePlayerFromOwner(p);
			}
			teamM.removePlayerFromTeam(p.getUniqueId());
			
			if(teamM.getPlayersOnATeam(team).isEmpty()){
				teamM.disbandTeam(team);
				if(teamM.getActiveTeams().size()==1){
					String winner = teamM.getActiveTeams().get(0);
					plugin.getServer().getPluginManager().callEvent(new GameEndEvent(teamM.getPlayersOnATeam(winner)));
				}
			}

		}


	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		p.setHealth(p.getMaxHealth());
		teamM.addPlayerToObservers(p);
		if(teamM.isPlayerInGame(p.getUniqueId())){
			if(State.getState().equals(State.INGAME) || State.getState().equals(State.SCATTER)){
				p.getWorld().strikeLightningEffect(p.getLocation());
				//So no race conditions happen
				new BukkitRunnable() {

					@Override
					public void run() {
						playerDied(p);
					}
				}.runTaskLater(plugin, 5L);

			}
			else{
				e.setDeathMessage(null);
			}
		}
		else{
			e.setDeathMessage(null);
		}
	}


}