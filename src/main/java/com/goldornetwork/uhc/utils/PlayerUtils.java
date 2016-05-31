package com.goldornetwork.uhc.utils;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerUtils {
	
	public static String getPrefix(OfflinePlayer p){
		if(p.isOnline()){
			Player target = (Player) p;
			if(target.hasPermission("uhc.chat.mod")){
				return ChatColor.RED.toString() + "\u2739" + ChatColor.WHITE.toString();
			}
			else if(target.hasPermission("uhc.chat.admin")){
				return ChatColor.GOLD.toString() + "\u2739" + ChatColor.WHITE.toString();
			}
			else{
				return "";
			}
		}
		else{
			return "";
		}
		
	}
	
	
}
