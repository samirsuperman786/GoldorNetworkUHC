package com.goldornetwork.uhc.commands.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class LookupCommand extends UHCCommand{

	private TeamManager teamM;

	public LookupCommand(TeamManager teamM) {
		super("lookup", "[team]");
		this.teamM=teamM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {

		if(args.length==1){
			if(teamM.isActiveTeam(args[0])){
				List<String> toReturn = new LinkedList<String>();
				String team = teamM.getTeamNameProper(args[0]);
				String header = "Team " + teamM.getColorOfTeam(team) + team;
				toReturn.add(header);
				for(UUID u : teamM.getPlayersOnATeam(team)){
					if(Bukkit.getOfflinePlayer(u).isOnline()){
						Player target = Bukkit.getPlayer(u);
						toReturn.add(PlayerUtils.getPrefix(target) + ChatColor.GREEN + "\u25CF" + teamM.getColorOfPlayer(target) + target.getName() + ChatColor.WHITE + ": " + ChatColor.RED + target.getHealth() + "\u2665");
					}
					else{
						OfflinePlayer target = Bukkit.getOfflinePlayer(u);
						toReturn.add(PlayerUtils.getPrefix(target) + ChatColor.RED + "\u25CF" + teamM.getColorOfPlayer(target) + target.getName());
					}
				}
				MessageSender.send(toReturn, sender);
				return true;
			}
			else{
				MessageSender.send(sender, ChatColor.RED + args[0] + " is not an active team.");
				return true;
			}
		}
		else{
			return false;
		}

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
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
