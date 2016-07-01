package com.goldornetwork.uhc.commands.team;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class JoinCommand extends UHCCommand {

	
	private TeamManager teamM;

	
	public JoinCommand(TeamManager teamM) {
		super("join", "[team]");
		this.teamM=teamM;
	}
	@Override
	public boolean execute(Player sender, String[] args) {
		if(State.getState().equals(State.INGAME)){
			MessageSender.send(sender, ChatColor.RED + "Match has already started.");
			return true;
		}
		else if(State.getState().equals(State.OPEN)==false){
			MessageSender.send(sender, ChatColor.RED + "You may not join a team.");
			return true;
		}
		else if(teamM.isPlayerInGame(sender.getUniqueId())){
			MessageSender.send(sender, ChatColor.RED + "You are already on a team.");
			return true;
		}
		else if(args.length==1){

			if(teamM.isTeamsEnabled()){
				String teamToJoin = args[0];
				if(teamM.isValidTeam(teamToJoin)){
					if(teamM.isPlayerInvitedToTeam(sender,teamToJoin)){
						if(teamM.isRoomToJoin(args[0].toLowerCase())){
							if(teamM.isPlayerAnObserver(sender.getUniqueId())){
								teamM.removePlayerFromObservers(sender);
							}
							MessageSender.alertMessage(sender, ChatColor.GREEN + "You have joined team "
							+ teamM.getColorOfTeam(teamToJoin) + teamM.getTeamNameProper(args[0]));
							
							teamM.addPlayerToTeam(sender, args[0].toLowerCase());
							teamM.unInvitePlayer(args[0], sender.getUniqueId());
							return true;
						}
						else{
							MessageSender.send(sender, ChatColor.RED + "No room left on team " + teamM.getColorOfTeam(teamToJoin) + teamM.getTeamNameProper(teamToJoin));
							return true;
						}
					}
					else{
						MessageSender.send(sender, ChatColor.RED + "You are not invited to team " + args[0].toLowerCase());
						return true;
					}
				}
				else{
					MessageSender.send(sender, ChatColor.RED + "Team " + args[0].toLowerCase() + " is not a valid team");
					return true;
				}
			}
			else{
				MessageSender.send(sender, ChatColor.RED + "Teams are not enabled yet.");
				return true;
			}
		}
		else{
			return false;
		}
	}
	@Override
	public List<String> tabComplete(Player sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if(args.length==1){
			for(String teams : teamM.getActiveTeams()){
				String toAdd = teamM.getTeamNameProper(teams);
				toReturn.add(toAdd);
			}
			return toReturn;
		}
		return null;
	}
}
