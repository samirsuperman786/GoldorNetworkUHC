package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ScatterManager implements Runnable {
	
	private static ScatterManager instance = new ScatterManager();
	private ChunkGenerator chunkG = ChunkGenerator.getInstance();
	
	private boolean startScattering;
	
	private boolean scatterTeam;
	
	private boolean scatterFFA;
	
	private boolean scatterComplete;
	
	private int radius;//todo remove
	
	private Map<String, List<UUID>> teamToScatter = new HashMap<String, List<UUID>>();
	
	private Map<String, Location> locationsOfTeamSpawn = new HashMap<String, Location>();
	
	private List<UUID> lateScatters = new ArrayList<UUID>();
	
	private List<String> nameOfTeams = new ArrayList<String>();
	
	private List<UUID> FFAToScatter = new ArrayList<UUID>();
	
	public static ScatterManager getInstance(){
		return instance;
	}
	
	public void setup(){
		//TODO make below false
		getUHCWorld().setPVP(true);
		getUHCWorld().setGameRuleValue("doMobSpawning", "false");
		startScattering=false;
		scatterTeam=false;
		scatterFFA=false;
		scatterComplete=false;
		radius=1000;
		teamToScatter.clear();
		locationsOfTeamSpawn.clear();
		lateScatters.clear();
		nameOfTeams.clear();
		FFAToScatter.clear();
		WorldBorder wb = getUHCWorld().getWorldBorder();
			wb.setCenter(getUHCWorld().getSpawnLocation());
			wb.setSize(radius*2);
			//wb.setSize(50, 120); use this to shrink border
			wb.setDamageBuffer(0);
			wb.setDamageAmount(.5);
			wb.setWarningTime(15);
			wb.setWarningDistance(20);
			for(Entity e : getUHCWorld().getEntities()){
				if(!(e instanceof Player)){
					e.remove();
				}
			}
			chunkG.loadGenerator(getUHCWorld(), getUHCWorld().getWorldBorder().getCenter(), 1008, 5);
		
			
			
	}
	
	public void scatterTeam(String team, List<UUID> p, Integer radius){
		teamToScatter.put(team.toLowerCase(), p);
		this.radius = radius;
		scatterFFA=false;
		scatterTeam=true;
	}
	
	public void scatterFFA(List<UUID> p, Integer radius){
		FFAToScatter.addAll(p);
		this.radius = radius;
		scatterTeam=false;
		scatterFFA = true;
		
	}
	
	public int getRadius(){
		return radius;
	}
	
	public void lateScatterAPlayerInFFA(Player p){
		Location location = new Location(getUHCWorld(), 0, 0, 0);
		Random random = new Random();
		
		int x = random.nextInt((radius*2) - (-radius*2) +1) + (-radius*2);
		int z = random.nextInt((radius*2) - (-radius*2) +1) + (-radius*2);
		location.setX(x);
		location.setZ(z);
		location.setY(getUHCWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()));
		p.teleport(location);
		p.setBedSpawnLocation(location);
	}
	
	public void lateScatterAPlayerInATeam(String team, Player p){
		p.teleport(locationsOfTeamSpawn.get(team.toLowerCase()));
	}
	
	public List<UUID> getLateScatters(){
		return this.lateScatters;
	}
	
	public boolean isScatteringComplete(){
		return scatterComplete;
	}
	public World getUHCWorld(){
		return Bukkit.getServer().getWorld("lol");
	}
	
	
	@Override
	public void run() {
		if(startScattering){
			if(scatterTeam){
				//TODO make a rotation list of viable UHC maps
				Location location = new Location(getUHCWorld(), 0, 0, 0);
				Random random = new Random();
				
				int x = random.nextInt((radius*2) - (-radius*2) +1) + (-radius*2);
				int z = random.nextInt((radius*2) - (-radius*2) +1) + (-radius*2);
				location.setX(x);
				location.setZ(z);
				location.setY(getUHCWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()));
				
				for(UUID u : teamToScatter.get(nameOfTeams.get(nameOfTeams.size()))){
					locationsOfTeamSpawn.put(nameOfTeams.get(nameOfTeams.size()).toLowerCase(), location);
					Bukkit.getPlayer(u).setBedSpawnLocation(location);
					if(Bukkit.getPlayer(u).isOnline()==false){
						
						lateScatters.add(u);
						
					}
					else if(Bukkit.getPlayer(u).isOnline()==true){
					
						Bukkit.getPlayer(u).teleport(location);
						
					}
					
					
				}
				teamToScatter.remove(nameOfTeams.get(nameOfTeams.size()));
				nameOfTeams.remove(nameOfTeams.size());
				
				if(teamToScatter.isEmpty()){
					getUHCWorld().setGameRuleValue("doMobSpawning", "true");
					scatterComplete=true;
					scatterTeam = false;
					
				}
				
			}
			else if(scatterFFA){
				
				Location location = new Location(getUHCWorld(), 0, 0, 0);
				Random random = new Random();
				
				int x = random.nextInt((radius*2) - (-radius*2) +1) + (-radius*2);
				int z = random.nextInt((radius*2) - (-radius*2) +1) + (-radius*2);
				location.setX(x);
				location.setZ(z);
				location.setY(getUHCWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()));
				
				if(Bukkit.getPlayer(FFAToScatter.get(FFAToScatter.size())).isOnline()==false){
					lateScatters.add(FFAToScatter.get(FFAToScatter.size()));
				}
				else if(Bukkit.getPlayer(FFAToScatter.get(FFAToScatter.size())).isOnline()==true){
					Bukkit.getPlayer(FFAToScatter.get(FFAToScatter.size())).teleport(location);
				}
				Bukkit.getPlayer(FFAToScatter.get(FFAToScatter.size())).setBedSpawnLocation(location);
				FFAToScatter.remove(FFAToScatter.size());
				
				if(FFAToScatter.isEmpty()){
					getUHCWorld().setGameRuleValue("doMobSpawning", "true");
					scatterComplete=true;
					scatterFFA=false;
				}
				
				
			}
			
		}
		else{
			//do nothing
		}
		
	
		
	}

}
