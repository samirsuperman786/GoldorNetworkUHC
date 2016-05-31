package com.goldornetwork.uhc.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.world.UHCBan;
import com.goldornetwork.uhc.utils.MessageSender;

public class UHCBanCommand extends UHCCommand{

	private TeamManager teamM;
	private UHCBan uhcB;
	public UHCBanCommand(TeamManager teamM, UHCBan uhcB) {
		super("playerban", "[player] [reason]");
		this.teamM=teamM;
		this.uhcB=uhcB;
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

		else if(teamM.isPlayerOnline(args[0])==false){
			//TODO add offline player ban capabilities
			MessageSender.send(ChatColor.RED, banner, "Player " + args[0].toLowerCase() + " is not online!");
			return true;
		}
		else if(args.length<=1){
			return false;
		}
		else{
			Player target = Bukkit.getPlayerExact(args[0]);
			StringBuilder str = new StringBuilder();
			for(int i =0; i<args.length; i++){
				str.append(args[i] + " ");
			}
			String msg = str.toString();
			uhcB.banPlayer(banner, target, msg);
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
