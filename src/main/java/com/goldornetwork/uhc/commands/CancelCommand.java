package com.goldornetwork.uhc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class CancelCommand implements CommandExecutor {

	private MessageSender ms = new MessageSender();
	private TimerManager timerM = TimerManager.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!(sender.hasPermission("uhc.start"))){
			ms.noPerms(sender);
			return true;
		}
		else if(timerM.hasMatchStarted()){
			ms.send(ChatColor.RED, sender, "Match has already started!");
			return true;
		}
		else{
			timerM.cancelMatch();
			ms.send(ChatColor.GREEN, sender, "You have canceled the match!");
			return true;
		}
		

	}

}
