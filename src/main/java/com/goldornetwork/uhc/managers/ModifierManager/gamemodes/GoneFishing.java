package com.goldornetwork.uhc.managers.ModifierManager.gamemodes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.goldornetwork.uhc.listeners.BreakEvent;
import com.goldornetwork.uhc.listeners.JoinEvent;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;

public class GoneFishing {

	private TeamManager teamM = TeamManager.getInstance();
	private DisabledCrafting disabledC = DisabledCrafting.getInstance();
	private JoinEvent joinE = JoinEvent.getInstance();
	private TimerManager timerM = TimerManager.getInstance();
	private FlowerPower flowerPowerM = FlowerPower.getInstance();
	private List <UUID> lateGoneFishing = new ArrayList<UUID>();
	
	private GoneFishing(){}
	
	private static class InstanceHolder{
		private static final GoneFishing INSTANCE = new GoneFishing();
	}
	public static GoneFishing getInstance(){
		return InstanceHolder.INSTANCE;
	}
	public void setup(){
		lateGoneFishing.clear();
		disabledC.enableGoneFishing(false);
		joinE.enableGoneFishing(false);
		timerM.enableGoneFishing(false);
	}
	public void enableGoneFishing(boolean val){
		disabledC.enableGoneFishing(val);
		joinE.enableGoneFishing(val);
		timerM.enableGoneFishing(val);
		flowerPowerM.addDisallowedItems(new ItemStack(125));//enchant table
	}
	
	public List<UUID> getLateGoneFishing(){
		return lateGoneFishing;
	}
	public void removeAPlayerFromLateGoneFishing(Player p){
		lateGoneFishing.remove(p);
	}
	

	public void giveAPlayerGoneFishingItems(Player p){
		ItemStack given = new ItemStack(Material.FISHING_ROD, 1);
		ItemMeta im = given.getItemMeta();
		im.addEnchant(Enchantment.LUCK, 250, true);
		im.addEnchant(Enchantment.LURE, 250, true);
		im.addEnchant(Enchantment.DURABILITY, 150, true);
		given.setItemMeta(im);
		p.getInventory().addItem(new ItemStack(Material.ANVIL, 20));
		p.getInventory().addItem(given);
		p.setExp(Integer.MAX_VALUE);
	}
	public void distributeItems(){
		for(UUID u : teamM.getPlayersInGame()){//INFINITE LEVELS, 20 ANVILS, FISHING ROD WITH LOFTS 250 & UNBREAKING 150, ENCHANT TABLES DISABLED
			if(Bukkit.getServer().getPlayer(u).isOnline()){
				Player p = Bukkit.getServer().getPlayer(u);
				giveAPlayerGoneFishingItems(p);
				
			}
			else{
				lateGoneFishing.add(u);
			}
		}
	}
	
}
