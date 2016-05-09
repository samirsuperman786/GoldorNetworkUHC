package com.goldornetwork.uhc.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerUtils {

	public static String getPrefix(Player target){
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
}
