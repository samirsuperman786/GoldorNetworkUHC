package com.goldornetwork.uhc.managers.world;

import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.ubl.UBL;

public class UHCServer implements Listener{


	private UHC plugin;
	private TeamManager teamM;
	private UBL ubl;
	private UHCBan uhcB;

	private int FAKE_PLAYER_SLOTS;
	private int BUFFER_PLAYER_SLOTS;
	private boolean canJoin = false;

	public UHCServer(UHC plugin, TeamManager teamM, UBL ubl, UHCBan uhcB) {
		this.plugin=plugin;
		this.teamM=teamM;
		this.ubl=ubl;
		this.uhcB=uhcB;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void setup(){
		plugin.getConfig().addDefault("Fake-Player-Slots", 50);
		plugin.getConfig().addDefault("BUFFER-PLAYER-SLOTS", 10);
		plugin.saveConfig();
		this.FAKE_PLAYER_SLOTS=plugin.getConfig().getInt("Fake-Player-Slots");
		this.BUFFER_PLAYER_SLOTS=plugin.getConfig().getInt("BUFFER-PLAYER-SLOTS");
		new BukkitRunnable() {

			@Override
			public void run() {
				canJoin=true;
			}
		}.runTaskLater(plugin, 20L);

	}

	@EventHandler
	public void on(PlayerLoginEvent e){
		Player target = e.getPlayer();

		uhcB.logIP(e.getAddress().getHostAddress(), target.getUniqueId());

		if(canJoin==false){
			e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Server is being set up.");
		}
		else if(ubl.isBanned(target.getUniqueId())){
			e.disallow(PlayerLoginEvent.Result.KICK_BANNED, ubl.getBanMessage(target.getUniqueId()));
		}
		else if(plugin.getServer().getBanList(Type.IP).isBanned(e.getAddress().getHostAddress())){
			if(target.isBanned()==false){
				uhcB.banPlayer(target.getName(), "ChargeBack");
			}
			e.disallow(PlayerLoginEvent.Result.KICK_BANNED, 
					ChatColor.RED + "\nPermanently Banned" + ChatColor.GOLD + "\u27A0" + ChatColor.AQUA
					+ Bukkit.getServer().getBanList(BanList.Type.IP).getBanEntry(e.getAddress().getHostAddress()).getReason()
					+ ChatColor.YELLOW + "\nEmail " + ChatColor.GOLD + "support@goldornetwork.com" + ChatColor.YELLOW + " to appeal.");
		}
		else if(target.isBanned()){
			e.disallow(PlayerLoginEvent.Result.KICK_BANNED, 
					ChatColor.RED + "\nPermanently Banned" + ChatColor.GOLD + "\u27A0" + ChatColor.AQUA
					+ Bukkit.getServer().getBanList(BanList.Type.NAME).getBanEntry(target.getName()).getReason()
					+ ChatColor.YELLOW + "\nEmail " + ChatColor.GOLD + "support@goldornetwork.com" + ChatColor.YELLOW + " to appeal.");
		}
		else if(target.hasPermission("uhc.whitelist.bypass")){
			e.allow();
		}
		else if(teamM.isPlayerInGame(target.getUniqueId())){
			e.allow();
		}
		else if(plugin.getServer().hasWhitelist()){

			if(State.getState().equals(State.OPEN)){
				if(plugin.getServer().getOnlinePlayers().size()<(FAKE_PLAYER_SLOTS)){
					if(target.isWhitelisted()){
						e.allow();
					}
					else{
						e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Server already re-whitelisted, but there are still "
								+ ChatColor.GRAY + ((FAKE_PLAYER_SLOTS + BUFFER_PLAYER_SLOTS) - Bukkit.getServer().getOnlinePlayers().size()) + ChatColor.YELLOW
								+ " slots left for whitelist.");
					}
				}
				else if(plugin.getServer().getOnlinePlayers().size()<(FAKE_PLAYER_SLOTS + BUFFER_PLAYER_SLOTS)){
					if(target.isWhitelisted()){
						e.allow();
					}
					else{
						e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Server is full, but there are still "
								+ ChatColor.GRAY + ((FAKE_PLAYER_SLOTS + BUFFER_PLAYER_SLOTS) - Bukkit.getServer().getOnlinePlayers().size())
								+ ChatColor.YELLOW + " slots left for whitelist.");
					}
				}
				else{
					e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Server is completely full, there are no additional whitelist spots left.");
				}
			}
			else if(State.getState().equals(State.NOT_RUNNING)){
				if(plugin.getServer().getOnlinePlayers().size()<(FAKE_PLAYER_SLOTS)){
					if(target.isWhitelisted()){
						e.allow();
					}
					else{
						e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Server not open yet.");
					}
				}
				else if(plugin.getServer().getOnlinePlayers().size()<(FAKE_PLAYER_SLOTS + BUFFER_PLAYER_SLOTS)){
					if(target.isWhitelisted()){
						e.allow();
					}
					else{
						e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Server is full, but there are still "
								+ ChatColor.GRAY + ((FAKE_PLAYER_SLOTS + BUFFER_PLAYER_SLOTS) - Bukkit.getServer().getOnlinePlayers().size())
								+ ChatColor.YELLOW + " slots left for whitelist.");
					}
				}
				else{
					e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Server is completely full, there are no additional whitelist spots left.");
				}
			}
			else if(State.getState().equals(State.SCATTER)){
				if(plugin.getServer().getOnlinePlayers().size()<(FAKE_PLAYER_SLOTS)){
					e.allow();
				}
				else{
					e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Server is full.");
				}
			}
			else if(State.getState().equals(State.INGAME)){
				if(plugin.getServer().getOnlinePlayers().size()<(FAKE_PLAYER_SLOTS)){
					e.allow();
				}
				else{
					e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Server is full.");
				}
			}
			else{
				e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "You may no longer join the UHC.");
			}
		}
		else if(plugin.getServer().hasWhitelist()==false){
			if(plugin.getServer().getOnlinePlayers().size()<(FAKE_PLAYER_SLOTS)){
				e.allow();
			}
			else if(plugin.getServer().getOnlinePlayers().size()<(FAKE_PLAYER_SLOTS + BUFFER_PLAYER_SLOTS)){
				if(target.isWhitelisted()){
					e.allow();
				}
				else{
					e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Server is full, but there are still "
							+ ChatColor.GRAY + ((FAKE_PLAYER_SLOTS + BUFFER_PLAYER_SLOTS) - Bukkit.getServer().getOnlinePlayers().size())
							+ ChatColor.YELLOW + " slots left for whitelist.");
				}
			}
			else{
				e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Server is completely full, there are no additional whitelist spots left.");
			}
		}
		else{
			e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Can not join the server at this time.");
		}
	}
}
