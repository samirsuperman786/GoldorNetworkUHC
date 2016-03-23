package com.goldornetwork.uhc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class StartCommand implements CommandExecutor{

	//instances
	private TimerManager timerM = TimerManager.getInstance();
	private MessageSender ms = new MessageSender();
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		if(!(sender.hasPermission("uhc.start"))){
			ms.noPerms(sender);
			return true;
		}
		else if(args.length == 0){
			if(timerM.hasMatchStarted()==true){
				ms.send(ChatColor.RED, sender, "Match has already started!");
				return true;
			}
			else{
				timerM.startMatch(true, 10*60, 40*60); 
				ms.send(ChatColor.GREEN, sender, "You have started the match!");
				return true;
			}
		}
		else{
			return false;
		}
		
		
	}

}
