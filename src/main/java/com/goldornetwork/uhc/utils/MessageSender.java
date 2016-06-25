package com.goldornetwork.uhc.utils;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;

public class MessageSender {

	
	private static String getPrefix(){
		return (ChatColor.GOLD + "[GN] ");
	}

	public static void send(Player target, String msg){
		target.sendMessage(getPrefix() + msg);
	}

	public static void send(CommandSender sender, String msg){
		sender.sendMessage(getPrefix() + msg);
	}

	public static void send(Player p, List<String> msg){
		List<String> toEdit = new LinkedList<String>();

		for(String messages : msg){
			toEdit.add(getPrefix() + ChatColor.GOLD + messages);
		}
		String[] toSend = new String[toEdit.size()];
		toEdit.toArray(toSend);

		p.sendMessage(toSend);
	}

	public static void send(CommandSender sender, List<String> msg){
		List<String> toEdit = new LinkedList<String>();

		for(String messages : msg){
			toEdit.add(getPrefix() + ChatColor.GOLD + messages);
		}
		String[] toSend = new String[toEdit.size()];
		toEdit.toArray(toSend);

		sender.sendMessage(toSend);
	}


	public static void noPerms(Player p){
		p.sendMessage(getPrefix() + ChatColor.RED + "No permission.");
	}

	public static void noPerms(CommandSender sender){
		sender.sendMessage(getPrefix() + ChatColor.RED + "No permission.");
	}

	public static void usageMessage(CommandSender sender, String usage){
		sender.sendMessage(getPrefix() + ChatColor.RED + "Usage: " + usage);
	}

	public static void noConsole(CommandSender sender){
		sender.sendMessage(getPrefix() + ChatColor.RED + "Console cannot run this command."); 
	}

	public static void alertMessage(Player p, String msg){
		p.sendMessage(getPrefix() + ChatColor.MAGIC + "G" + ChatColor.GOLD + msg + ChatColor.MAGIC + "G");
	}

	public static void sendToOPS(String msg){
		Bukkit.broadcast(ChatColor.GOLD + "[OPS] " + msg, "uhc.broadcast.mod");
	}
	
	public static void sendToOPS(TextComponent msg){
		//Bukkit.broadcast(ChatColor.GOLD + "[OPS] " + msg, "uhc.broadcast.mod");
		for(Player online : Bukkit.getOnlinePlayers()){
			if(online.hasPermission("uhc.broadcast.mod")){
				Player target = online;
				target.spigot().sendMessage(msg);
			}
		}
	}
	
	public static void broadcast(String msg){
		for(Player online: Bukkit.getOnlinePlayers()){
			online.sendMessage(getPrefix() + ChatColor.GOLD + msg);
		}
	}

	public static void broadcast(List<String> msg){
		List<String> toEdit = new LinkedList<String>();

		for(String messages : msg){
			toEdit.add(getPrefix() + ChatColor.GOLD + messages);
		}
		String[] toSend = new String[toEdit.size()];
		toEdit.toArray(toSend);

		for(Player online: Bukkit.getOnlinePlayers()){
			online.sendMessage(toSend);
		}
	}

	@SuppressWarnings("deprecation")
	public static void broadcastTitle(String title, String subtitle){
		for(Player online: Bukkit.getOnlinePlayers()){
			online.sendTitle(title, subtitle);
		}
	}

	@SuppressWarnings("deprecation")
	public static void broadcastBigTitle(String title){
		for(Player online: Bukkit.getOnlinePlayers()){
			online.sendTitle(title, "");
		}
	}

	@SuppressWarnings("deprecation")
	public static void broadcastSmallTitle(String subtitle){
		for(Player online: Bukkit.getOnlinePlayers()){
			online.sendTitle("", subtitle);
		}
	}
}
