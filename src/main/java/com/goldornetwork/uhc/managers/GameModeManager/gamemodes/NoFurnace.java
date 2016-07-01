package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.world.WorldManager;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;

public class NoFurnace extends Gamemode implements Listener{

	private WorldManager worldM;
	
	public NoFurnace(WorldManager worldM) {
		super("No Furnace", "NoFurnace", "Furnaces cannot be crafted or placed. There will be one furnace at 0, 200, 0.");
		this.worldM=worldM;
	}

	@EventHandler
	public void on(GameStartEvent e){
		Location loc = worldM.getCenter();
		loc.setY(200);
		loc.getBlock().setType(Material.FURNACE);
	}
	
	@EventHandler
	public void on(BlockPlaceEvent e){
		if(e.getBlock().getType().equals(Material.FURNACE)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void on(BlockBreakEvent e){
		if(e.getBlock().getType().equals(Material.FURNACE) || e.getBlock().getType().equals(Material.BURNING_FURNACE)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void on(PrepareItemCraftEvent e){
		if(e.getRecipe().getResult().getType().equals(Material.FURNACE)){
			e.getInventory().setResult(new ItemStack(Material.AIR));
		}
	}
}
