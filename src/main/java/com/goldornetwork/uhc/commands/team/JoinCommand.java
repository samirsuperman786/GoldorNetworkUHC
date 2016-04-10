package com.goldornetwork.uhc.commands.team;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class JoinCommand extends UHCCommand {

	//instances
	private TeamManager teamM;

	public JoinCommand(TeamManager teamM) {
		super("join", "[team]");
		this.teamM=teamM;
	}
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		if(!(sender instanceof Player)){
			MessageSender.noConsole(sender);
			return true;
		}
		else if(State.getState().equals(State.INGAME)){
			MessageSender.send(ChatColor.RED, p, "Match has already started!");
			return true;
		}
		else if(State.getState().equals(State.OPEN)==false){
			MessageSender.send(ChatColor.RED, p, "You may not join a team.");
			return true;
		}
		else if(teamM.isPlayerInGame(p)){
			MessageSender.send(ChatColor.RED, sender, "You are already on a team");
			return true;
		}

		else if(args.length == 0){
			if(teamM.isFFAEnabled()){
				if(teamM.isFFARoomToJoin()){
					teamM.addPlayerToFFA(p);
					MessageSender.alertMessage(p, ChatColor.GREEN, "You have joined the FFA");
					return true;
				}
				else{
					MessageSender.alertMessage(p, ChatColor.RED, "No room to join sorry!");
					return true;
				}

			}
			else{
				return false;
			}
		}
		else if(args.length==1){
			if(teamM.isTeamsEnabled()){
				if(teamM.isValidTeam(args[0])){
					if(teamM.isPlayerInvitedToTeam(p,args[0].toLowerCase())){
						if(teamM.isTeamRoomToJoin(args[0].toLowerCase())){
							MessageSender.alertMessage(p, ChatColor.GREEN, "You have joined team " + teamM.getColorOfTeam(args[0].toLowerCase()) + args[0]);
							teamM.addPlayerToTeam(p, args[0].toLowerCase());
							teamM.unInvitePlayer(teamM.getTeamOfPlayer(p), p);
							return true;
						}
						else{
							MessageSender.send(ChatColor.RED, p, "No room left on team " + args[0]);
							return true;
						}

					}
					else{
						MessageSender.send(ChatColor.RED, p, "You are not invited to team " + args[0].toLowerCase());
						return true;
					}
				}

				else{
					MessageSender.send(ChatColor.RED, p, "Team " + args[0].toLowerCase() + " is not a valid team");
					return true;
				}
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}


	}
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if(args.length==1){
			return teamM.getActiveTeams();
		}
		return null;
	}

}
