package com.goldornetwork.uhc.commands.team;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class CreateCommand extends UHCCommand{

	//instances
	private TeamManager teamM;
	
	public CreateCommand(TeamManager teamM) {
		super("create", "");
		this.teamM=teamM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;

		if(!(sender instanceof Player)){
			MessageSender.noConsole(sender);
			return true;
		}

		else if(teamM.isPlayerInGame(p.getUniqueId())){
			MessageSender.send(ChatColor.RED, p, "You are already on a team.");
			return true;
		}

		else if(!(State.getState().equals(State.OPEN))){
			MessageSender.send(ChatColor.RED, p, "You may only join during the open phase.");
			return true;
		}
		else if(args.length==0){
			if(teamM.isTeamsEnabled()){
				if(teamM.isPlayerAnObserver(p.getUniqueId())){
					teamM.removePlayerFromObservers(p);
				}
				if(teamM.createRandomTeam(p)==true){
					MessageSender.alertMessage(p, ChatColor.GREEN, "You have created a team, please use /invite");
				}
				else{
					MessageSender.alertMessage(p, ChatColor.RED, "Teams are full.");
				}
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
		return null;
	}

}
