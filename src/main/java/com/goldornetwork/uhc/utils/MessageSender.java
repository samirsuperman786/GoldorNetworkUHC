package com.goldornetwork.uhc.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageSender {
	
	private static String getPrefix(){
		return (ChatColor.GOLD + "[GoldorNetwork] ");
	}

	public static void send(ChatColor cc, Player p, String msg){
		p.sendMessage(getPrefix() + cc + msg);
	}
	public static void send(ChatColor cc, CommandSender sender, String msg){
		sender.sendMessage(getPrefix() + cc + msg);
	}
	
	public static void noPerms(Player p){
		p.sendMessage(getPrefix() + ChatColor.RED + "No permission.");
	}
	public static void noPerms(CommandSender sender){
		sender.sendMessage(getPrefix() + ChatColor.RED + "No permission.");
	}
	
	public static void noConsole(CommandSender sender){
		sender.sendMessage(getPrefix() + ChatColor.RED + "Console cannot run this command!"); 
	}
	
	public static void alertMessage(Player p, ChatColor cc, String msg){
		p.sendMessage(getPrefix() + ChatColor.MAGIC + "G" + cc + msg + ChatColor.MAGIC + "G");
	}
	
	public static void sendToOPS(String msg){
		Bukkit.broadcast(ChatColor.RED + "[ANTICHEAT] " + msg, Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
	}
	public static void broadcast(String msg){
		Bukkit.getServer().broadcastMessage(getPrefix() + ChatColor.RED + msg);
	}
	
}
