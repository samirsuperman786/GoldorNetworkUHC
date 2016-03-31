package com.goldornetwork.uhc.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public abstract class UHCCommand {
	private String name, usage;
	
	public UHCCommand(String name, String usage) {
		this.name = name;
		this.usage=usage;
	}
	
	public String getName(){
		return name;
	}
	public String getUsage(){
		return "/" + name + " " + usage;
	}
	public String getPermission(){
		return "uhc." + name;
	}
	
	public abstract boolean execute(CommandSender sender, String[] args);
	
	public abstract List<String> tabComplete(CommandSender sender, String[] args);
	
	
}
