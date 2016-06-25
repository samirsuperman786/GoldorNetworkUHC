package com.goldornetwork.uhc.commands.team;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.chat.TeamInteraction;
import com.goldornetwork.uhc.utils.MessageSender;

public class PMCoordsCommand extends UHCCommand {


	private TeamManager teamM;
	private TeamInteraction teamI;


	public PMCoordsCommand(TeamManager teamM, TeamInteraction teamI) {
		super("pmc", "");
		this.teamM=teamM;
		this.teamI=teamI;
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		if(teamM.isTeamsEnabled()){

			if(teamM.isPlayerInGame(sender.getUniqueId())){
				if(teamM.isPlayerOnTeam(sender.getUniqueId())){
					teamI.sendCoords(teamM.getTeamOfPlayer(sender.getUniqueId()), sender);
				}
				else{
					MessageSender.send(sender, ChatColor.RED + "You are not on a team.");
				}
			}
			else{
				MessageSender.send(sender, ChatColor.RED + "You are not in the game.");
			}
			return true;
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
