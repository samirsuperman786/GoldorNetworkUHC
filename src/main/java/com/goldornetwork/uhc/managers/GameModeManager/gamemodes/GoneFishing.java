package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.events.GameStartEvent;

public class GoneFishing extends Gamemode implements Listener {

	private TeamManager teamM;
	
	private List <UUID> lateGoneFishing = new ArrayList<UUID>();
	
	public GoneFishing(TeamManager teamM) {
		super("Gone Fishing", "GoneFishing", "Players spawn with infinite levels, 20 anvils, a fishing rod with: Luck of the Sea 250, Lure 250, and Unbreaking 150. Caution: Enchantment tables are disabled.");
		this.teamM=teamM;
	}
	
	@Override
	public void onEnable() {
		lateGoneFishing.clear();
	}
	
	@EventHandler
	public void on(GameStartEvent e){
		distributeItems();
	}
	
	@EventHandler
	public void on(PrepareItemCraftEvent e){
		if(State.getState().equals(State.INGAME)){
			Material item = e.getRecipe().getResult().getType();
				if(item.equals(Material.ENCHANTMENT_TABLE)){
					e.getInventory().setResult(new ItemStack(Material.AIR));
				}
			
		}	
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(State.getState().equals(State.INGAME)){
			if(teamM.isPlayerInGame(p.getUniqueId())){
				if(lateGoneFishing.contains(p.getUniqueId())){
					giveAPlayerGoneFishingItems(p);
					removeAPlayerFromLateGoneFishing(p);
				}
			}
		}
	}
	
	private void removeAPlayerFromLateGoneFishing(Player p){
		lateGoneFishing.remove(p.getUniqueId());
	}
	

	private void giveAPlayerGoneFishingItems(Player p){
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
	private void distributeItems(){
		for(UUID u : teamM.getPlayersInGame()){//INFINITE LEVELS, 20 ANVILS, FISHING ROD WITH LOFTS 250 & UNBREAKING 150, ENCHANT TABLES DISABLED
			if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
				Player p = Bukkit.getServer().getPlayer(u);
				giveAPlayerGoneFishingItems(p);
				
			}
			else{
				lateGoneFishing.add(u);
			}
		}
	}
	
}
