package com.goldornetwork.uhc.commands.staff;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;

public class CancelCommand extends UHCCommand {

	/*
	 * CURRENTLY DISABLED
	 */
	//instances
	private TimerManager timerM;

	public CancelCommand(TimerManager timerM) {
		super("cancel", "");
		this.timerM=timerM;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		 if(State.getState().equals(State.INGAME)){
			MessageSender.send(ChatColor.RED, sender, "Match has already started!");
			return true;
		}
		else if(State.getState().equals(State.OPEN)){
			timerM.cancelMatch();
			MessageSender.send(ChatColor.GREEN, sender, "You have canceled the match!");
			return true;
		}
		else{
			return false;
		}

	}
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if(args.length==1){
			return null;
		}
		return new ArrayList<String>();
	}

}
