package com.goldornetwork.uhc.commands.team;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.world.listeners.team.TeamInteraction;
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
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		if(!(sender instanceof Player)){
			MessageSender.noConsole(sender);
			return true;
		}
		else if(teamM.isTeamsEnabled()){
			if(teamM.isPlayerInGame(p.getUniqueId())){
				if(teamM.isPlayerOnTeam(p.getUniqueId())){
					teamI.sendCoords(teamM.getTeamOfPlayer(p.getUniqueId()), p);
				}
				else{
					MessageSender.send(ChatColor.RED, p, "You are not on a team!");
				}
			}
			else{
				MessageSender.send(ChatColor.RED, p, "You are not in the game!");
			}
			return true;
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
		return null;
	}
	
}
