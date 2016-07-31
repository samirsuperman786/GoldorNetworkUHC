package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.tabapi.TabAPI;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.managers.world.customevents.GameEndEvent;
import com.goldornetwork.uhc.managers.world.customevents.GameStartEvent;
import com.goldornetwork.uhc.utils.PlayerUtils;


public class TabManager implements Listener{


	private UHC plugin;
	private GameModeManager gamemodeM;

	private List<UUID> tabs = new ArrayList<UUID>();
	private int slots;
	private int timer;
	private boolean matchEnd =false;
	
	public TabManager(UHC plugin, GameModeManager gamemodeM){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin=plugin;
		this.gamemodeM=gamemodeM;
		this.slots = (plugin.getConfig().getInt("Fake-Player-Slots") + plugin.getConfig().getInt("BUFFER-PLAYER-SLOTS"));
		headerUpdater();
	}

	@EventHandler
	public void on(PlayerJoinEvent e){
		addToObserverBoard(e.getPlayer());
		for(UUID u : tabs){
			if(PlayerUtils.getOfflinePlayer(u).isOnline()){
				Player target = PlayerUtils.getPlayer(u);
				TabAPI.updateTab(target);	
			}
		}
	}

	@EventHandler
	public void on(PlayerQuitEvent e){
		if(tabs.contains(e.getPlayer().getUniqueId())){
			removeObserverBoard(e.getPlayer());
		}
	}
	@EventHandler
	public void on(GameStartEvent e){
		updateFooter();
	}
	
	@EventHandler
	public void on(GameEndEvent e){
		matchEnd=true;
	}

	public void addToObserverBoard(Player target){
		tabs.add(target.getUniqueId());

		TabAPI.setHeader(target, ChatColor.GOLD + "" + ChatColor.BOLD + "GoldorNetwork " + ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "UHC" + ChatColor.DARK_AQUA + "]", 
				"",
				ChatColor.DARK_AQUA + "Online Players: " + ChatColor.WHITE + plugin.getServer().getOnlinePlayers().size() + ChatColor.GRAY + "/" + ChatColor.WHITE + slots,
				"");
		TabAPI.setFooter(target, "",
				ChatColor.DARK_AQUA + "Elapsed Time: " + getTime());

		TabAPI.updateTab(target);
	}

	public void removeObserverBoard(Player target){
		tabs.remove(target.getUniqueId());
		TabAPI.clearAllItems(target);
		TabAPI.removeTab(target);
		TabAPI.setHeaderFooter(target, "", "");
		TabAPI.updateTab(target);
	}

	private String getTime(){
		int hours = timer/3600;
		int minutes = (timer/60) %60;
		int seconds = timer % 60;
		String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		String prefix;
		
		if(timer==0){
			prefix = ChatColor.GOLD.toString();
		}
		else if(timer>0 && matchEnd ==false){
			prefix = ChatColor.GREEN.toString();
		}
		else{
			prefix = ChatColor.DARK_RED.toString();
		}
		return prefix + timeString;
	}

	private void headerUpdater(){
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(UUID u : tabs){
					if(PlayerUtils.getOfflinePlayer(u).isOnline()){
						Player target = PlayerUtils.getPlayer(u);
						if(gamemodeM.getEnabledGamemodes().isEmpty()){
							TabAPI.setHeader(target, ChatColor.GOLD + "" + ChatColor.BOLD + "GoldorNetwork " + ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "UHC" + ChatColor.DARK_AQUA + "]", 
									"",
									ChatColor.DARK_AQUA + "Online Players: " + ChatColor.WHITE + plugin.getServer().getOnlinePlayers().size() + ChatColor.GRAY + "/" + ChatColor.WHITE + slots,
									"");
						}
						else{
							int comma = 0;
							StringBuilder str = new StringBuilder();

							for(Gamemode game : gamemodeM.getEnabledGamemodes()){
								comma++;
								String message = ChatColor.AQUA + game.getProperName();
								String properMessage;
								if(comma<gamemodeM.getEnabledGamemodes().size()){
									properMessage = message + ChatColor.GRAY + " + ";
								}
								else{
									properMessage=message;
								}
								str.append(properMessage);
							}
							String msg = str.toString();
							
							
							TabAPI.setHeader(target, ChatColor.GOLD + "" + ChatColor.BOLD + "GoldorNetwork " + ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "UHC" + ChatColor.DARK_AQUA + "]", 
									"",
									ChatColor.DARK_AQUA + "Online Players: " + ChatColor.WHITE + plugin.getServer().getOnlinePlayers().size() + ChatColor.GRAY + "/" + ChatColor.WHITE + slots,
									"", 
									ChatColor.DARK_AQUA + "Enabled Scenarios: " + ChatColor.AQUA + msg);
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0L, 40L);
	}
	
	private void updateFooter(){
		new BukkitRunnable() {
			@Override
			public void run() {
				if(matchEnd==false){
					for(UUID u : tabs){
						if(PlayerUtils.getOfflinePlayer(u).isOnline()){
							Player target = PlayerUtils.getPlayer(u);
							TabAPI.setFooter(target, "",
									ChatColor.DARK_AQUA + "Elapsed Time: " + getTime());
						}
					}
					timer++;
				}
				else{
					for(UUID u : tabs){
						if(PlayerUtils.getOfflinePlayer(u).isOnline()){
							Player target = PlayerUtils.getPlayer(u);
							TabAPI.setFooter(target, "",
									ChatColor.DARK_AQUA + "Elapsed Time: " + getTime());
						}
					}
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}
}
