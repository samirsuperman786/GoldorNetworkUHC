package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;

public class BlockRush extends Gamemode implements Listener{

	//instances
	private ScatterManager scatterM;
	//storage
	private List<Material> firstBlocksMined = new ArrayList<Material>();
	
	public BlockRush(ScatterManager scatterM) {
		super("BlockRush", "First one to mine a unique block gets a diamond!");
		this.scatterM=scatterM;
	}
	@Override
	public void onEnable() {
		firstBlocksMined.clear();
	}
	@Override
	public void onDisable() {
		firstBlocksMined.clear();
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(BlockBreakEvent e){
		if(State.getState().equals(State.INGAME)){
			if(e instanceof Player){
				Player p = e.getPlayer();
				if(firstBlocksMined.contains(e.getBlock().getType())==false){
					run(p, e);
					
				}
			}
		}
	}
	
	private void run(Player p, BlockBreakEvent e){
		p.getInventory().addItem(new ItemStack(Material.DIAMOND));
		scatterM.getUHCWorld().playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 3.0F, .5F);
		firstBlocksMined.add(e.getBlock().getType());
	}
	
}
