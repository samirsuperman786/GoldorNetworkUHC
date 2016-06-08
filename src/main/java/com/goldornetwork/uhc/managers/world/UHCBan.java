package com.goldornetwork.uhc.managers.world;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class UHCBan {

	private TeamManager teamM;
	
	public UHCBan(TeamManager teamM) {
		this.teamM=teamM;
	}
	public void banPlayer(Player banner, Player target, String reason){
		
		MessageSender.broadcast(PlayerUtils.getPrefix(banner) + teamM.getColorOfPlayer(banner.getUniqueId()) + banner.getName() + ChatColor.GOLD + " \u27A0 Banned \u27A0 " + PlayerUtils.getPrefix(target) + teamM.getColorOfPlayer(target.getUniqueId()) + target.getName() + ChatColor.GOLD + " \u27A0 " + reason);
		Bukkit.getServer().getBanList(BanList.Type.NAME).addBan(target.getName(), reason, null, banner.getName());
		target.kickPlayer(ChatColor.GOLD + reason);
		
	}
}
