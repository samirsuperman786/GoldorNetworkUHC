package com.goldornetwork.uhc.commands.staff;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.chat.ChatManager;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

public class UHCUnMuteCommand extends UHCCommand{


	private ChatManager chatM;
	private TeamManager teamM;


	public UHCUnMuteCommand(ChatManager chatM, TeamManager teamM) {
		super("unmute", "[player]");
		this.chatM=chatM;
		this.teamM=teamM;
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		if(args.length==0){
			MessageSender.send(sender, ChatColor.RED + "Please specify a player.");
			return true;
		}
		else if(args[0].equalsIgnoreCase("*")){
			chatM.unMutePlayers();
			return true;
		}
		else if(PlayerUtils.isPlayerOnline(args[0])==false){
			MessageSender.send(sender, ChatColor.RED + "Player " + args[0].toLowerCase() + " is not online.");
			return true;
		}
		else if(args.length==1){
			Player target = PlayerUtils.getPlayer(args[0]);

			if(chatM.isMuted(target.getUniqueId())){
				chatM.unMute(target.getUniqueId());
				return true;
			}
			else{
				MessageSender.send(sender, ChatColor.RED + "Player " + teamM.getColorOfPlayer(target.getUniqueId()) + target.getName()
				+ ChatColor.RED + " is not muted.");
				return true;
			}
		}
		else{
			return false;
		}
	}

	@Override
	public List<String> tabComplete(Player sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		if(args.length==1){
			for(UUID all : chatM.getMutedPlayers()){
				OfflinePlayer target = Bukkit.getOfflinePlayer(all);
				toReturn.add(target.getName());
			}
		}
		return toReturn;
	}

}
