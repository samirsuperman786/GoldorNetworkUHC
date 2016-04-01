package com.goldornetwork.uhc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class CancelCommand implements CommandExecutor {

	//instances
	private TimerManager timerM;

	public CancelCommand(TimerManager timerM) {
		this.timerM=timerM;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!(sender.hasPermission("uhc.start"))){
			MessageSender.noPerms(sender);
			return true;
		}
		else if(timerM.hasMatchStarted()){
			MessageSender.send(ChatColor.RED, sender, "Match has already started!");
			return true;
		}
		else{
			timerM.cancelMatch();
			MessageSender.send(ChatColor.GREEN, sender, "You have canceled the match!");
			return true;
		}


	}

}
