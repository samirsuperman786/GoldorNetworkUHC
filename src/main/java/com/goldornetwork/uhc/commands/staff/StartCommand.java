package com.goldornetwork.uhc.commands.staff;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.Parser;

public class StartCommand extends UHCCommand{

	//instances
	private TimerManager timerM;
	private TeamManager teamM;

	public StartCommand(TimerManager timerM, TeamManager teamM) {
		super("start", "[Teamsize]");
		this.timerM=timerM;
		this.teamM=teamM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		//TODO work on easier start implementation

		if(!State.getState().equals(State.NOT_RUNNING)){
			MessageSender.send(ChatColor.RED, sender, "Match has already started.");
			return true;
		}
		else if(args.length==1){
			if(Parser.isInt(args[0])){
				int teamSize = Integer.valueOf(args[0]);
				teamM.setupTeams(teamSize);
				timerM.startMatch();
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
