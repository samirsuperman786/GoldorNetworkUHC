package com.goldornetwork.uhc.managers.world.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.Medic;
import com.goldornetwork.uhc.utils.MessageSender;

public class JoinEvent implements Listener{

	//instances
	private TeamManager teamM;
	private ScatterManager scatterM;

	public JoinEvent(UHC plugin, TeamManager teamM, ScatterManager scatterM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.teamM=teamM;
		this.scatterM=scatterM;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		
		if(teamM.isPlayerInGame(p)){
			if(teamM.isTeamsEnabled()){
				teamM.displayName(p, teamM.getTeamOfPlayer(p));
			}
		}
		else if(teamM.isPlayerAnObserver(p)){
			p.setDisplayName(ChatColor.AQUA + "[Observer] " + p.getName()+ ChatColor.WHITE);
		}
		
		if(State.getState().equals(State.OPEN)){
			if(teamM.isPlayerInGame(p)==false){
				if(teamM.isTeamsEnabled()){
					MessageSender.alertMessage(p, ChatColor.RED, "You are currently not on a team! Use /create");
				}
			}
		}
		if(State.getState().equals(State.OPEN) || State.getState().equals(State.NOT_RUNNING)){
			for(PotionEffect effect : p.getActivePotionEffects()){
				p.removePotionEffect(effect.getType());
			}
			p.setMaxHealth(20);
			p.setGameMode(GameMode.ADVENTURE);
			p.setDisplayName(p.getName());
			p.getInventory().clear();
			p.setLevel(0);
			p.setExp(0L);
			p.getInventory().setArmorContents(null);
			p.teleport(scatterM.getLobby().getSpawnLocation());
			Medic.heal(p);
		}
		

	}


}
