package com.goldornetwork.uhc.managers.ModifierManager.actions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.ScatterManager;

public class BlockRush {

	private static BlockRush instance = new BlockRush();
	private ScatterManager scatterM = ScatterManager.getInstance();
	
	private List<Material> firstBlocksMined = new ArrayList<Material>();
	
	
	public static BlockRush getInstance(){
		return instance;
	}
	public void setup(){
		firstBlocksMined.clear();
	}
	public void run(Player p, BlockBreakEvent e){
		if(firstBlocksMined.contains(e.getBlock().getType())==false){
			p.getInventory().addItem(new ItemStack(Material.DIAMOND));
			scatterM.getUHCWorld().playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 3.0F, .5F);
			firstBlocksMined.add(e.getBlock().getType());
		}
	}
	
}
