package com.goldornetwork.uhc.managers.world;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UHCBan {

	
	public void banPlayer(Player banner, Player target, String reason){
		Bukkit.getServer().getBanList(BanList.Type.NAME).addBan(target.getName(), reason, null, banner.getName());
		target.kickPlayer(reason);
	}
}
