package com.goldornetwork.uhc.commands.game;

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
	//storage
	private final int DEFAULTTIMETILLSTART=6*60; //change to 15 *60
	private final int DEFAULTTIMETILLPVP=1*30; //change to 40*60
	
	public StartCommand(TimerManager timerM, TeamManager teamM) {
		super("start", "[Teamsize]");
		this.timerM=timerM;
		this.teamM=teamM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		//TODO work on easier start implementation
		
		if(!State.getState().equals(State.NOT_RUNNING)){
			MessageSender.send(ChatColor.RED, sender, "Match has already started!");
			return true;
		}
		else if(args.length==1){
			if(Parser.isInt(args[0])){
				int teamSize = Integer.valueOf(args[0]);
				if(teamSize==1){
					teamM.setupFFA();
					MessageSender.broadcast(ChatColor.GOLD + "FFA has been enabled!");
				}
				else{
					teamM.setupTeams(teamSize);
					MessageSender.broadcast(ChatColor.GOLD + "Teams have been enabled with a size of " + ChatColor.GRAY + teamSize + ChatColor.GOLD + " players per team!");
				}
				timerM.startMatch(DEFAULTTIMETILLSTART, DEFAULTTIMETILLPVP);
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
