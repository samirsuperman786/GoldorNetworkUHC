package com.goldornetwork.uhc.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class HealthCommand extends UHCCommand {

	
	private TeamManager teamM;

	
	public HealthCommand(TeamManager teamM) {
		super("health", "[player]");
		this.teamM = teamM;
	}

	@Override
	public boolean execute(Player sender, String[] args) {

		if(args.length == 0){
			return false;
		}

		else if(args.length == 1){
			if(PlayerUtils.isPlayerOnline(args[0]) == false){
				MessageSender.send(sender, ChatColor.RED + "Player " + args[0].toLowerCase() + " is not online.");
				return true;
			}
			else if(teamM.isPlayerInGame(PlayerUtils.getPlayer(args[0]).getUniqueId()) == false){
				MessageSender.send(sender, ChatColor.RED + args[0] + " is not in game.");
				return true;
			}
			else{
				Player target = PlayerUtils.getPlayer(args[0]);
				MessageSender.send(sender, teamM.getColorOfPlayer(target.getUniqueId()) + target.getName()
				+ ChatColor.WHITE + ": " + ChatColor.RED + target.getHealth() + "\u2665");
				return true;
			}
		}

		else{
			return false;
		}

	}

	@Override
	public List<String> tabComplete(Player sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		if(args.length == 1) {
			for(Player all : Bukkit.getOnlinePlayers()){
				toReturn.add(all.getName());
			}
		}
		return toReturn;
	}

}
