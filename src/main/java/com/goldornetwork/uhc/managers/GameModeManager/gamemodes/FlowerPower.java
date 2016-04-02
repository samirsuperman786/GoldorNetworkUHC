package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class FlowerPower extends Gamemode implements Listener{

	//instances
	private Random random = new Random();
	private TeamManager teamM;
	
	//storage
	private List<ItemStack> disalloweditems = new ArrayList<ItemStack>();
	
	public FlowerPower(TeamManager teamM) {
		super("FlowerPower", "When you break a flower, a random amount of a random item drops!");
		this.teamM= teamM;
	}
	@Override
	public void onEnable() {
		addDisallowedItems(7, 11);
		addDisallowedItems(new ItemStack(18));//leaf
		addDisallowedItems(new ItemStack(26));//bed
		addDisallowedItems(new ItemStack(27));//decay leave
		addDisallowedItems(new ItemStack(51));//fire
		addDisallowedItems(new ItemStack(52));//spawner
		addDisallowedItems(new ItemStack(55));//no idea
		addDisallowedItems(new ItemStack(59));//no idea
		addDisallowedItems(new ItemStack(62)); //lit furnace
		addDisallowedItems(new ItemStack(63));//no idea
		addDisallowedItems(new ItemStack(64));//half door
		addDisallowedItems(new ItemStack(71));//half door
		addDisallowedItems(new ItemStack(68));//no idea
		addDisallowedItems(new ItemStack(90));//portal block
		addDisallowedItems(92, 94);
		addDisallowedItems(96, 255);
	}
	@Override
	public void onDisable() {}
	
	public void addDisallowedItems(ItemStack item){
		this.disalloweditems.add(item);
	}
	public void addDisallowedItems(int minBound, int maxBound){ //apparently this is the only way to disallow large quantities of items
		for(int low= minBound; low<=maxBound; low++){
			this.disalloweditems.add(new ItemStack(low));
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(BlockBreakEvent e){
		if(State.getState().equals(State.INGAME)){
			if(e instanceof Player){
				Player p = e.getPlayer();
				if(teamM.isPlayerInGame(p)){
					if(e.getBlock().getType().equals(Material.YELLOW_FLOWER) || e.getBlock().getType().equals(Material.RED_ROSE)){
						run(p, e);
					}
				}
			}
		}
	}
	public void run(Player p, BlockBreakEvent e){
		boolean foundItem=false;
		while(foundItem==false){
			int item = random.nextInt(356) + 1;
			if(disalloweditems.contains(new ItemStack(item))==false && new ItemStack(item).getItemMeta()!=null){
				int amount = random.nextInt(64);
				MessageSender.send(ChatColor.GREEN, p, "ID " + item);
				p.getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(item, amount));
				foundItem=true;
			}
		}
	}
}
