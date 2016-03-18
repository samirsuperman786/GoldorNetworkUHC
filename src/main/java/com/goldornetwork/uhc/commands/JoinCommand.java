package com.goldornetwork.uhc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class JoinCommand implements CommandExecutor {

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
		else if(teamM.isPlayerInGame(p)&& !(args[0].equalsIgnoreCase("observers")) && !(args[0].equalsIgnoreCase("obs"))){
			ms.send(ChatColor.RED, sender, "You are already on a team");
			return true;
		}
		else if(timerM.hasMatchStarted() && !(args[0].equalsIgnoreCase("observers")) && !(args[0].equalsIgnoreCase("obs"))){
			ms.send(ChatColor.RED, p, "Match has already started!");
			return true;
		}
		else if(args.length == 0){
			if(teamM.isFFAEnabled()){
				teamM.addPlayerToFFA(p);
				return true;
			}
			else{
				return false;
			}
		}
		else if(args.length==1){
			if(args[0].equalsIgnoreCase("observers")||args[0].equalsIgnoreCase("obs")){
				if(teamM.isPlayerAnObserver(p)==false){
					teamM.addPlayerToObservers(p);
					ms.send(ChatColor.AQUA, p, "You have joined the observers");
					return true;
				}
				else{
					ms.send(ChatColor.RED, p, "You are already an observers!");
					return true;
				}
			}
			if(teamM.isTeamsEnabled()){
				if(teamM.isValidTeam(args[0])){
					if(teamM.isPlayerInvitedToTeam(p,args[0].toLowerCase())){
						if(teamM.isTeamRoomToJoin(args[0].toLowerCase())){
							ms.alertMessage(p, ChatColor.GREEN, "You have joined team " + teamM.getColorOfTeam(args[0].toLowerCase() + args[0]));
							teamM.addPlayerToTeam(p, args[0].toLowerCase());
							return true;
						}
						else{
							ms.send(ChatColor.RED, p, "No room left on team " + args[0]);
							return true;
						}
						
					}
					else{
						ms.send(ChatColor.RED, p, "You are not invited to team " + args[0].toLowerCase());
						return true;
					}
				}
				
				else{
					ms.send(ChatColor.RED, p, "Team " + args[0].toLowerCase() + " is not a valid team");
					return true;
				}
			}
		}
		
		
		return false;
	}

}
