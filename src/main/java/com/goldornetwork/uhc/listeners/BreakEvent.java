package com.goldornetwork.uhc.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class BreakEvent implements Listener{
	private static BreakEvent instance = new BreakEvent();
	private TeamManager teamM = TeamManager.getInstance();
	private MessageSender ms = new MessageSender();
	private Map<UUID, Integer> amountOfDiamondsMined = new HashMap<UUID, Integer>();
	private List<ItemStack> disalloweditems = new ArrayList<ItemStack>();
	private Random random = new Random();
	
	
	private int diamondThreshold = 20;
	//TODO make below true
	private boolean enableFlowerPower=false;
	
	public static BreakEvent getInstance(){
		return instance;
	}
	public void setup(){
		amountOfDiamondsMined.clear();
		//TODO add more restrictions ugh
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
	
	public void addDisallowedItems(ItemStack item){
		this.disalloweditems.add(item);
	}
	public void addDisallowedItems(int minBound, int maxBound){ //apparently this is the only way to disallow large quantities of items
		for(int low= minBound; low<=maxBound; low++){
			this.disalloweditems.add(new ItemStack(low));
		}
		
	}
	public void enableFlowerPower(boolean val){
		this.enableFlowerPower=val;
	}
	
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void OnPlayerBreakEvent(BlockBreakEvent e){
		Player p = e.getPlayer();
		//TODO make below true
		if(teamM.isPlayerInGame(p)){
			if(e.getBlock().getType().equals(Material.DIAMOND_ORE)){
				
					if(amountOfDiamondsMined.containsKey(p.getUniqueId())==false){
						amountOfDiamondsMined.put(p.getUniqueId(), 1);
					}
					else if(amountOfDiamondsMined.containsKey(p.getUniqueId())){
						amountOfDiamondsMined.put(p.getUniqueId(), amountOfDiamondsMined.get(p.getUniqueId()) + 1);
						if(amountOfDiamondsMined.get(p.getUniqueId())>=diamondThreshold && amountOfDiamondsMined.get(p.getUniqueId()) % 10 == 0){
							ms.sendToOPS(p.getName() + " has mined " + ChatColor.GRAY + amountOfDiamondsMined.get(p.getUniqueId()) + " diamonds");
						}
					}
		
			}
			if(enableFlowerPower){
				if(e.getBlock().getType().equals(Material.YELLOW_FLOWER) || e.getBlock().getType().equals(Material.RED_ROSE)){
					boolean foundItem=false;
					while(foundItem==false){
						int item = random.nextInt(356) + 1;
						if(disalloweditems.contains(new ItemStack(item))==false && new ItemStack(item).getItemMeta()!=null && new ItemStack(item).getData()!=null && new ItemStack(item).getType()!=null){
							int amount = random.nextInt(64);
							ms.send(ChatColor.GREEN, p, "ID " + item);
							p.getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(item, amount));
							//p.getInventory().addItem(new ItemStack(item, amount));
							foundItem=true;
						}
					}
					
				}
			}
			
		}
		
		
		
		
		
	}
	
	
}
