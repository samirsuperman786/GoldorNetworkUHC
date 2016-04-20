package com.goldornetwork.uhc.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
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
			else if(teamM.isFFAEnabled()){
				teamM.displayName(p, "FFA");
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
				else if(teamM.isFFAEnabled()){
					MessageSender.alertMessage(p, ChatColor.RED, "You are not in the FFA yet! Use /join");
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
		if(State.getState().equals(State.INGAME)|| State.getState().equals(State.SCATTER)){
			if(teamM.isPlayerInGame(e.getPlayer())){
					if(scatterM.getLateScatters().contains(p.getUniqueId())){
						scatterM.handleLateScatter(p);
						scatterM.removePlayerFromLateScatters(p);
						MessageSender.send(ChatColor.GREEN, p, "You have been late scattered!");
					}
				
					if(scatterM.getLateScatters().contains(p.getUniqueId())){
						scatterM.handleLateScatter(p);
						scatterM.removePlayerFromLateScatters(p);
						MessageSender.send(ChatColor.GREEN, p, "You have been late scattered to your teams spawn!");
					}
				
			}
			else if(teamM.isPlayerInGame(e.getPlayer())==false){
				if(p.getWorld().equals(scatterM.getUHCWorld())==false){
					p.teleport(scatterM.getUHCWorld().getSpawnLocation());
				}
				if(teamM.isPlayerAnObserver(p)==false){
					teamM.addPlayerToObservers(p);
				}
				else{
					MessageSender.send(ChatColor.AQUA, p, "You are now spectating the game");
				}
				
			}
		}

	}


}
