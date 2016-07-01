package com.goldornetwork.uhc.commands.staff;

import java.util.List;

import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.world.UHCWhitelist;
import com.goldornetwork.uhc.utils.MessageSender;

import net.md_5.bungee.api.ChatColor;

public class UHCWhitelistCommand extends UHCCommand{

	
	private UHCWhitelist uhcWhitelist;
	
	
	public UHCWhitelistCommand(UHCWhitelist uhcWhitelist) {
		super("whitelist", "[add/on/off] [player]");
		this.uhcWhitelist=uhcWhitelist;
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		if(args.length==1){
			String action = args[0];
			if(action.equalsIgnoreCase("on")){
				uhcWhitelist.setWhitelist(true);
				return true;
			}
			else if(action.equalsIgnoreCase("off")){
				uhcWhitelist.setWhitelist(false);
				return true;
			}
			else{
				return false;
			}
		}
		else if(args.length==2){
			String action = args[0];
			String target = args[1];
			if(action.equalsIgnoreCase("add")){
				uhcWhitelist.addWhitelist(target);
				MessageSender.send(sender, ChatColor.GRAY + "Whitelisted " + target);
				return true;
			}
			else{
				return false;
			}
			
		}
		else{
			return false;
		}
	}

	@Override
	public List<String> tabComplete(Player sender, String[] args) {
		return null;
	}

}
