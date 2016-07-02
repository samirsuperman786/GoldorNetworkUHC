package com.goldornetwork.uhc.commands.game;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.VoteManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.Parser;

public class VoteCommand extends UHCCommand{

	
	private VoteManager voteM;
	
	
	public VoteCommand(VoteManager voteM) {
		super("vote", "[option]");
		this.voteM=voteM;
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		if(State.getState().equals(State.OPEN)){
			if(voteM.isActive()==false){
				MessageSender.send(sender, ChatColor.RED + "No poll currently open.");
				return true;
			}
			else if(args.length==1){
				
				if(Parser.isInt(args[0])){
					int input = Integer.valueOf(args[0]);
					if(voteM.isValidOption(input)){
						
						if(voteM.hasVoted(sender.getUniqueId())){
							voteM.changeVote(sender.getUniqueId(), input);
							MessageSender.send(sender, ChatColor.GREEN + "Changed vote to option " + ChatColor.GOLD + input);
							return true;
						}
						else{
							voteM.addVote(sender.getUniqueId(), input);
							MessageSender.send(sender, ChatColor.GREEN + "You have voted for option " + ChatColor.GOLD + input);
							return true;
						}
					}
					else{
						MessageSender.send(sender, ChatColor.RED + args[0] + " is not a valid option.");
						return true;
					}
				}
				else{
					MessageSender.send(sender, ChatColor.RED + "Please use a number.");
					return false;
				}
			}
			else{
				return false;
			}
		}
		else{
			MessageSender.send(sender, ChatColor.RED + "No poll currently open.");
			return true;
		}
	}

	@Override
	public List<String> tabComplete(Player sender, String[] args) {
		List<String> toReturn = new LinkedList<String>();
		
		for(int i = 1; i<(voteM.getNumberOfOptions()+1); i++){
			toReturn.add(i + "");
		}
		return toReturn;
	}

}
