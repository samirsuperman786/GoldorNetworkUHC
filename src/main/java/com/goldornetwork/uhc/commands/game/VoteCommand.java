package com.goldornetwork.uhc.commands.game;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.VoteManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.Parser;

public class VoteCommand extends UHCCommand{

	private VoteManager voteM;
	/*
	 * TODO check if poll is currently running
	 */
	public VoteCommand(VoteManager voteM) {
		super("vote", "[option]");
		this.voteM=voteM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			MessageSender.noConsole(sender);
			return true;
		}
		else if(State.getState().equals(State.OPEN)){
			Player p = (Player) sender;
			if(voteM.isActive()==false){
				MessageSender.send(ChatColor.RED, p, "No poll currently open.");
				return true;
			}
			else if(args.length==1){
				if(Parser.isInt(args[0])){
					int input = Integer.valueOf(args[0]);
					if(voteM.isValidOption(input)){
						if(voteM.hasVoted(p)){
							MessageSender.send(ChatColor.RED, p, "You have already voted.");
							return true;
						}
						voteM.addVote(p, input);
						MessageSender.send(ChatColor.GREEN, p, "You have voted for option " + ChatColor.GOLD + input);
						return true;
					}
					else{
						MessageSender.send(ChatColor.RED, sender, args[0] + " is not a valid option.");
						return true;
					}
				}
				else{
					MessageSender.send(ChatColor.RED, sender, "Please use a number.");
					return false;
				}
			}
			else{
				return false;
			}
		}
		else{
			MessageSender.send(ChatColor.RED, sender, "No poll currently open.");
			return true;
		}
		
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new LinkedList<String>();
		
		for(int i = 0; i<voteM.getNumberOfOptions(); i++){
			toReturn.add(i + "");
		}
		return toReturn;
	}

}
