package com.goldornetwork.uhc.commands.staff;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.world.UHCBan;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class UHCBanCommand extends UHCCommand{

	
	private UHCBan uhcB;
	
	
	public UHCBanCommand(UHCBan uhcB) {
		super("playerban", "[player] [reason]");
		this.uhcB=uhcB;
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		
		if(!(sender instanceof Player)){
			//TODO add console ban capabilities
			return true;
		}
		else if(args.length==0){
			MessageSender.send(sender, ChatColor.RED + "Please specify a player.");
			return true;
		}
		else if(PlayerUtils.isPlayerOnline(args[0])==false){
			//TODO add offline player ban capabilities
			MessageSender.send(sender, ChatColor.RED + "Player " + args[0].toLowerCase() + " is not online.");
			return true;
		}
		else if(args.length<=1){
			return false;
		}
		else{
			Player target = PlayerUtils.getPlayerExact(args[0]);
			StringBuilder str = new StringBuilder();
			
			for(int i =1; i<args.length; i++){
				str.append(args[i] + " ");
			}
			
			String msg = str.toString();
			uhcB.banPlayer(sender, target, msg);
			return true;
		}

	}

	@Override
	public List<String> tabComplete(Player sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		if(args.length==1){
			for(Player all : Bukkit.getOnlinePlayers()){
				toReturn.add(all.getName());
			}
		}
		return toReturn;
	}

}
