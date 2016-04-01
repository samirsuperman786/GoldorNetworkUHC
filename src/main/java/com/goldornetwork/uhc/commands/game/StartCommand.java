package com.goldornetwork.uhc.commands.game;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class StartCommand extends UHCCommand{

	//instances
	private TimerManager timerM;
	
	//storage
	private final int DEFAULTTIMETILLSTART=15*60;
	private final int DEFAULTTIMETILLPVP=40*60;
	
	public StartCommand(TimerManager timerM) {
		super("start", "");
		this.timerM=timerM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		
		if(args.length == 0){
			if(State.getState().equals(State.INGAME)){
				MessageSender.send(ChatColor.RED, sender, "Match has already started!");
				return true;
			}
			else{
				timerM.startMatch(true, DEFAULTTIMETILLSTART, DEFAULTTIMETILLPVP); 
				MessageSender.send(ChatColor.GREEN, sender, "You have started the match!");
				return true;
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
