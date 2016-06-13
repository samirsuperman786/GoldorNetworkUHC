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
		else if(teamM.isPlayerInGame(p.getUniqueId())==false){
			MessageSender.send(ChatColor.RED, p, "You are not on a team!");
			return true;
		}

		else if(teamM.getOwnerOfTeam(teamM.getTeamOfPlayer(p.getUniqueId()))!=p.getUniqueId()){
			MessageSender.send(ChatColor.RED, p, "You are not the owner of the team!");
			return true;
		}
		else if(args.length==0){
			MessageSender.send(ChatColor.RED, p, "Please specify a player!");
			return true;
		}

		else if(teamM.isPlayerOnTeam(Bukkit.getServer().getOfflinePlayer(args[0]).getUniqueId())){
			teamM.unInvitePlayer(teamM.getTeamOfPlayer(p.getUniqueId()), Bukkit.getServer().getOfflinePlayer(args[0]).getUniqueId());
			MessageSender.alertMessage(p, ChatColor.GREEN, "You have uninvited " + Bukkit.getServer().getOfflinePlayer(args[0]).getName());
			return true;
		}
		else{
			if(teamM.isPlayerInvitedToTeam(Bukkit.getServer().getOfflinePlayer(args[0]), teamM.getTeamOfPlayer(p.getUniqueId()))){
				teamM.unInvitePlayer(teamM.getTeamOfPlayer(p.getUniqueId()), Bukkit.getServer().getOfflinePlayer(args[0]).getUniqueId());
				MessageSender.alertMessage(p, ChatColor.GREEN, "You have uninvited " + Bukkit.getServer().getOfflinePlayer(args[0]).getName());
				if(Bukkit.getServer().getOfflinePlayer(args[0]).isOnline()){
					MessageSender.alertMessage(Bukkit.getServer().getPlayer(args[0]), ChatColor.RED, "You have been uninvited to team " + teamM.getColorOfPlayer(p.getUniqueId()) + teamM.getTeamNameProper(teamM.getTeamOfPlayer(p.getUniqueId())) + ChatColor.RED + " by " + teamM.getColorOfPlayer(p.getUniqueId()) + p.getName());
				}
				return true;
			}
			else{
				MessageSender.send(ChatColor.RED, p, "You have not invited player " + args[0]);
				return true;
			}
		}

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		Player p = (Player) sender;
		if(args.length==1 && teamM.isPlayerInGame(p.getUniqueId())){
			for(UUID u : teamM.getInvitedPlayers(teamM.getTeamOfPlayer(p.getUniqueId()))){
				toReturn.add(Bukkit.getServer().getOfflinePlayer(u).getName());

			}
		}
		return toReturn;
	}

}
