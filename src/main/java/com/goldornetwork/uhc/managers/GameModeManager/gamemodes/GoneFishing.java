package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;
import com.goldornetwork.uhc.managers.world.customevents.UHCEnterMapEvent;

public class GoneFishing extends Gamemode implements Listener {


	private TeamManager teamM;

	private Set<UUID> lateGoneFishing = new HashSet<UUID>();
	private final ItemStack rod;

	public GoneFishing(TeamManager teamM) {
		super("Gone Fishing", "GoneFishing", "Players spawn with infinite levels, 64 anvils, a fishing rod with:"
				+ " Luck of the Sea 100, Lure 100, and Unbreaking 100. Caution: Enchantment tables are disabled.");
		this.teamM=teamM;
		this.rod = new ItemStack(Material.FISHING_ROD, 1);
		ItemMeta meta = rod.getItemMeta();
		meta.addEnchant(Enchantment.DURABILITY, 100, true);
		meta.addEnchant(Enchantment.LURE, 100, true);
		meta.addEnchant(Enchantment.LUCK, 100, true);
		meta.setDisplayName(ChatColor.AQUA + "Fish Slapper 100");
		rod.setItemMeta(meta);
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
		if(State.getState().equals(State.INGAME) || State.getState().equals(State.SCATTER)){
			Material item = e.getRecipe().getResult().getType();
			if(item.equals(Material.ENCHANTMENT_TABLE)){
				e.getInventory().setResult(new ItemStack(Material.AIR));
			}
		}	
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(UHCEnterMapEvent e){
		Player p = e.getPlayer();

		if(lateGoneFishing.contains(p.getUniqueId())){
			giveAPlayerGoneFishingItems(p);
			removeAPlayerFromLateGoneFishing(p);
		}
	}

	private void removeAPlayerFromLateGoneFishing(Player p){
		lateGoneFishing.remove(p.getUniqueId());
	}

	private void giveAPlayerGoneFishingItems(Player p){
		p.getInventory().addItem(new ItemStack(Material.ANVIL, 64));
		p.getInventory().addItem(rod);
		p.setLevel(Integer.MAX_VALUE);
	}
	
	private void distributeItems(){

		for(UUID u : teamM.getPlayersInGame()){
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
