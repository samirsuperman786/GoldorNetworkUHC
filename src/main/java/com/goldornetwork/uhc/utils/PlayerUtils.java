package com.goldornetwork.uhc.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerUtils {


	public static String getPrefix(OfflinePlayer p){
		if(p.isOnline()){
			Player target = (Player) p;
			
			String donatorPrefix="";
			String prefix ="";
			
			if(target.hasPermission("uhc.donator")){
				donatorPrefix= ChatColor.GREEN + "*";
			}
			if(target.hasPermission("uhc.chat.mod")){
				prefix = ChatColor.RED + "\u2756" + ChatColor.WHITE.toString();
			}
			else if(target.hasPermission("uhc.chat.admin")){
				prefix= ChatColor.GOLD + "\u2756" + ChatColor.WHITE.toString();
			}
			return donatorPrefix + prefix;
			
		}
		else{
			return "";
		}
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isPlayerOnline(String input){
		return Bukkit.getServer().getOfflinePlayer(input).isOnline();
	}
	
	public static Player getPlayer(String input){
		return Bukkit.getServer().getPlayer(input);
	}
	
	@SuppressWarnings("deprecation")
	public static OfflinePlayer getOfflinePlayer(String input){
		return Bukkit.getServer().getOfflinePlayer(input);
	}
	
	public static Player getPlayerExact(String input){
		return Bukkit.getServer().getPlayerExact(input);
	}
	
	public static OfflinePlayer getOfflinePlayer(UUID u){
		return Bukkit.getServer().getOfflinePlayer(u);
	}
	
	public static Player getPlayer(UUID u){
		return Bukkit.getServer().getPlayer(u);
	}
	
}
