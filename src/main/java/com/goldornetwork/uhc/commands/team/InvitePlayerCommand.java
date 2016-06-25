package com.goldornetwork.uhc.commands.team;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class InvitePlayerCommand extends UHCCommand{


	private TeamManager teamM;


	public InvitePlayerCommand(TeamManager teamM) {
		super("invite", "[player]");
		this.teamM=teamM;
	}



	@Override
	public boolean execute(Player sender, String[] args) {
		if(State.getState().equals(State.INGAME)){
			MessageSender.send(sender, ChatColor.RED + "Match has already started.");
			return true;
		}
		else if(teamM.isPlayerInGame(sender.getUniqueId())==false){
			MessageSender.send(sender, ChatColor.RED + "You are not on a team.");
			return true;
		}
		else if(teamM.isPlayerOwner(teamM.getTeamOfPlayer(sender.getUniqueId()), sender.getUniqueId())==false){
			MessageSender.send(sender, ChatColor.RED + "You are not the owner of the team.");
			return true;
		}
		else if(args.length==0){
			MessageSender.send(sender, ChatColor.RED + "Please specify a player.");
			return true;
		}
		else if(PlayerUtils.isPlayerOnline(args[0])==false){
			MessageSender.send(sender, ChatColor.RED + "Player " + args[0].toLowerCase() + " is not online.");
			return true;
		}
		else if(teamM.isPlayerOnTeam(PlayerUtils.getPlayer(args[0]).getUniqueId())){
			MessageSender.send(sender, ChatColor.RED + "Player " + args[0] + " is already on a team.");
			return true;
		}
		else{
			Player target = PlayerUtils.getPlayer(args[0]);
			teamM.invitePlayer(teamM.getTeamOfPlayer(sender.getUniqueId()), target.getUniqueId());
			
			MessageSender.alertMessage(target, ChatColor.GREEN + "You have been invited to team " + teamM.getColorOfPlayer(sender.getUniqueId())
			+ teamM.getTeamNameProper(teamM.getTeamOfPlayer(sender.getUniqueId())) + ChatColor.GREEN + " by " + teamM.getColorOfPlayer(sender.getUniqueId())
			+ sender.getName());
			
			MessageSender.alertMessage(sender, ChatColor.GREEN + "You have invited player " + target.getName());
			return true;
		}
	}

	@Override
	public List<String> tabComplete(Player sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		if(args.length==1){
			for(Player all : Bukkit.getOnlinePlayers()){
				toReturn.add(all.getName());
			}
		}
		return toReturn;
	}



}
