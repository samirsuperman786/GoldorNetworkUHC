package com.goldornetwork.uhc.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.goldornetwork.uhc.managers.ChunkGenerator;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class ChunkGenerateCommand implements CommandExecutor{

	private TimerManager timerM = TimerManager.getInstance();
	private MessageSender ms = new MessageSender();
	private ChunkGenerator chunkG = ChunkGenerator.getInstance();
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!(sender.hasPermission("uhc.generate"))){
			ms.noPerms(sender);
			return true;
		}
		else if(timerM.hasMatchStarted()){
			ms.send(ChatColor.RED, sender, "Cannot enter chunk generation mode during the match!");
			return true;
		}
		else if(chunkG.isGenerating()==true){
			ms.send(ChatColor.RED, sender, "Please cancel current generation first!");
			return true;
		}
		else if(args.length>0){
			if(args.length==2){
				if(Bukkit.getServer().getWorld(args[0])!=null){
					World world = Bukkit.getServer().getWorld(args[0]);
					if(world.getWorldBorder()!=null){
						if(Integer.valueOf(args[1])!=null){
							int radius = Integer.valueOf(args[1]);
							chunkG.loadGenerator(world, world.getWorldBorder().getCenter(), radius);
							ms.broadcast("Generating chunks with a radius of " + radius + " in world \"" + world.getName()  + "\" ETC : " + (chunkG.getTimeTillCompleteInMinutes()) + " minutes");
							ms.broadcast("Warning- the server should be empty during the generation period.");
							return true;
						}
						else{
							return false;
						}
						
					}
					else{
						if(Integer.valueOf(args[1])!=null){
							int radius = Integer.valueOf(args[1]);
							chunkG.loadGenerator(world, new Location(world, 0, 0, 0) , radius);
							ms.broadcast("Generating chunks with a radius of " + radius + " in world \"" + world.getName() + "\" ETC : " + (chunkG.getTimeTillCompleteInMinutes()) + " minutes");
							ms.broadcast("Warning- the server should be empty during the generation period.");
							return true;
						}
						else{
							return false;
						}
					}
				
				}
				else{
					ms.send(ChatColor.RED, sender, "World " + args[0] + " does not exist!" );
					return true;
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
