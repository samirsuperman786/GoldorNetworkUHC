package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;
import com.goldornetwork.uhc.managers.world.customevents.UHCEnterMapEvent;
import com.goldornetwork.uhc.utils.MessageSender;

public class TheHobbitManager extends Gamemode implements Listener{


	private TeamManager teamM;

	private Set<UUID> lateHobbits = new HashSet<UUID>();

	
	public TheHobbitManager(TeamManager teamM) {
		super("The Hobbit", "TheHobbit", "Players receive one golden nugget and when right clicked, the player receives invisibility for 30 seconds.");
		this.teamM=teamM;
	}

	@Override
	public void onEnable() {
		lateHobbits.clear();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(GameStartEvent e){
		distributeItems();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(UHCEnterMapEvent e){
		Player p = e.getPlayer();

		if(lateHobbits.contains(p.getUniqueId())){
			giveAPlayerHobbitItems(p);
			removePlayerFromLateHobbits(p);
		}
	}

	private void giveAPlayerHobbitItems(Player p){
		ItemStack given = new ItemStack(Material.GOLD_NUGGET,1);
		ItemMeta im = given.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "The Magic Ring of Invisibility");
		given.setItemMeta(im);
		p.getInventory().addItem(given);
	}

	private void removePlayerFromLateHobbits(Player p){
		lateHobbits.remove(p.getUniqueId());
	}

	private void distributeItems(){
		for(UUID u : teamM.getPlayersInGame()){
			if(Bukkit.getServer().getOfflinePlayer(u).isOnline()){
				Player p = Bukkit.getServer().getPlayer(u);
				giveAPlayerHobbitItems(p);
			}
			else{
				lateHobbits.add(u);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerInteractEvent e){
		Player p = e.getPlayer();

		if(State.getState().equals(State.INGAME)){
			if(teamM.isPlayerInGame(p.getUniqueId())){
				if(e.getMaterial().equals(Material.GOLD_NUGGET)){
					if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
						if(e.getItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "The Magic Ring of Invisibility")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30*20, 0, false, false));
							p.getInventory().remove(p.getItemInHand());
							MessageSender.send(p, ChatColor.GOLD + "You have activated your invisibility ring!");
						}
					}
				}
			}
		}
	}
}
