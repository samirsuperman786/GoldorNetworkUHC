package com.goldornetwork.uhc.commands.console;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.managers.world.UHCBan;

public class Console implements CommandExecutor {


	private UHCBan uhcB;

	public Console(UHC plugin, UHCBan uhcB) {
		plugin.getCommand("console").setExecutor(this);
		this.uhcB=uhcB;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(sender instanceof Player){
			return true;
		}
		else{
			if(args.length==4){
				if(args[0].equalsIgnoreCase("ban")){
					if(args[1].equalsIgnoreCase("ip")){
						String ip = args[2];
						String reason = args[3];
						uhcB.banIPAndAliases(ip, reason);
						return true;
					}
					else if(args[1].equalsIgnoreCase("name")){
						String target = args[2];
						String reason = args[3];
						uhcB.banPlayer(target, reason);
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
			else{
				return false;
			}
		}
	}
}
