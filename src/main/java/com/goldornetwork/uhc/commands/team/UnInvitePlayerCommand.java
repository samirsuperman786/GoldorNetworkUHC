package com.goldornetwork.uhc.commands.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class UnInvitePlayerCommand extends UHCCommand {

	//instances
	private TeamManager teamM;

	public UnInvitePlayerCommand(TeamManager teamM) {
		super("uninvite", "[player]");
		this.teamM=teamM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		if(!(sender instanceof Player)){
			MessageSender.noConsole(sender);
			return true;
		}

		else if(teamM.isTeamsEnabled()==false){
			MessageSender.send(ChatColor.RED, p, "Teams are not enabled!");
			return true;
		}

		else if(State.getState().equals(State.INGAME)){
			MessageSender.send(ChatColor.RED, p, "Match has already started!");
			return true;
		}
		else if(teamM.isPlayerInGame(p)==false){
			MessageSender.send(ChatColor.RED, p, "You are not on a team!");
			return true;
		}

		else if(teamM.getOwnerOfTeam(teamM.getTeamOfPlayer(p))!=p.getUniqueId()){
			MessageSender.send(ChatColor.RED, p, "You are not the owner of the team!");
			return true;
		}
		else if(args.length==0){
			MessageSender.send(ChatColor.RED, p, "Please specify a player!");
			return true;
		}

		else if(teamM.isPlayerOnline(args[0])==false){
			MessageSender.send(ChatColor.RED, p, "Player " + args[0].toLowerCase() + " is not online!");
			return true;
		}
		else if(teamM.isPlayerOnTeam(Bukkit.getServer().getPlayer(args[0]))){
			MessageSender.send(ChatColor.RED, p, "Player " + args[0] + " is already on a team!");
			return true;
		}
		else{
			if(teamM.isPlayerInvitedToTeam(Bukkit.getServer().getPlayer(args[0]), teamM.getTeamOfPlayer(p))){
				teamM.unInvitePlayer(teamM.getTeamOfPlayer(p), Bukkit.getServer().getPlayer(args[0]));
				MessageSender.alertMessage(p, ChatColor.GREEN, "You have uninvited " + Bukkit.getServer().getPlayer(args[0]).getName());
				MessageSender.alertMessage(Bukkit.getServer().getPlayer(args[0]), ChatColor.RED, "You have been uninvited to team " + teamM.getColorOfPlayer(p) + teamM.getTeamNameProper(teamM.getTeamOfPlayer(p)) + ChatColor.RED + " by " + teamM.getColorOfPlayer(p) + p.getName());
				return true;
			}
			else{
				MessageSender.send(ChatColor.RED, p, "You have not invited player " + args[0]);
				return false;
			}
		}

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		Player p = (Player) sender;
		if(args.length==1 && teamM.isPlayerInGame(p)){
			for(UUID u : teamM.getPlayersOnATeam(teamM.getTeamOfPlayer(p))){
				if(Bukkit.getOfflinePlayer(u).isOnline()){
					toReturn.add(Bukkit.getServer().getPlayer(u).getName());
				}
			}
		}
		return toReturn;
	}

}
