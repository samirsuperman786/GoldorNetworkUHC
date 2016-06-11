package com.goldornetwork.uhc.commands.game;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.world.listeners.team.ChatManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class ReplyCommand extends UHCCommand{

	private TeamManager teamM;
	private ChatManager chatM;

	public ReplyCommand(ChatManager chatM, TeamManager teamM) {
		super("reply", "[message]");
		this.chatM=chatM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player messenger = (Player) sender;
		if(!(sender instanceof Player)){
			return true;
		}
		else if(args.length==0){
			return false;
		}
		else if(chatM.hasRecentlyMessaged(messenger)==false){
			MessageSender.send(ChatColor.RED, messenger, "No one to reply to.");
			return true;
		}
		else if(args.length>0){
			if(Bukkit.getOfflinePlayer(chatM.getRecentRecipient(messenger)).isOnline()){
				StringBuilder str = new StringBuilder();
				for(int i =0; i<args.length; i++){
					str.append(args[i] + " ");
				}
				String msg = str.toString();
				chatM.reply(messenger, msg);
			}
			else{
				MessageSender.send(ChatColor.RED, messenger, "Player " + Bukkit.getOfflinePlayer(chatM.getRecentRecipient(messenger)).getName() + ChatColor.RED + " is not online.");
			}

			return true;

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
