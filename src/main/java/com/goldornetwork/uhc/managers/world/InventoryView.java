package com.goldornetwork.uhc.managers.world;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;

public class InventoryView implements Listener{

	
	private UHC plugin;
	private TeamManager teamM;
	
	public InventoryView(UHC plugin, TeamManager teamM) {
		this.plugin=plugin;
		this.teamM=teamM;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void on(PlayerInteractAtEntityEvent e){
		Player clicker = e.getPlayer();
		if(teamM.isPlayerAnObserver(clicker.getUniqueId())){
			if(e.getRightClicked() instanceof Player){
				Player target = (Player) e.getRightClicked();
				if(teamM.isPlayerInGame(target.getUniqueId())){
					showInventory(clicker, target);
				}
			}
		}
	}
	
	private void showInventory(Player clicker, Player target){
		Inventory targetInv = Bukkit.createInventory(target, 36, teamM.getColorOfPlayer(target.getUniqueId()) + target.getName());
		targetInv.setContents(target.getInventory().getContents());
		clicker.openInventory(targetInv);
	}
	
	
}
