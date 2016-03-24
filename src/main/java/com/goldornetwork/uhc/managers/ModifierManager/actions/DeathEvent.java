package com.goldornetwork.uhc.managers.ModifierManager.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.goldornetwork.uhc.managers.ScatterManager;
import com.goldornetwork.uhc.managers.TeamManager;
import com.goldornetwork.uhc.managers.TimerManager;
import com.goldornetwork.uhc.utils.MessageSender;

public class DeathEvent implements Listener {

	//instances
	private static DeathEvent instance = new DeathEvent();
	private TeamManager teamM =  TeamManager.getInstance();
	private TimerManager timerM =  TimerManager.getInstance();
	private ScatterManager scatterM = ScatterManager.getInstance();
	private PotionSwap potionS = PotionSwap.getInstance();
	private MessageSender ms = new MessageSender();

	//gamemodes
	private boolean enableLiveWithRegret;
	private boolean enableKillSwitch;
	
	//storage
	private Map<UUID, Integer> numberOfDeaths = new HashMap<UUID, Integer>();


	public static DeathEvent getInstance(){
		return instance;
	}

	public void setup(){
		enableLiveWithRegret =false;
		numberOfDeaths.clear();
	}

	public void enableLiveWithRegret(boolean val){
		this.enableLiveWithRegret=val;
	}
	public void enableKillSwitch(boolean val){
		this.enableKillSwitch = val;
	}

	private void playerDied(Player p, Event e){
		p.setHealth(p.getMaxHealth());
		teamM.addPlayerToObservers(p);
		if(teamM.isTeamsEnabled()){
			if(teamM.getOwnerOfTeam(teamM.getTeamOfPlayer(p)).equals(p.getUniqueId())){
				teamM.removePlayerFromOwner(p);
			}
			deathMsg(p, e);
			teamM.removePlayerFromTeam(p);
		}
		else if(teamM.isFFAEnabled()){
			deathMsg(p, e);
			teamM.removePlayerFromFFA(p);
		}

	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		if(teamM.isPlayerInGame(p)==true && timerM.hasCountDownEnded()){
			p.getWorld().strikeLightningEffect(p.getLocation());
			if(enableLiveWithRegret){
				if(timerM.isPVPEnabled()==false){
					if(numberOfDeaths.containsKey(p.getUniqueId())==false){
						numberOfDeaths.put(p.getUniqueId(), 1);
						p.setHealth(p.getMaxHealth());
						potionS.giveAPlayerARandomPotion(p, Integer.MAX_VALUE);
						e.setDeathMessage(teamM.getColorOfPlayer(p) + p.getName() + ChatColor.GRAY + " has died and respawned with debuffs.");
						if(teamM.isTeamsEnabled()){
							scatterM.lateScatterAPlayerInATeam(teamM.getTeamOfPlayer(p), p);
						}
						else if(teamM.isFFAEnabled()){
							scatterM.lateScatterAPlayerInFFA(p);					}
						else{
							playerDied(p, e);
						}
					}
					else{
						playerDied(p, e);
					}

				}
				else{
					playerDied(p, e);
				}

			}
			if(enableKillSwitch){
				if(p.getKiller() instanceof Player){
					ItemStack[] targetInventory = p.getInventory().getContents();
					Inventory killerInventory = p.getKiller().getInventory();
					killerInventory.clear();
					killerInventory.setContents(targetInventory);
					e.getDrops().clear();
					Player killer = p.getKiller();
					ms.alertMessage(killer, ChatColor.GOLD, "You have switched inventories with " + teamM.getColorOfPlayer(p) + p.getName());
					playerDied(p, e);
				}
				
			}
			else{
				playerDied(p, e);
			}
			
		}
		else{
			e.setDeathMessage(null);
		}
	}
	//TODO fix this stuff
	private void deathMsg(Player target, Event e){
		PlayerDeathEvent event = (PlayerDeathEvent) e;
		if(event.getEntity() instanceof Zombie){
			event.setDeathMessage(target.getName() + " died by a creeper" );
		}
		else{
			event.setDeathMessage(target.getName() + " died by " + event.getEntity().getKiller().getName());

		}



	}


}
