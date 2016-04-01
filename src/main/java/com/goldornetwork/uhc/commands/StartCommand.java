package com.goldornetwork.uhc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class StartCommand implements CommandExecutor{

	//instances
	private TimerManager timerM;
	
	public StartCommand(TimerManager timerM) {
		this.timerM=timerM;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		if(!(sender.hasPermission("uhc.start"))){
			MessageSender.noPerms(sender);
			return true;
		}
		else if(args.length == 0){
			if(timerM.hasMatchStarted()==true){
				MessageSender.send(ChatColor.RED, sender, "Match has already started!");
				return true;
			}
			else{
				timerM.startMatch(true, 15*60, 40*60); 
				MessageSender.send(ChatColor.GREEN, sender, "You have started the match!");
				return true;
			}
		}
		else{
			return false;
		}
		
		
	}

}
