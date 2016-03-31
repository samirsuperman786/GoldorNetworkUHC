package com.goldornetwork.uhc.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.BlockRush;
import com.goldornetwork.uhc.managers.ModifierManager.gamemodes.FlowerPower;
import com.goldornetwork.uhc.utils.AntiXray;

public class BreakEvent implements Listener{

	//instances
	private static BreakEvent instance = new BreakEvent();
	private TeamManager teamM = TeamManager.getInstance();
	private TimerManager timerM= TimerManager.getInstance();
	private FlowerPower flowerPowerM = FlowerPower.getInstance();
	private AntiXray antiX = AntiXray.getInstace();
	private BlockRush blockRushM = BlockRush.getInstance();
	//storage

	
	private boolean enableFlowerPower;
	private boolean enableBlockRush;

	public static BreakEvent getInstance(){
		return instance;
	}

	public void setup(){
		antiX.setup();
		enableFlowerPower=false;
		enableBlockRush=false;
	}

	
	public void enableFlowerPower(boolean val){
		this.enableFlowerPower=val;
	}
	public void enableBlockRush(boolean val){
		this.enableBlockRush = val;
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void OnPlayerBreakEvent(BlockBreakEvent e){
		Player p = e.getPlayer();
		if(timerM.hasCountDownEnded()){
			if(teamM.isPlayerInGame(p)){
				if(e.getBlock().getType().equals(Material.DIAMOND_ORE)){
					antiX.run(p, e);
				}
				if(enableFlowerPower){
					if(e.getBlock().getType().equals(Material.YELLOW_FLOWER) || e.getBlock().getType().equals(Material.RED_ROSE)){
						flowerPowerM.run(p, e);
					}
				}
				if(enableBlockRush){
					blockRushM.run(p, e);
				}

			}
		}






	}


}
