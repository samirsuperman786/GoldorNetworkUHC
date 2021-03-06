package com.goldornetwork.uhc.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.commands.game.HealthCommand;
import com.goldornetwork.uhc.commands.game.HelpCommand;
import com.goldornetwork.uhc.commands.game.HelpopCommand;
import com.goldornetwork.uhc.commands.game.InfoCommand;
import com.goldornetwork.uhc.commands.game.LookupCommand;
import com.goldornetwork.uhc.commands.game.PMCommand;
import com.goldornetwork.uhc.commands.game.ReplyCommand;
import com.goldornetwork.uhc.commands.game.ReportCommand;
import com.goldornetwork.uhc.commands.game.TeleportCommand;
import com.goldornetwork.uhc.commands.game.VoteCommand;
import com.goldornetwork.uhc.commands.staff.FreezeCommand;
import com.goldornetwork.uhc.commands.staff.StartCommand;
import com.goldornetwork.uhc.commands.staff.UHCBanCommand;
import com.goldornetwork.uhc.commands.staff.UHCMuteCommand;
import com.goldornetwork.uhc.commands.staff.UHCUnMuteCommand;
import com.goldornetwork.uhc.commands.staff.UHCWarnCommand;
import com.goldornetwork.uhc.commands.staff.UHCWhitelistCommand;
import com.goldornetwork.uhc.commands.team.CreateCommand;
import com.goldornetwork.uhc.commands.team.InvitePlayerCommand;
import com.goldornetwork.uhc.commands.team.JoinCommand;
import com.goldornetwork.uhc.commands.team.KickCommand;
import com.goldornetwork.uhc.commands.team.LeaveCommand;
import com.goldornetwork.uhc.commands.team.PMCoordsCommand;
import com.goldornetwork.uhc.commands.team.RequestCommand;
import com.goldornetwork.uhc.commands.team.TeamChatCommand;
import com.goldornetwork.uhc.commands.team.UnInvitePlayerCommand;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.VoteManager;
import com.goldornetwork.uhc.managers.GameModeManager.GameModeManager;
import com.goldornetwork.uhc.managers.chat.ChatManager;
import com.goldornetwork.uhc.managers.chat.TeamInteraction;
import com.goldornetwork.uhc.managers.world.ChunkGenerator;
import com.goldornetwork.uhc.managers.world.UHCBan;
import com.goldornetwork.uhc.managers.world.UHCWarn;
import com.goldornetwork.uhc.managers.world.UHCWhitelist;
import com.goldornetwork.uhc.managers.world.listeners.MoveListener;
import com.goldornetwork.uhc.utils.MessageSender;

public class CommandHandler implements CommandExecutor, TabCompleter {


	private final UHC plugin;

	private List<UHCCommand> cmds = new ArrayList<UHCCommand>();

	public CommandHandler(UHC plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		UHCCommand command = getCommand(cmd.getName());
		Player messenger;
		
		if(command == null){
			return true;
		}
		else if(!(sender instanceof Player)){
			MessageSender.noConsole(sender);
			return true;
		}
		else if(!(sender.hasPermission(command.getPermission()))){
			MessageSender.noPerms(sender);
			return true;
		}
		messenger = (Player) sender;
		
		try{
			if(!(command.execute(messenger, args))){
				MessageSender.usageMessage(sender, command.getUsage());
			}

		} catch(CommandException ex) {
			MessageSender.send(sender, ChatColor.RED + ex.getMessage());
		} catch(Exception e) {
			MessageSender.send(sender, ChatColor.RED + e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}
		return true;

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		UHCCommand command = getCommand(cmd.getName());

		if(command == null){
			return null;
		}

		if(!(sender instanceof Player)){
			return null;
		}

		if (!(sender.hasPermission(command.getPermission()))){
			return null;
		}

		Player messenger = (Player) sender;

		try {
			List<String> list = command.tabComplete(messenger, args);

			if(list == null){
				list = getAllPlayerNames(sender);
			}
			if(list.isEmpty()){
				return list;
			}

			List<String> toReturn = new ArrayList<String>();

			if(args[args.length - 1].isEmpty()){
				for (String type : list) {
					toReturn.add(type);
				}
			} 
			else{
				for(String type : list){
					if(type.toLowerCase().startsWith(args[args.length - 1].toLowerCase())){
						toReturn.add(type);
					}
				}
			}
			return toReturn;

		} catch(Exception ex){
			sender.sendMessage(ChatColor.RED + ex.getMessage());
		}
		return null;

	}

	protected UHCCommand getCommand(String name) {

		for(UHCCommand cmd : cmds){
			if(cmd.getName().equalsIgnoreCase(name)){
				return cmd;
			}
		}
		return null;
	}

	private List<String> getAllPlayerNames(CommandSender sender) {
		List<String> list = new ArrayList<String>();

		for(Player all : Bukkit.getOnlinePlayers()){
			if(sender instanceof Player){
				list.add(all.getName());
			}
		}
		return list;
	}

	public void registerCommands(TeamManager teamM, TimerManager timerM, GameModeManager gamemodeM,
			ChunkGenerator chunkG, VoteManager voteM, TeamInteraction teamI, UHCBan uhcB, UHCWarn uhcWarn,
			ChatManager chatM, UHCWhitelist uhcWhitelist, MoveListener moveL){
		// staff
		cmds.add(new StartCommand(timerM, teamM));
		cmds.add(new UHCBanCommand(uhcB));
		cmds.add(new UHCWarnCommand(uhcWarn));
		cmds.add(new UHCMuteCommand(chatM));
		cmds.add(new UHCUnMuteCommand(chatM, teamM));
		cmds.add(new UHCWhitelistCommand(uhcWhitelist));
		cmds.add(new FreezeCommand(moveL));

		// team
		cmds.add(new CreateCommand(teamM));
		cmds.add(new InvitePlayerCommand(teamM));
		cmds.add(new JoinCommand(teamM));
		cmds.add(new UnInvitePlayerCommand(teamM));
		cmds.add(new LeaveCommand(teamM));
		cmds.add(new PMCoordsCommand(teamM, teamI));
		cmds.add(new TeamChatCommand(teamM, teamI));
		cmds.add(new KickCommand(teamM));
		cmds.add(new RequestCommand(teamM, uhcWhitelist));
		
		// game
		cmds.add(new HelpopCommand(chatM));
		cmds.add(new HelpCommand());
		cmds.add(new InfoCommand(gamemodeM));
		cmds.add(new VoteCommand(voteM));
		cmds.add(new HealthCommand(teamM));
		cmds.add(new LookupCommand(teamM));
		cmds.add(new PMCommand(chatM));
		cmds.add(new ReplyCommand(chatM));
		cmds.add(new ReportCommand(chatM));
		cmds.add(new TeleportCommand(teamM));
		
		for(UHCCommand cmd : cmds){
			PluginCommand pCmd = plugin.getCommand(cmd.getName());

			pCmd.setExecutor(this);
			pCmd.setTabCompleter(this);
		}

	}

}
