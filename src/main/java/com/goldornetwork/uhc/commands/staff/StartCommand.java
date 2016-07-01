package com.goldornetwork.uhc.commands.staff;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.Parser;

public class StartCommand extends UHCCommand{

	
	private TimerManager timerM;
	private TeamManager teamM;

	
	public StartCommand(TimerManager timerM, TeamManager teamM) {
		super("start", "[Teamsize]");
		this.timerM=timerM;
		this.teamM=teamM;
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		if(State.getState().equals(State.OPEN)){
			if(args.length==1){
				if(Parser.isInt(args[0])){
					teamM.setTeamSize(Integer.valueOf(args[0]));
					MessageSender.send(sender, ChatColor.GREEN + "Changed team size to " + ChatColor.GRAY + args[0]);
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
		else if(!State.getState().equals(State.NOT_RUNNING)){
			MessageSender.send(sender, ChatColor.RED + "Can only start the match when the match is not running.");
			return true;
		}
		else if(args.length==1){
			if(Parser.isInt(args[0])){
				int teamSize = Integer.valueOf(args[0]);
				teamM.setupTeams(teamSize);
				timerM.startMatch(sender);
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
