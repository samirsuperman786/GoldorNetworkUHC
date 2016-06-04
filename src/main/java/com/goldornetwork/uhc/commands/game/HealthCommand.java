package com.goldornetwork.uhc.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class HealthCommand extends UHCCommand {

	private TeamManager teamM;
	public HealthCommand(TeamManager teamM) {
		super("health","[player]");
		this.teamM=teamM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		
		if(args.length == 0){
			return false;
		}
		else if(args.length==1){
			if(teamM.isPlayerOnline(args[0])==false){
				MessageSender.send(ChatColor.RED, sender, "Player " + args[0].toLowerCase() + " is not online!");
				return true;
			}
			
			else if(teamM.isPlayerInGame(Bukkit.getPlayer(args[0]))==false){
				MessageSender.send(ChatColor.RED, sender, args[0] + " is not in game.");
				return true;
			}
			else{
				Player target = Bukkit.getPlayer(args[0]);
				MessageSender.send(sender, teamM.getColorOfPlayer(target) + target.getName() + ChatColor.WHITE + ": " + ChatColor.RED + target.getHealth() + "\u2665");
				return true;
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
			for(Player all : Bukkit.getOnlinePlayers()){
				toReturn.add(all.getName());
			}
		}
		return toReturn;
	}

}
