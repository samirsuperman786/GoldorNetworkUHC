package com.goldornetwork.uhc.commands.team;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class CreateCommand extends UHCCommand{


	private TeamManager teamM;

	
	public CreateCommand(TeamManager teamM) {
		super("create", "");
		this.teamM=teamM;
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		if(teamM.isPlayerInGame(sender.getUniqueId())){
			MessageSender.send(sender, ChatColor.RED + "You are already on a team.");
			return true;
		}
		else if(!(State.getState().equals(State.OPEN))){
			MessageSender.send(sender, ChatColor.RED + "You may only join during the open phase.");
			return true;
		}
		else if(args.length==0){
			if(teamM.isTeamsEnabled()){
				if(teamM.isPlayerAnObserver(sender.getUniqueId())){
					teamM.removePlayerFromObservers(sender);
				}
				if(teamM.createRandomTeam(sender)==true){
					MessageSender.alertMessage(sender, ChatColor.GREEN + "You have created a team, please use /invite");
				}
				else{
					MessageSender.alertMessage(sender, ChatColor.RED + "Teams are full.");
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
	public List<String> tabComplete(Player sender, String[] args) {
		return null;
	}

}
