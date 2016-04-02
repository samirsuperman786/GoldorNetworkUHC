package com.goldornetwork.uhc.commands.game;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class StartCommand extends UHCCommand{

	//instances
	private TimerManager timerM;
	private TeamManager teamM;
	//storage
	private final int DEFAULTTIMETILLSTART=15*60;
	private final int DEFAULTTIMETILLPVP=40*60;
	
	public StartCommand(TimerManager timerM, TeamManager teamM) {
		super("start", "[FFA|Teams]");
		this.timerM=timerM;
		this.teamM=teamM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		//TODO work on easier start implementation
		
		if(State.getState().equals(State.OPEN)){
			MessageSender.send(ChatColor.RED, sender, "Match has already started!");
			return true;
		}
		
		else if(args[0].equalsIgnoreCase("FFA")){
			teamM.setupFFA();
			timerM.startMatch(true, DEFAULTTIMETILLSTART, DEFAULTTIMETILLPVP);
			return true;
		}
		else if(args[0].equalsIgnoreCase("TEAMS")){
			if(args.length!=2){
				return false;
			}
			else{
				if(Integer.valueOf(args[2])!=null){
					int teamSize = Integer.valueOf(args[2]);
					teamM.setupTeams(teamSize);
					timerM.startMatch(true, DEFAULTTIMETILLSTART, DEFAULTTIMETILLPVP);
					return true;
				}
				else{
					return false;
				}
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
