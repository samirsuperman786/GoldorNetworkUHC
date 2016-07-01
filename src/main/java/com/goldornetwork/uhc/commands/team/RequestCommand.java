package com.goldornetwork.uhc.commands.team;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.managers.world.UHCWhitelist;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class RequestCommand extends UHCCommand{

	
	private TeamManager teamM;
	private UHCWhitelist uhcWhitelist;
	
	public RequestCommand(TeamManager teamM, UHCWhitelist uhcWhitelist) {
		super("request", "[player]");
		this.teamM=teamM;
		this.uhcWhitelist=uhcWhitelist;
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
		else if(PlayerUtils.isPlayerOnline(args[0])){
			MessageSender.send(sender, ChatColor.RED + "Player " + args[0].toLowerCase() + " is already online.");
			return true;
		}
		else if(uhcWhitelist.isWhitelisted(args[0])){
			MessageSender.send(sender, ChatColor.RED + "Player " + args[0] + " is already whitelisted.");
			return true;
		}
		else if(teamM.getPlayersOnATeam(teamM.getTeamOfPlayer(sender.getUniqueId())).size()>=teamM.getTeamSize()){
			MessageSender.send(sender, ChatColor.RED + "Your team is full, you may not request a whitelist.");
			return true;
		}
		else if(uhcWhitelist.isOnCooldown(teamM.getTeamOfPlayer(sender.getUniqueId()))){
			MessageSender.send(sender, ChatColor.RED + "You are cooldown for that command.");
			return true;
		}
		else if(args.length==1){
			String target = args[0];
			uhcWhitelist.requestWhitelist(sender, teamM.getTeamOfPlayer(sender.getUniqueId()), target);
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
