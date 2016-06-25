package com.goldornetwork.uhc.commands.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class UnInvitePlayerCommand extends UHCCommand {


	private TeamManager teamM;


	public UnInvitePlayerCommand(TeamManager teamM) {
		super("uninvite", "[player]");
		this.teamM=teamM;
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		if(teamM.isTeamsEnabled()==false){
			MessageSender.send(sender, ChatColor.RED + "Teams are not enabled.");
			return true;
		}
		else if(State.getState().equals(State.INGAME)){
			MessageSender.send(sender, ChatColor.RED + "Match has already started.");
			return true;
		}
		else if(teamM.isPlayerInGame(sender.getUniqueId())==false){
			MessageSender.send(sender, ChatColor.RED + "You are not on a team.");
			return true;
		}
		else if(teamM.getOwnerOfTeam(teamM.getTeamOfPlayer(sender.getUniqueId()))!=sender.getUniqueId()){
			MessageSender.send(sender, ChatColor.RED + "You are not the owner of the team.");
			return true;
		}
		else if(args.length==0){
			MessageSender.send(sender, ChatColor.RED + "Please specify a player.");
			return true;
		}
		else if(teamM.isPlayerOnTeam(PlayerUtils.getOfflinePlayer(args[0]).getUniqueId())){
			teamM.unInvitePlayer(teamM.getTeamOfPlayer(sender.getUniqueId()), PlayerUtils.getOfflinePlayer(args[0]).getUniqueId());
			MessageSender.alertMessage(sender, ChatColor.GREEN + "You have uninvited " + PlayerUtils.getOfflinePlayer(args[0]).getName());
			return true;
		}
		else{

			if(teamM.isPlayerInvitedToTeam(PlayerUtils.getOfflinePlayer(args[0]), teamM.getTeamOfPlayer(sender.getUniqueId()))){
				teamM.unInvitePlayer(teamM.getTeamOfPlayer(sender.getUniqueId()), PlayerUtils.getOfflinePlayer(args[0]).getUniqueId());
				MessageSender.alertMessage(sender, ChatColor.GREEN + "You have uninvited " + PlayerUtils.getOfflinePlayer(args[0]).getName());
				if(PlayerUtils.isPlayerOnline(args[0])){

					MessageSender.alertMessage(PlayerUtils.getPlayer(args[0]), ChatColor.RED + "You have been uninvited to team "
							+ teamM.getColorOfPlayer(sender.getUniqueId()) + teamM.getTeamNameProper(teamM.getTeamOfPlayer(sender.getUniqueId())) + ChatColor.RED 
							+ " by " + teamM.getColorOfPlayer(sender.getUniqueId()) + sender.getName());
				}
				return true;
			}
			else{
				MessageSender.send(sender, ChatColor.RED + "You have not invited player " + args[0]);
				return true;
			}
		}

	}

	@Override
	public List<String> tabComplete(Player sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		if(args.length==1 && teamM.isPlayerInGame(sender.getUniqueId())){
			for(UUID u : teamM.getInvitedPlayers(teamM.getTeamOfPlayer(sender.getUniqueId()))){
				toReturn.add(Bukkit.getServer().getOfflinePlayer(u).getName());

			}
		}
		return toReturn;
	}
}
