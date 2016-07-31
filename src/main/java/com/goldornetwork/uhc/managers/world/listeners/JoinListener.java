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
import com.goldornetwork.uhc.managers.world.customevents.UHCJoinEvent;
import com.goldornetwork.uhc.utils.CoordXZ;
import com.goldornetwork.uhc.utils.LocationUtils;
import com.goldornetwork.uhc.utils.Medic;
import com.goldornetwork.uhc.utils.MessageSender;

public class JoinListener implements Listener{

	
	private UHC plugin;
	private TeamManager teamM;
	private WorldManager worldM;
	private Random random = new Random();

	
	public JoinListener(UHC plugin, TeamManager teamM, WorldManager worldM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.teamM=teamM;
		this.worldM=worldM;
		this.plugin=plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerJoinEvent e){
		Player p = e.getPlayer();

		if(State.getState().equals(State.OPEN)){
			if(teamM.isPlayerInGame(p.getUniqueId())==false){
				if(teamM.isTeamsEnabled()){
					MessageSender.alertMessage(p, ChatColor.RED + "You are currently not on a team. Use /create");
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
			CoordXZ variation = LocationUtils.locationInRadius(2);
			Location toTeleport = lobby.clone().add(variation.x, 0, variation.z);
			p.teleport(toTeleport);
			Medic.heal(p);
		}
		if(State.getState().equals(State.INGAME)){
			if(teamM.isPlayerInGame(p.getUniqueId())){
				plugin.getServer().getPluginManager().callEvent(new UHCJoinEvent(p));
			}
		}
	}

	
}
