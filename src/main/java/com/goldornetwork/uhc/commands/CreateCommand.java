package com.goldornetwork.uhc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class CreateCommand implements CommandExecutor{

	private TeamManager teamM = TeamManager.getInstance();
	private TimerManager timerM = TimerManager.getInstance();
	private MessageSender ms = new MessageSender();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player p = (Player) sender;
		
		if(!(sender instanceof Player)){
			ms.noConsole(sender);
			return true;
		}
		
		
		else if(teamM.isPlayerInGame(p)){
			ms.send(ChatColor.RED, p, "You are already on a team!");
			return true;
		}
		
		else if(timerM.hasMatchStarted()){
			ms.send(ChatColor.RED, p, "Match has already started!");
			return true;
		}
		else if(args.length==0){
			if(teamM.isTeamsEnabled()){
				if(teamM.createRandomTeam(p)==true){
					ms.alertMessage(p, ChatColor.GREEN, "You have created a team, please use /invite");
				
				}
				else{
					ms.alertMessage(p, ChatColor.RED, "Teams are full!");
				}
				return true;
			}
			else if(teamM.isFFAEnabled()){
				ms.alertMessage(p, ChatColor.RED, "Please use /join");
				return true;
			}
			else{
				return false;
			}
		}
		
		else{
			return false;
		}
		
		
		
		
	}

}
