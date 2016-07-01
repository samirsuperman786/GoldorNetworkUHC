package com.goldornetwork.uhc.commands.staff;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.goldornetwork.uhc.commands.UHCCommand;
import com.goldornetwork.uhc.managers.world.listeners.MoveListener;
import com.goldornetwork.uhc.utils.MessageSender;
import com.goldornetwork.uhc.utils.PlayerUtils;

import net.md_5.bungee.api.ChatColor;

public class FreezeCommand extends UHCCommand{


	private MoveListener moveL;


	public FreezeCommand(MoveListener moveL) {
		super("freeze", "[player]");
		this.moveL=moveL;
	}

	@Override
	public boolean execute(Player sender, String[] args) {
		if(args.length==1){
			String input = args[0];
			UUID target = PlayerUtils.getOfflinePlayer(input).getUniqueId();
			
			if(moveL.isFrozen(target)){
				moveL.unfreezePlayer(target);
				MessageSender.send(sender, ChatColor.GRAY + "Un-froze player " + input);
			}
			else{
				moveL.freezePlayer(target);
				MessageSender.send(sender, ChatColor.GRAY + "Froze player " + input);
			}
			return true;
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
