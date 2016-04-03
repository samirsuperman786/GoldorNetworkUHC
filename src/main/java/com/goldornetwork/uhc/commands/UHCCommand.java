package com.goldornetwork.uhc.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public abstract class UHCCommand {

	private String name, usage;
	
	public UHCCommand(String name, String usage) {
		this.name=name;
		this.usage=usage;
	}
	
	/**
	 * Used to get the name of a command
	 * @return <code> String </code>name of command
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Used to get the usage of a command
	 * @return <code> String </code> usage of command
	 */
	public String getUsage(){
		return "/" + name + " " + usage;
	}
	
	/**
	 * Used to get the permission of a command
	 * @return <code> String </code> permission of command
	 */
	public String getPermission(){
		return "uhc." + name;
	}
	
	public abstract boolean execute(CommandSender sender, String[] args);
	
	public abstract List<String> tabComplete(CommandSender sender, String[] args);
	
	
}
