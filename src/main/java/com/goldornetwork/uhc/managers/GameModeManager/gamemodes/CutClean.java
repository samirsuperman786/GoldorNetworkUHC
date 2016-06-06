package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;

public class CutClean extends Gamemode implements Listener{

	private TeamManager teamM;
	private Random random = new Random();
	
	public CutClean(TeamManager teamM) {
		super("CutClean", "Pretty much any drop is smelted.");
		this.teamM=teamM;
	}
	
	@EventHandler
	public void on(BlockBreakEvent e){
		if(State.getState().equals(State.INGAME)==false){
			return;
		}
		Player target = e.getPlayer();
		if(teamM.isPlayerInGame(target.getUniqueId())==false){
			return;
		}
		Block block = e.getBlock();
		if(getSmeltedBlock(block)==null){
			return;
		}
		dropItem(target.getLocation(), new ItemStack(getSmeltedBlock(block)));
		target.giveExp(e.getExpToDrop());
		e.setCancelled(true);
		block.setType(Material.AIR);
	}
	
	@EventHandler
	public void on(EntityDeathEvent e){
		if(State.getState().equals(State.INGAME)==false){
			return;
		}
		else if(e.getEntity() instanceof Player){
			return;
		}
		else if(getSmelted(e.getEntity())!=null){
			e.getDrops().clear();
			for(ItemStack toDrop : getSmelted(e.getEntity())){
				e.getDrops().add(toDrop);
			}
		}
	}
	private Material getSmeltedBlock(Block block){
		Material toReturn = null;
		switch(block.getType()){
		case IRON_ORE: toReturn=Material.IRON_INGOT;
		break;
		case DIAMOND_ORE: toReturn= Material.DIAMOND;
		break;
		case GOLD_ORE: toReturn = Material.GOLD_INGOT;
		break;
		default:
			break;
		}
		return toReturn;
	}
	private List<ItemStack> getSmelted(Entity mob){
		List<Material> toAdd = new ArrayList<Material>();
		switch(mob.getType()){
		case CHICKEN: 
			toAdd.add(Material.COOKED_CHICKEN);
			toAdd.add(Material.FEATHER);
			break;
		case COW:
			toAdd.add(Material.COOKED_BEEF);
			toAdd.add(Material.LEATHER);
			break;
		case MUSHROOM_COW:
			toAdd.add(Material.LEATHER);
			toAdd.add(Material.COOKED_BEEF);
			break;
		case PIG:
			toAdd.add(Material.GRILLED_PORK);
			break;
		case RABBIT:
			toAdd.add(Material.COOKED_RABBIT);
			toAdd.add(Material.RABBIT_HIDE);
			break;
		case SHEEP:
			toAdd.add(Material.WOOL);
			toAdd.add(Material.COOKED_MUTTON);
			break;
		default:
			return null;
		}
		List<ItemStack> toReturn = new ArrayList<ItemStack>();
		for(Material toItem :toAdd){
			int amount = random.nextInt(3);
			toReturn.add(new ItemStack(toItem, amount));
			
		}
		return toReturn;
	}
	
	private void dropItem(Location loc, ItemStack item){
		loc.getWorld().dropItem(loc, item);
	}
	
}
