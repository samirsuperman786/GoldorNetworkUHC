package com.goldornetwork.uhc.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.world.listeners.team.ChatManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class ReportCommand extends UHCCommand{

	private ChatManager chatM;
	public ReportCommand(ChatManager chatM) {
		super("report", "[player] [reason]");
		this.chatM=chatM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player messenger = (Player) sender;
		if(!(sender instanceof Player)){
			return true;
		}
		else if(args.length==0){
			MessageSender.send(ChatColor.RED, messenger, "Please specify a player!");
			return true;
		}
		else if(Bukkit.getOfflinePlayer(args[0]).isOnline()==false){
			MessageSender.send(ChatColor.RED, messenger, "Player " + args[0].toLowerCase() + " is not online!");
			return true;
		}
		else if(args.length<=1){
			return false;
		}
		else{
			Player target = Bukkit.getPlayerExact(args[0]);
			StringBuilder str = new StringBuilder();
			for(int i =1; i<args.length; i++){
				str.append(args[i] + " ");
			}
			String msg = str.toString();
			chatM.report(messenger, target, msg);
			return true;
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		if(args.length==1){
			for(Player all : Bukkit.getOnlinePlayers()){
				toReturn.add(all.getName());
			}
		}
		return toReturn;
	}

}
