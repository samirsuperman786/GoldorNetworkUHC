package com.goldornetwork.uhc.commands.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager;
import com.goldornetwork.uhc.managers.GameModeManager.Gamemode;
import com.goldornetwork.uhc.utils.MessageSender;


public class InfoCommand extends UHCCommand{
	private GameModeManager gamemodeM;

	public InfoCommand(GameModeManager gamemodeM) {
		super("info", "[gamemode]");
		this.gamemodeM=gamemodeM;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(args.length==0){
			if(getMessage()!=null){
				for(String msg : getMessage()){
					MessageSender.send(sender, msg);
				}
			}
			else{
				MessageSender.send(sender, ChatColor.RED + "No gamemodes currently enabled. Please use /info [gamemode]");
			}

			return true;
		}
		else if(args.length==1){
			if(gamemodeM.getGamemode(args[0])!=null){
				Gamemode game = gamemodeM.getGamemode(args[0]);
				MessageSender.send(sender, ChatColor.AQUA + game.getProperName() + ": " + ChatColor.DARK_AQUA + game.getDescription());
				return true;
			}
			return false;
		}
		else{
			return false;
		}

	}

	private List<String> getMessage(){
		List<String> toReturn = new LinkedList<String>();
		if(gamemodeM.getEnabledGamemodes().isEmpty()==false){
			toReturn.add(ChatColor.GOLD + "Enabled Gamemodes: ");
			for(Gamemode game : gamemodeM.getEnabledGamemodes()){
				toReturn.add(ChatColor.AQUA + game.getProperName() + ": " + ChatColor.DARK_AQUA + game.getDescription());
			}
			toReturn.add(ChatColor.GOLD + "For a specific gamemode -> " + ChatColor.GOLD + "/info " + ChatColor.AQUA + "[gamemode]");
		}

		else if(gamemodeM.getEnabledGamemodes().isEmpty()){
			return null;
		}
		return toReturn;
	}
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		if(args.length==1){
			for(Gamemode game : gamemodeM.getGamemodes()){
				toReturn.add(game.getName());
			}
		}
		return toReturn;
	}
}
