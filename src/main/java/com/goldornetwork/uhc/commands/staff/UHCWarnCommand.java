package com.goldornetwork.uhc.commands.staff;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.world.UHCWarn;
import com.goldornetwork.uhc.utils.MessageSender;

public class UHCWarnCommand extends UHCCommand{

	private UHCWarn uhcWarn;
	
	public UHCWarnCommand(UHCWarn uhcWarn) {
		super("warn", "player");
		this.uhcWarn=uhcWarn;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player banner = (Player) sender;
		if(!(sender instanceof Player)){
			//TODO add console ban capabilities
			return true;
		}
		else if(args.length==0){
			MessageSender.send(ChatColor.RED, banner, "Please specify a player!");
			return true;
		}

		else if(Bukkit.getOfflinePlayer(args[0]).isOnline()==false){
			MessageSender.send(ChatColor.RED, banner, "Player " + args[0].toLowerCase() + " is not online!");
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
			uhcWarn.warnPlayer(target, msg);
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
