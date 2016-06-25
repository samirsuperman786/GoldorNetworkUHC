package com.goldornetwork.uhc.managers.world;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UHCWarn {


	public void warnPlayer(Player target, String reason){
		target.sendMessage(ChatColor.GOLD + "" + ChatColor.MAGIC + "G" + ChatColor.GOLD + "[WARN] "
				+ ChatColor.RED + reason + ChatColor.GOLD + "" + ChatColor.MAGIC + "G");
	}
}
