package com.goldornetwork.uhc.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.ChunkGenerator;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class CommandHandler implements CommandExecutor, TabCompleter{

	private final UHC plugin;
	
	public CommandHandler(UHC plugin) {
		this.plugin= plugin;
	}
	
	private List<UHCCommand> cmds = new ArrayList<UHCCommand>();
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		UHCCommand command = getCommand(cmd.getName());
		
		if(!(sender.hasPermission(command.getPermission()))){
			MessageSender.noPerms(sender);
			return true;
		}
		try{
			if(!(command.execute(sender, args))){
				MessageSender.send(ChatColor.RED, sender, "Usage: " + command.getUsage());
			}
		}catch(Exception e){
			MessageSender.send(ChatColor.RED, sender, e.getClass().getName() + ": " + e.getMessage());
		}
		return true;
		
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		UHCCommand command = getCommand(cmd.getName());
		
		try {
			List<String> list = command.tabComplete(sender, args);
			
			// if the list is null, replace it with everyone online.
			if (list == null) {
				list = getAllPlayerNames(sender);
			}
			
			// I don't want anything done if the list is empty.
			if (list.isEmpty()) {
				return list;
			}
			
			List<String> toReturn = new ArrayList<String>();
			
			if (args[args.length - 1].isEmpty()) {
				for (String type : list) {
					toReturn.add(type);
				}
			} else {
				for (String type : list) {
					if (type.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
						toReturn.add(type);
					}
				}
			}
			
			return toReturn;
		} catch (Exception ex) {
			// send them the error message in red if anything failed.
			sender.sendMessage(ChatColor.RED + ex.getMessage());
		}
		return null;
		
	}
	
	protected UHCCommand getCommand(String name) {
        for (UHCCommand cmd : cmds) {
            if (cmd.getName().equalsIgnoreCase(name)) {
                return cmd;
            }
        }
        
        return null;
    }
	
	private List<String> getAllPlayerNames(CommandSender sender) {
		List<String> list = new ArrayList<String>();
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (sender instanceof Player && !((Player) sender).canSee(online)) {
				continue;
			}
			
			list.add(online.getName());
		}
		
		return list;
	}
	
	public void registerCommands(TeamManager teamM, TimerManager timerM, ChunkGenerator chunkG){
		
	}
	
	
}
