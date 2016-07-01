package com.goldornetwork.uhc.commands.game;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.chat.ChatManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class ReplyCommand extends UHCCommand{


	private ChatManager chatM;


	public ReplyCommand(ChatManager chatM) {
		super("reply", "[message]");
		this.chatM=chatM;
	}

	@Override
	public boolean execute(Player sender, String[] args){

		if(args.length==0){
			return false;
		}
		else if(chatM.hasRecentlyMessaged(sender)==false){
			MessageSender.send(sender, ChatColor.RED + "No one to reply to.");
			return true;
		}
		else if(args.length>0){

			if(Bukkit.getOfflinePlayer(chatM.getRecentRecipient(sender)).isOnline()){
				StringBuilder str = new StringBuilder();
				for(int i =0; i<args.length; i++){
					str.append(args[i] + " ");
				}
				String msg = str.toString();
				chatM.reply(sender, msg);
			}
			else{
				MessageSender.send(sender, ChatColor.RED + "Player " + Bukkit.getOfflinePlayer(chatM.getRecentRecipient(sender)).getName()
						+ ChatColor.RED + " is not online.");
			}
			return true;
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
