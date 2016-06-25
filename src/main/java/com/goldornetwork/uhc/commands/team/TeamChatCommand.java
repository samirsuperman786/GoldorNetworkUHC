package com.goldornetwork.uhc.commands.team;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.chat.TeamInteraction;
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
	public boolean execute(Player sender, String[] args) {
		if(teamM.isTeamsEnabled()){

			if(teamM.isPlayerInGame(sender.getUniqueId())){
				if(teamM.isPlayerOnTeam(sender.getUniqueId())){
					if(args.length==0){
						return false;
					}
					else{
						StringBuilder str = new StringBuilder();
						for(int i =0; i<args.length; i++){
							str.append(args[i] + " ");
						}
						String msg = str.toString();
						teamI.sendMsgTeamates(teamM.getTeamOfPlayer(sender.getUniqueId()), sender, msg);
						return true;
					}
				}
				else{
					MessageSender.send(sender, ChatColor.RED + "You are not on a team.");
					return true;
				}
			}
			else{
				MessageSender.send(sender, ChatColor.RED + "You are not in the game.");
				return true;
			}
		}
		else if(teamM.isTeamsEnabled()==false){
			MessageSender.send(sender, ChatColor.RED + "Teams are not enabled.");
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public List<String> tabComplete(Player sender, String[] args) {
		return null;
	}
}
