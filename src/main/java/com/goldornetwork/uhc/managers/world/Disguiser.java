package com.goldornetwork.uhc.managers.world;

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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class Disguiser implements Listener{


	private UHC plugin;
	private TeamManager teamM;
	
	
	private Set<UUID> hidden = new HashSet<UUID>();
	private Set<UUID> cooldown = new HashSet<UUID>();


	public Disguiser(UHC plugin, TeamManager teamM) {
		this.plugin=plugin;
		this.teamM=teamM;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(PlayerJoinEvent e){
		Player target = e.getPlayer();
		if(State.getState().equals(State.OPEN) || State.getState().equals(State.NOT_RUNNING)){
			for(UUID u : hidden){
				if(PlayerUtils.getOfflinePlayer(u).isOnline()){
					Player hiddenPlayer = PlayerUtils.getPlayer(u);
					hiddenPlayer.hidePlayer(target);
				}
			}
			target.getInventory().addItem(getInactiveDisguiser());
		}
		showAllTo(target);
	}

	@EventHandler
	public void on(PlayerQuitEvent e){
		if(hidden.contains(e.getPlayer().getUniqueId())){
			hidden.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent e){
		Player target = e.getPlayer();
		if(State.getState().equals(State.OPEN) || State.getState().equals(State.NOT_RUNNING)){
			if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				if(e.getItem()!=null){
					if(e.getItem().equals(getInactiveDisguiser())){
						if(cooldown.contains(target.getUniqueId())){
							target.sendMessage(ChatColor.RED + "You are cooldown.");
						}
						else{
							target.sendMessage(ChatColor.AQUA + "Players are now hidden.");
							target.getInventory().remove(getInactiveDisguiser());
							hideAllFrom(target);
							addCooldown(target.getUniqueId());
							target.getInventory().addItem(getActiveDisguiser());
						}
					}
					else if(e.getItem().equals(getActiveDisguiser())){
						if(cooldown.contains(target.getUniqueId())){
							target.sendMessage(ChatColor.RED + "You are cooldown.");
						}
						else{
							target.sendMessage(ChatColor.AQUA + "Players are now shown.");
							target.getInventory().remove(getActiveDisguiser());
							showAllTo(target);
							addCooldown(target.getUniqueId());
							target.getInventory().addItem(getInactiveDisguiser());
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void on(GameStartEvent e){
		for(UUID u : hidden){
			if(PlayerUtils.getOfflinePlayer(u).isOnline()){
				Player target = PlayerUtils.getPlayer(u);
				showAllTo(target);
			}
		}
		hidden.clear();
	}

	private ItemStack getActiveDisguiser(){
		Material item = Material.INK_SACK;
		ItemStack im = new ItemStack(item, 1, (byte) 10);
		ItemMeta meta = im.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Players are hidden.");
		im.setItemMeta(meta);
		return im;
	}

	private ItemStack getInactiveDisguiser(){
		Material item = Material.INK_SACK;
		ItemStack im = new ItemStack(item, 1, (byte) 8);
		ItemMeta meta = im.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Players are shown.");
		im.setItemMeta(meta);
		return im;
	}

	private void hideAllFrom(Player target){
		hidden.add(target.getUniqueId());
		for(Player online : Bukkit.getOnlinePlayers()){
			target.hidePlayer(online);
		}
	}
	
	@SuppressWarnings("unused")
	private void hideAllFrom(Player target, String team){
		hidden.add(target.getUniqueId());
		for(Player online : Bukkit.getOnlinePlayers()){
			if(teamM.areTeamMates(target.getUniqueId(), online.getUniqueId())==false){
				target.hidePlayer(online);
			}
		}
	}

	private void showAllTo(Player target){
		if(hidden.contains(target.getUniqueId())){
			hidden.remove(target.getUniqueId());
		}

		for(Player online : Bukkit.getOnlinePlayers()){
			target.showPlayer(online);
		}
	}

	private void addCooldown(UUID u){
		cooldown.add(u);

		new BukkitRunnable() {
			@Override
			public void run() {
				cooldown.remove(u);
			}
		}.runTaskLater(plugin, 60L);
	}
}
