package com.goldornetwork.uhc.managers.world.listeners.team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class ChatManager implements Listener{

	private UHC plugin;
	private TeamManager teamM;
	private boolean mutePlayers;

	public ChatManager(UHC plugin, TeamManager teamM) {
		this.plugin=plugin;
		this.teamM=teamM;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		mutePlayers=false;
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
