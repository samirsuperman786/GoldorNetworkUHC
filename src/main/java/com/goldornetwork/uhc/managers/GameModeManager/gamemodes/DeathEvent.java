package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class DeathEvent implements Listener {

	//instances
	private TeamManager teamM;
	private TimerManager timerM;
	private ScatterManager scatterM;
	private LiveWithRegret liveWithRegretM;
	private KillSwitch killSwitchM;
	//gamemodes
	private boolean enableLiveWithRegret;
	private boolean enableKillSwitch;
	


	public DeathEvent(TeamManager teamM, TimerManager timerM, ScatterManager scatterM, LiveWithRegret liveWithRegretM, KillSwitch killSwitchM) {
		this.teamM=teamM;
		this.timerM=timerM;
		this.scatterM=scatterM;
		this.liveWithRegretM=liveWithRegretM;
		this.killSwitchM=killSwitchM;
	}

	public void enableLiveWithRegret(boolean val){
		this.enableLiveWithRegret=val;
	}
	public void enableKillSwitch(boolean val){
		this.enableKillSwitch = val;
	}

	public void playerDied(Player p, Event e){
		p.setHealth(p.getMaxHealth());
		teamM.addPlayerToObservers(p);
		if(teamM.isTeamsEnabled()){
			if(teamM.getOwnerOfTeam(teamM.getTeamOfPlayer(p)).equals(p.getUniqueId())){
				teamM.removePlayerFromOwner(p);
			}
			deathMsg(p, e);
			teamM.removePlayerFromTeam(p);
		}
		else if(teamM.isFFAEnabled()){
			deathMsg(p, e);
			teamM.removePlayerFromFFA(p);
		}

	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		if(teamM.isPlayerInGame(p)==true && timerM.hasCountDownEnded()){
			p.getWorld().strikeLightningEffect(p.getLocation());
			if(enableLiveWithRegret){
				if(timerM.isPVPEnabled()==false){
					liveWithRegretM.run(p, e);
					
					if(teamM.isTeamsEnabled()){
						scatterM.lateScatterAPlayerInATeam(teamM.getTeamOfPlayer(p), p);
					}
					else if(teamM.isFFAEnabled()){
						scatterM.lateScatterAPlayerInFFA(p);					}
				}
				else{
					playerDied(p, e);
				}

			}
			//TODO fix conflicts with both gamemodes
			if(enableKillSwitch){
				if(p.getKiller() instanceof Player){
					killSwitchM.run(p, p.getKiller(), e);
				}
				else{
					playerDied(p, e);
				}
			}
			
		}
		else{
			e.setDeathMessage(null);
		}
	}
	//TODO fix this stuff
	private void deathMsg(Player target, Event e){
		PlayerDeathEvent event = (PlayerDeathEvent) e;
		if(event.getEntity() instanceof Zombie){
			event.setDeathMessage(target.getName() + " died by a creeper" );
		}
		else{
			event.setDeathMessage(target.getName() + " died by " + event.getEntity().getKiller().getName());

		}

	}


}
