package com.goldornetwork.uhc.commands.game;

import java.util.List;

import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

import net.md_5.bungee.api.ChatColor;

public class TeleportCommand extends UHCCommand{

	
	private TeamManager teamM;
	
	
	public TeleportCommand(TeamManager teamM) {
		super("teleport", "[player]");
		this.teamM=teamM;
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		if(State.getState().equals(State.INGAME) || State.getState().equals(State.SCATTER)){
			if(teamM.isPlayerAnObserver(sender.getUniqueId())){
				if(args.length==1){
					String input = args[0];
					if(PlayerUtils.isPlayerOnline(input)){
						if(State.getState().equals(State.INGAME)){
							Player target = PlayerUtils.getPlayer(input);
							sender.teleport(target.getLocation());
							return true;
						}
						else{
							return true;
						}
					}
					else{
						MessageSender.send(sender, ChatColor.RED + "Player " + input + " is not online.");
						return true;
					}
				}
				else{
					return false;
				}
			}
			else{
				return true;
			}
		}
		else{
			return true;
		}
	}

	@Override
	public List<String> tabComplete(Player sender, String[] args) {
		return null;
	}
}
