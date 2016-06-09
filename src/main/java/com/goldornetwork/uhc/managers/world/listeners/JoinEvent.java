package com.goldornetwork.uhc.managers.world.listeners;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.utils.Medic;
import com.goldornetwork.uhc.utils.MessageSender;

public class JoinEvent implements Listener{

	//instances
	private TeamManager teamM;
	private WorldManager worldM;
	private Random random = new Random();
	
	public JoinEvent(UHC plugin, TeamManager teamM, WorldManager worldM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.teamM=teamM;
		this.worldM=worldM;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		
		if(teamM.isPlayerInGame(p.getUniqueId())){
			if(teamM.isTeamsEnabled()){
				teamM.displayName(p, teamM.getTeamOfPlayer(p.getUniqueId()));
			}
		}
		else if(teamM.isPlayerAnObserver(p.getUniqueId())){
			p.setDisplayName(ChatColor.AQUA + "[Observer] " + p.getName()+ ChatColor.WHITE);
		}
		
		if(State.getState().equals(State.OPEN)){
			if(teamM.isPlayerInGame(p.getUniqueId())==false){
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
			Location lobby = worldM.getLobby().getSpawnLocation();
			Location toTeleport = lobby.add(lobby.getBlockX() + random.nextInt(1), lobby.getBlockY(), lobby.getBlockZ() + random.nextInt(1));
			toTeleport.setYaw(90);
			p.teleport(toTeleport);
			Medic.heal(p);
		}
		

	}


}
