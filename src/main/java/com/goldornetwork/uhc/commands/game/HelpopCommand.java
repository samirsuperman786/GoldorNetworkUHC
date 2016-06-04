package com.goldornetwork.uhc.commands.game;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class HelpopCommand extends UHCCommand{

	private TeamManager teamM;
	public HelpopCommand(TeamManager teamM) {
		super("helpop", "[message]");
		this.teamM=teamM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player target = (Player) sender;
		if(!(sender instanceof Player)){
			MessageSender.noConsole(sender);
			return true;
		}
		else if(args.length==0){
			return false;
		}
		else{
			StringBuilder str = new StringBuilder();
			for(int i =0; i<args.length; i++){
				str.append(args[i] + " ");
			}
			String msg = str.toString();
			MessageSender.sendToOPS(teamM.getColorOfPlayer(target) + target.getName() + ChatColor.WHITE + ": " + msg);
			return true;
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null;
	}
	
	
	
}
