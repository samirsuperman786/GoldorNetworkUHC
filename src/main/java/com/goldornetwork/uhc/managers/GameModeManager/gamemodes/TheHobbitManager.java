package com.goldornetwork.uhc.managers.GameModeManager.gamemodes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.GameStartEvent;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class TheHobbitManager extends Gamemode implements Listener{

	//instances
	private TeamManager teamM;

	
	//storage
	private List<UUID> lateHobbits = new ArrayList<UUID>();
	
	public TheHobbitManager(TeamManager teamM) {
		super("TheHobbit", "Players receive one golden nugget and when clicked, the player receives invisibility for 30 seconds!");
		this.teamM=teamM;
	}
	@Override
	public void onEnable() {
		lateHobbits.clear();
	}
	
	@Override
	public void onDisable() {}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(GameStartEvent e){
		distributeItems();
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(State.getState().equals(State.INGAME)){
			if(lateHobbits.contains(p.getUniqueId())){
				giveAPlayerHobbitItems(p);
				removePlayerFromLateHobbits(p);
			}
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
			if(Bukkit.getServer().getPlayer(u).isOnline()){
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
				if(teamM.isPlayerInGame(p)){
					if(p.getItemInHand().equals(Material.GOLD_NUGGET)){
						if(p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "The Magic Ring of Invisibility")){
									p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30*20, 1));
									p.getInventory().remove(p.getItemInHand());
									MessageSender.send(ChatColor.GOLD, p, "You have activated your invisibility ring!");
						}
					}
				}
			}
	}


}
