package com.goldornetwork.uhc.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class InvitePlayerCommand implements CommandExecutor{

	//instances
	private TeamManager teamM;
	private TimerManager timerM;

	public InvitePlayerCommand(TeamManager teamM, TimerManager timerM) {
		this.teamM=teamM;
		this.timerM=timerM;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player p = (Player) sender;
		if(!(sender instanceof Player)){
			MessageSender.noConsole(sender);
			return true;
		}

		else if(teamM.isFFAEnabled()==true){
			MessageSender.send(ChatColor.RED, p, "Teams are not enabled!");
			return true;
		}

		else if(timerM.hasCountDownEnded()){
			MessageSender.send(ChatColor.RED, p, "Match has already started!");
			return true;
		}
		else if(teamM.isPlayerInGame(p)==false){
			MessageSender.send(ChatColor.RED, p, "You are not on a team!");
			return true;
		}

		else if(teamM.isPlayerOwner(p)==false){
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
		else if(teamM.isPlayerInGame(Bukkit.getServer().getPlayer(args[0]))==true){
			MessageSender.send(ChatColor.RED, p, "Player " + args[0] + " is already on a team!");
			return true;
		}
		else {
			teamM.invitePlayer(p, Bukkit.getServer().getPlayer(args[0]));
			MessageSender.alertMessage(Bukkit.getServer().getPlayer(args[0]), ChatColor.GREEN, "You have been invited to team " + teamM.getColorOfPlayer(p) + teamM.getTeamOfPlayer(p) + ChatColor.GREEN + " by " + teamM.getColorOfPlayer(p) + p.getName());
			MessageSender.alertMessage(p, ChatColor.GREEN, "You have invited player " + Bukkit.getServer().getPlayer(args[0]).getName());
			return true;
		}





	}



}
