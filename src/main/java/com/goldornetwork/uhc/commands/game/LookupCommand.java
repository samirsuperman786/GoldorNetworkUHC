package com.goldornetwork.uhc.commands.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class LookupCommand extends UHCCommand{


	private TeamManager teamM;


	public LookupCommand(TeamManager teamM) {
		super("lookup", "[team/player]");
		this.teamM=teamM;
	}

	@Override
	public boolean execute(Player sender, String[] args) {

		if(args.length==1){
			if(teamM.isActiveTeam(args[0])){
				MessageSender.send(sender, getFormat(args[0]));
				return true;
			}
			else if(PlayerUtils.getOfflinePlayer(args[0]).hasPlayedBefore() || PlayerUtils.getOfflinePlayer(args[0]).isOnline()){
				OfflinePlayer target = PlayerUtils.getOfflinePlayer(args[0]);
				if(teamM.isPlayerInGame(target.getUniqueId())){
					MessageSender.send(sender, getFormat(teamM.getTeamOfPlayer(target.getUniqueId())));
					return true;
				}
				else{
					MessageSender.send(sender, ChatColor.RED + "Player " + target.getName() + " is not on a team.");
					return true;
				}
			}
			else{
				MessageSender.send(sender, ChatColor.RED + args[0] + " is not an active team or a player.");
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
			for(Player online : Bukkit.getOnlinePlayers()){
				toReturn.add(online.getName());
			}
			return toReturn;
		}
		return null;
	}

	private List<String> getFormat(String teamToFormat){
		List<String> toReturn = new LinkedList<String>();
		List<String> online = new ArrayList<String>();
		List<String> offline = new ArrayList<String>();
		String team = teamM.getTeamNameProper(teamToFormat);
		String header = teamM.getColorOfTeam(team) + "Team " + team + ": ";
		toReturn.add(header);
		for(UUID u : teamM.getPlayersOnATeam(team)){

			if(Bukkit.getOfflinePlayer(u).isOnline()){
				Player target = Bukkit.getPlayer(u);
				double health = target.getHealth();
				double roundedHealth = .5*(Math.round(health/.5));

				online.add(PlayerUtils.getPrefix(target) + ChatColor.GREEN + "\u25CF" + teamM.getColorOfPlayer(target.getUniqueId())
				+ target.getName() + ChatColor.WHITE + ": " + ChatColor.RED + roundedHealth + "\u2665");

			}
			else{
				OfflinePlayer target = Bukkit.getOfflinePlayer(u);
				offline.add(PlayerUtils.getPrefix(target) + ChatColor.RED + "\u25CF" + teamM.getColorOfPlayer(target.getUniqueId()) + target.getName());
			}
		}
		toReturn.addAll(online);
		toReturn.addAll(offline);

		return toReturn;
	}

}
