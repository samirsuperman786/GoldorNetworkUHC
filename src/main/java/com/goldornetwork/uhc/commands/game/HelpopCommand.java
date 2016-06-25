package com.goldornetwork.uhc.commands.game;

import java.util.List;

import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.chat.ChatManager;

public class HelpopCommand extends UHCCommand{

	
	private ChatManager chatM;
	
	
	public HelpopCommand(ChatManager chatM) {
		super("helpop", "[message]");
		this.chatM = chatM;
	}

	@Override
	public boolean execute(Player sender, String[] args) {

		if(args.length==0){
			return false;
		}
		else{
			StringBuilder str = new StringBuilder();
			for(int i =0; i<args.length; i++){
				str.append(args[i] + " ");
			}
			String msg = str.toString();
			chatM.helpop(sender, msg);
			return true;
		}
	}

	@Override
	public List<String> tabComplete(Player sender, String[] args) {
		return null;
	}
	
	
	
}
