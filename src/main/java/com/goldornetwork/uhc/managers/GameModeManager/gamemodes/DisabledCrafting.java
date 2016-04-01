package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.TimerManager;

public class DisabledCrafting implements Listener{

	//instances
	private TimerManager timerM;
	
	//gamemodes
	private boolean enableGoneFishing;
	
	public DisabledCrafting(TimerManager timerM) {
		this.timerM=timerM;
	}
	
	public void enableGoneFishing(boolean val){
		this.enableGoneFishing = val;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onCraft(PrepareItemCraftEvent e){
		if(timerM.hasCountDownEnded()){
			Material item = e.getRecipe().getResult().getType();
			if(enableGoneFishing){
				if(item.equals(Material.ENCHANTMENT_TABLE)){
					e.getInventory().setResult(new ItemStack(Material.AIR));
				}
			}
		}
		
		
	}
	
	
	
}
