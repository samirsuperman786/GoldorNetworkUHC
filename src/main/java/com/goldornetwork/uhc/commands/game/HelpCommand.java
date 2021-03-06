package com.goldornetwork.uhc.commands.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.utils.MessageSender;

public class HelpCommand extends UHCCommand{

	
	public HelpCommand() {
		super("help", "[query]");
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		List<String> toReturn = new LinkedList<String>();

		if(args.length==0){
			toReturn.add(ChatColor.GOLD + "/help" + ChatColor.AQUA + " [query]");
			toReturn.add(ChatColor.AQUA + "1) " + formatCommand("[team]", "All commands related to team management and creation."));
			toReturn.add(ChatColor.AQUA + "2) " + formatCommand("[game]", "All commands related to general gameplay."));
			toReturn.add(ChatColor.AQUA + "3) " + formatCommand("[staff]", "All staff commands.")); 
			MessageSender.send(sender, toReturn);
			return true;
		}
		else if(args.length==1){
			if(args[0].equalsIgnoreCase("team")){
				toReturn.add(ChatColor.GOLD + "Team Commands: ");
				toReturn.add(formatCommand("/create", "Use this to create a team."));
				toReturn.add(formatCommand("/invite [player]", "Use this to invite a player to your team."));
				toReturn.add(formatCommand("/uninvite [player]", "Use this to revoke an invite."));
				toReturn.add(formatCommand("/join [team]", "Use this to join a team."));
				toReturn.add(formatCommand("/leave", "Use this to leave a team."));
				toReturn.add(formatCommand("/kick [player]", "Use this to kick a player from your team."));
				toReturn.add(formatCommand("/request [player]", "Use this to request a whitelist."));
				toReturn.add(formatCommand("/pmc", "Use this to message your coordinates to your team."));
				toReturn.add(formatCommand("/pmt [message]", "Use this to message only your team."));
				MessageSender.send(sender, toReturn);
				return true;
			}
			else if(args[0].equalsIgnoreCase("game")){
				toReturn.add(ChatColor.GOLD + "Game Commands: ");
				toReturn.add(formatCommand("/helpop [message]", "Use this to message staff."));
				toReturn.add(formatCommand("/info", "Use this to learn the scenarios."));
				toReturn.add(formatCommand("/vote [option]", "Use this to vote when a poll is active."));
				toReturn.add(formatCommand("/health [player]", "Get the health of a player."));
				toReturn.add(formatCommand("/lookup [team/player]", "Use this to lookup a team or a player."));
				toReturn.add(formatCommand("/report [player] [reason]", "Use this to report a player."));
				toReturn.add(formatCommand("/pm [player] [message]", "Use this to message a player."));
				toReturn.add(formatCommand("/reply [message]", "Use this to reply to a player."));
				toReturn.add(formatCommand("/tp [player]", "Teleport to a player."));
				MessageSender.send(sender, toReturn);
				return true;
			}
			else if(args[0].equalsIgnoreCase("staff")){
				toReturn.add(ChatColor.GOLD + "Staff Commands: ");
				toReturn.add(formatCommand("/start [teamsize]", "Use this to start a match."));
				toReturn.add(formatCommand("/warn [player] [reason]", "Use to warn a player."));
				toReturn.add(formatCommand("/mute [player] [reason]", "Use this to mute a player."));
				toReturn.add(formatCommand("/unmute [player]", "Use this to unmute a player."));
				toReturn.add(formatCommand("/playerban [player] [reason]", "Use this to permanently ban a player."));
				toReturn.add(formatCommand("/freeze [player]", "Use this to freeze a player."));
				
				MessageSender.send(sender, toReturn);
				return true;
			}
			else{
				return false;
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
			toReturn.add("team");
			toReturn.add("game");
			toReturn.add("staff");
		}
		return toReturn;
	}

	private String formatCommand(String commandName, String description){
		return ChatColor.AQUA + commandName + ": " + ChatColor.DARK_AQUA + description;
	}

}
