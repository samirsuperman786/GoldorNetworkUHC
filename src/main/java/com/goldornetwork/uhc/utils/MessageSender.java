package com.goldornetwork.uhc.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageSender {
	
	private String getPrefix(){
		return (ChatColor.GOLD + "[GoldorNetwork] ");
	}

	public void send(ChatColor cc, Player p, String msg){
		p.sendMessage(getPrefix() + cc + msg);
	}
	public void send(ChatColor cc, CommandSender sender, String msg){
		sender.sendMessage(getPrefix() + cc + msg);
	}
	
	public void noPerms(Player p){
		p.sendMessage(getPrefix() + ChatColor.RED + "No permission.");
	}
	public void noPerms(CommandSender sender){
		sender.sendMessage(getPrefix() + ChatColor.RED + "No permission.");
	}
	
	public void noConsole(CommandSender sender){
		sender.sendMessage(getPrefix() + ChatColor.RED + "Console cannot run this command!"); 
	}
	
	public void alertMessage(Player p, ChatColor cc, String msg){
		p.sendMessage(getPrefix() + ChatColor.MAGIC + "G" + cc + msg + ChatColor.MAGIC + "G");
	}
	
	public void sendToOPS(String msg){
		Bukkit.broadcast(ChatColor.RED + "[ANTICHEAT] " + msg, Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
	}
	public void broadcast(String msg){
		Bukkit.getServer().broadcastMessage(getPrefix() + ChatColor.RED + msg);
	}
	
}
