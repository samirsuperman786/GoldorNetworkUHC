package com.goldornetwork.uhc.commands.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.utils.MessageSender;

public class HelpCommand extends UHCCommand{

	public HelpCommand() {
		super("help", "[query]");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		List<String> toReturn = new LinkedList<String>();
		
		if(args.length==0){
			toReturn.add(ChatColor.GOLD + "/help" + ChatColor.AQUA + " [query]");
			toReturn.add(ChatColor.AQUA + "1) " + formatCommand("[team]", "All commands related to team management and creation."));
			toReturn.add(ChatColor.AQUA + "2) " + formatCommand("[game]", "All commands related to general gameplay."));
			toReturn.add(ChatColor.AQUA + "3) " + formatCommand("[staff]", "All staff commands.")); 
			MessageSender.send(toReturn, sender);
			return true;
		}
		else if(args.length==1){
			if(args[0].equalsIgnoreCase("team")){
				toReturn.add(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Team Commands");
				toReturn.add(formatCommand("/create", "Use this to create a team."));
				toReturn.add(formatCommand("/invite [player]", "Use this to invite a player to your team."));
				toReturn.add(formatCommand("/uninvite [player]", "Use this to un-invite a player from your team."));
				toReturn.add(formatCommand("/join [team]", "Use this to join a team."));
				toReturn.add(formatCommand("/leave", "Use this to leave a team or disband it if you are the owner."));
				toReturn.add(formatCommand("/pmc", "Use this to message your coordinates to your team."));
				toReturn.add(formatCommand("/pmt [message]", "Use this to message only your team."));
				MessageSender.send(toReturn, sender);
				return true;
			}
			else if(args[0].equalsIgnoreCase("game")){
				toReturn.add(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Game Commands");
				toReturn.add(formatCommand("/helpop [message]", "Use this to message staff."));
				toReturn.add(formatCommand("/info", "Use this to learn the gamemodes."));
				toReturn.add(formatCommand("/vote [option]", "Use this to vote when a poll is active."));
				toReturn.add(formatCommand("/health [player]", "Get the health of a player."));
				toReturn.add(formatCommand("/lookup [team]", "Use this to lookup a team."));
				MessageSender.send(toReturn, sender);
				return true;
			}
			else if(args[0].equalsIgnoreCase("staff")){
				toReturn.add(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Staff Commands");
				toReturn.add(formatCommand("/playerban [player]", "Use this to permanently ban a player."));
				toReturn.add(formatCommand("/start [teamsize]", "Use this to start a match."));
				MessageSender.send(toReturn, sender);
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
	public List<String> tabComplete(CommandSender sender, String[] args) {
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
