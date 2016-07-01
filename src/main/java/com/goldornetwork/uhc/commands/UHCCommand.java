package com.goldornetwork.uhc.commands;

import java.util.List;

import org.bukkit.entity.Player;

public abstract class UHCCommand {

	
	private String name;
	private String usage;
	
	
	public UHCCommand(String name, String usage) {
		this.name=name;
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
	
	public abstract boolean execute(Player sender, String[] args);
	
	public abstract List<String> tabComplete(Player sender, String[] args);
	
	
}
