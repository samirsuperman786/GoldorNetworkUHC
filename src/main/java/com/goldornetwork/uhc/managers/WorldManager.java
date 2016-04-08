package com.goldornetwork.uhc.managers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.utils.Medic;

public class WorldManager implements Listener{

	private UHC plugin;
	private ScatterManager scatterM;
	
	public WorldManager(UHC plugin, ScatterManager scatterM) {
		this.plugin=plugin;
		this.scatterM=scatterM;
	}
	
	public void setup(){
		for(Player all : Bukkit.getOnlinePlayers()){
			all.setGameMode(GameMode.ADVENTURE);
			Medic.heal(all);
			all.teleport(scatterM.getLobby().getSpawnLocation());
		}
	}
	
	@EventHandler
	public void on(PlayerChangedWorldEvent e){
		if(e.getFrom().getPlayers().isEmpty()){
			Bukkit.unloadWorld(e.getFrom(), false);
		}
	}
}
