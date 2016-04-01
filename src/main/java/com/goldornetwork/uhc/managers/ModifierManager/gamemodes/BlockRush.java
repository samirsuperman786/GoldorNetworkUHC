package com.goldornetwork.uhc.managers.ModifierManager.gamemodes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.listeners.BreakEvent;
import com.goldornetwork.uhc.managers.ScatterManager;

public class BlockRush {

	//instances
	private ScatterManager scatterM;
	private BreakEvent breakE;
	//storage
	private List<Material> firstBlocksMined = new ArrayList<Material>();
	
	public BlockRush(ScatterManager scatterM, BreakEvent breakE) {
		this.scatterM=scatterM;
		this.breakE=breakE;
	}
	
	public void setup(){
		firstBlocksMined.clear();
		breakE.enableBlockRush(false);
	}
	public void enableBlockRush(boolean val){
		breakE.enableBlockRush(val);
	}
	public void run(Player p, BlockBreakEvent e){
		if(firstBlocksMined.contains(e.getBlock().getType())==false){
			p.getInventory().addItem(new ItemStack(Material.DIAMOND));
			scatterM.getUHCWorld().playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 3.0F, .5F);
			firstBlocksMined.add(e.getBlock().getType());
		}
	}
	
}
