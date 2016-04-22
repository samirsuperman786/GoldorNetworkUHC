package com.goldornetwork.uhc.managers.world;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.utils.Medic;
import com.goldornetwork.uhc.utils.MessageSender;

public class WorldManager implements Listener{

	private UHC plugin;
	private ScatterManager scatterM;
	
	public WorldManager(UHC plugin, ScatterManager scatterM) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin=plugin;
		this.scatterM=scatterM;
	}
	
	public void setup(){
		for(Player all : Bukkit.getOnlinePlayers()){
			all.setGameMode(GameMode.ADVENTURE);
			all.setMaxHealth(20);
			all.setLevel(0);
			all.setExp(0L);
			Medic.heal(all);
			for(PotionEffect effect : all.getActivePotionEffects()){
				all.removePotionEffect(effect.getType());
			}
			all.setDisplayName(all.getName());
			all.getInventory().clear();
			all.getInventory().setArmorContents(null);
			all.teleport(scatterM.getLobby().getSpawnLocation());
			
		}
	}
	
	public void endGame(List<UUID> winners){
		MessageSender.broadcast("Game has ended!");
		MessageSender.broadcast("Winners are: ");
		for(UUID u : winners){
			MessageSender.broadcast(Bukkit.getServer().getOfflinePlayer(u).getName());
			if(Bukkit.getOfflinePlayer(u).isOnline()){
				Player target = Bukkit.getServer().getPlayer(u);
				target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4));
			}
		}
	}
	
	public void endGame(){
		MessageSender.broadcast("Game has ended!");
		MessageSender.broadcast("No one has won!");
		
	}
	@EventHandler
	public void on(PlayerChangedWorldEvent e){
		if(e.getFrom().getPlayers().isEmpty()){
			Bukkit.unloadWorld(e.getFrom(), false);
		}
	}
}
