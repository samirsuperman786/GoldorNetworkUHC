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

	//instances
	private TeamManager teamM;
	private TimerManager timerM;
	
	public CreateCommand(TeamManager teamM, TimerManager timerM) {
		this.teamM=teamM;
		this.timerM=timerM;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player p = (Player) sender;

		if(!(sender instanceof Player)){
			MessageSender.noConsole(sender);
			return true;
		}


		else if(teamM.isPlayerInGame(p)){
			MessageSender.send(ChatColor.RED, p, "You are already on a team!");
			return true;
		}

		else if(timerM.hasCountDownEnded()){
			MessageSender.send(ChatColor.RED, p, "Match has already started!");
			return true;
		}
		else if(timerM.hasMatchStarted()==false){
			MessageSender.send(ChatColor.RED, sender, "Game has not started yet!");
			return true;
		}
		else if(args.length==0){
			if(teamM.isTeamsEnabled()){
				if(teamM.createRandomTeam(p)==true){
					MessageSender.alertMessage(p, ChatColor.GREEN, "You have created a team, please use /invite");

				}
				else{
					MessageSender.alertMessage(p, ChatColor.RED, "Teams are full!");
				}
				return true;
			}
			else if(teamM.isFFAEnabled()){
				MessageSender.alertMessage(p, ChatColor.RED, "Please use /join");
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
