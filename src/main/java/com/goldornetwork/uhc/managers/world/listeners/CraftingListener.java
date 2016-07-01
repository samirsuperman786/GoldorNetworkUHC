package com.goldornetwork.uhc.managers.world.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.State;

public class CraftingListener implements Listener{

	public CraftingListener(UHC plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
	public void on(PrepareItemCraftEvent e){
		if(State.getState().equals(State.INGAME) || State.getState().equals(State.SCATTER)){
			Material item = e.getRecipe().getResult().getType();
			
			if(item.equals(Material.GOLDEN_APPLE)){
				if(e.getRecipe().getResult().getDurability()==1){
					e.getInventory().setResult(new ItemStack(Material.AIR));
				}
			}
			else if(item.equals(Material.TNT)){
				e.getInventory().setResult(new ItemStack(Material.AIR));
			}
		}	
	}
	
	@EventHandler
	public void on(BlockPlaceEvent e){
		if(e.getBlock().getType().equals(Material.TNT)){
			e.setCancelled(true);
		}
	}
	
}
