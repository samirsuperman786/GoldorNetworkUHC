package com.goldornetwork.uhc.managers.world.listeners.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class ChatManager implements Listener{

	private UHC plugin;
	private TeamManager teamM;
	private boolean mutePlayers;

	private List<UUID> muted = new ArrayList<UUID>();
	
	public ChatManager(UHC plugin, TeamManager teamM) {
		this.plugin=plugin;
		this.teamM=teamM;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		mutePlayers=false;
	}

	public void mute(Player banner, Player target, String reason, int minutes){
		muted.add(target.getUniqueId());
		MessageSender.broadcast(PlayerUtils.getPrefix(banner) + teamM.getColorOfPlayer(banner.getUniqueId()) + banner.getName() + ChatColor.GOLD + " \u27A0 " + minutes + " Minute Mute \u27A0 " + PlayerUtils.getPrefix(target) + teamM.getColorOfPlayer(target.getUniqueId()) + target.getName() + ChatColor.GOLD + " \u27A0 " + reason);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				unMute(target.getUniqueId());
				
			}
		}.runTaskLater(plugin, minutes * 60 * 20);
	}
	public void unMute(UUID target){
		muted.remove(target);
		if(Bukkit.getOfflinePlayer(target).isOnline()){
			Player p = Bukkit.getPlayer(target);
			p.sendMessage(ChatColor.GOLD + "You have been unmuted.");
		}
		
	}
	
	public void mutePlayers(){
		mutePlayers=true;
		MessageSender.broadcast("Chat has been muted.");
	}

	public void unMutePlayers(){
		mutePlayers= false;
		MessageSender.broadcast("Chat has been unmuted!");
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void on(AsyncPlayerChatEvent e){
		Player sender = e.getPlayer();
		e.setCancelled(true);
		if(mutePlayers && sender.hasPermission("uhc.chat.mutebypass")==false){
			return;
		}
		else if(muted.contains(sender.getUniqueId())){
			return;
		}
		else{
			for(Player all : Bukkit.getServer().getOnlinePlayers()){
				if(teamM.isTeamsEnabled()){
					if(teamM.isPlayerOnTeam(sender.getUniqueId())){
						if(teamM.getPlayersOnATeam(teamM.getTeamOfPlayer(sender.getUniqueId())).contains(all.getUniqueId())){
							all.sendMessage(teamM.getColorOfTeam(teamM.getTeamOfPlayer(sender.getUniqueId())) + "[" + teamM.getTeamNameProper(teamM.getTeamOfPlayer(sender.getUniqueId()))+ "] " + PlayerUtils.getPrefix(sender) + ChatColor.GREEN + sender.getName() +  ChatColor.WHITE + ": " + e.getMessage());
						}
						else{
							all.sendMessage(teamM.getColorOfTeam(teamM.getTeamOfPlayer(sender.getUniqueId())) + "[" + teamM.getTeamNameProper(teamM.getTeamOfPlayer(sender.getUniqueId()))+ "] " + PlayerUtils.getPrefix(sender) + ChatColor.RED + sender.getName() +  ChatColor.WHITE + ": " + e.getMessage());
						}
					}
					else if(teamM.isPlayerAnObserver(sender.getUniqueId())){
						all.sendMessage(teamM.getColorOfPlayer(sender.getUniqueId()) + "[Observer] " + PlayerUtils.getPrefix(sender)+ ChatColor.AQUA + sender.getName() +  ChatColor.WHITE + ": " + e.getMessage());
					}
					else{
						all.sendMessage(PlayerUtils.getPrefix(sender) + teamM.getColorOfPlayer(sender.getUniqueId()) + sender.getName() + ChatColor.WHITE + ": " + e.getMessage());
					}
				}
				
				else{
					all.sendMessage(PlayerUtils.getPrefix(sender) + teamM.getColorOfPlayer(sender.getUniqueId()) + sender.getName() + ": " + e.getMessage());
				}
			}
		}

	}
}
