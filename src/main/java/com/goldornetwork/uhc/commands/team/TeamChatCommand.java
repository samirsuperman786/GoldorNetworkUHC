package com.goldornetwork.uhc.commands.team;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.listeners.team.TeamInteraction;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class TeamChatCommand extends UHCCommand{

	private TeamInteraction teamI;
	private TeamManager teamM;
	
	public TeamChatCommand(TeamManager teamM, TeamInteraction teamI) {
		super("pmt", "[message]");
		this.teamI=teamI;
		this.teamM=teamM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		if(!(sender instanceof Player)){
			MessageSender.noConsole(sender);
			return true;
		}
		else if(teamM.isTeamsEnabled()){
			if(teamM.isPlayerInGame(p)){
				if(teamM.isPlayerOnTeam(p)){
					if(args.length==0){
						return false;
					}
					else{
						StringBuilder str = new StringBuilder();
						for(int i =0; i<args.length; i++){
							str.append(args[i] + " ");
						}
						String msg = str.toString();
						teamI.sendMsgTeamates(teamM.getTeamOfPlayer(p), p, msg);
						return true;
					}
					
				}
				else{
					MessageSender.send(ChatColor.RED, p, "You are not on a team!");
					return true;
				}
			}
			else{
				MessageSender.send(ChatColor.RED, p, "You are not in the game!");
				return true;
			}
		}
		else if(teamM.isTeamsEnabled()==false){
			MessageSender.send(ChatColor.RED, p, "Teams are not enabled!");
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
