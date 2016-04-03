package com.goldornetwork.uhc.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.listeners.MoveEvent;
import com.goldornetwork.uhc.managers.GameModeManager.GameStartEvent;
import com.goldornetwork.uhc.managers.GameModeManager.State;
import com.goldornetwork.uhc.utils.MessageSender;
import com.google.common.collect.ImmutableSet;


public class ScatterManager{

	//TODO check if spawn location is valid 

	//instances
	private UHC plugin;
	private TeamManager teamM;
	private MoveEvent moveE;

	//storage
	private boolean scatterComplete;
	private int radius;
	private int k;
	private BlockFace[] faces = new BlockFace[] { BlockFace.SELF, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST};

	//storage
	private Map<String, List<UUID>> teamToScatter = new HashMap<String, List<UUID>>();
	private Map<String, Location> locationsOfTeamSpawn = new HashMap<String, Location>();
	private List<UUID> lateScatters = new ArrayList<UUID>();
	private List<String> nameOfTeams = new ArrayList<String>();
	private List<UUID> FFAToScatter= new ArrayList<UUID>();

	public ScatterManager(UHC plugin, TeamManager teamM, MoveEvent moveE) {
		this.plugin=plugin;
		this.teamM=teamM;
		this.moveE=moveE;

	}


	/**
	 * Does the following: unfreezes players, disables pvp, disables mob spawning, disables natural regeneration, sets difficulty to hard, will clear scattered players, 
	 * will re-initialize the world border, will clear entities.
	 */
	public void setup(){
		moveE.unfreezePlayers();
		radius = 1000;
		getUHCWorld().setPVP(false);
		getUHCWorld().setGameRuleValue("doMobSpawning", "false");
		getUHCWorld().setGameRuleValue("naturalRegeneration", "false");
		getUHCWorld().setDifficulty(Difficulty.HARD);
		scatterComplete=false;
		teamToScatter.clear();
		locationsOfTeamSpawn.clear();
		lateScatters.clear();
		nameOfTeams.clear();
		WorldBorder wb = getUHCWorld().getWorldBorder();
		wb.setCenter(getUHCWorld().getSpawnLocation());
		wb.setSize(radius*2);
		wb.setDamageBuffer(0);
		wb.setDamageAmount(.5);
		wb.setWarningTime(15);
		wb.setWarningDistance(20);
		for(Entity e : getUHCWorld().getEntities()){
			if(!(e instanceof Player)){
				e.remove();
			}
		}
		//Test code here


	}

	/**
	 * Used to check state of scattering
	 * @return <code> True </code> if scattering has completed
	 */
	public boolean isScatteringComplete(){
		return scatterComplete;
	}
	/**
	 * Will scatter all teams and freeze them until scattering has completed
	 */
	public void scatterTeams(){
		MessageSender.broadcast(ChatColor.GOLD + "Scattering players...");
		for(String team : teamM.getListOfTeams()){
			nameOfTeams.add(team);
			teamToScatter.put(team, teamM.getPlayersOnATeam(team));
		}
		k=0;
		moveE.freezePlayers();
		new BukkitRunnable() {
			@Override
			public void run() {
				if(teamToScatter.isEmpty()){
					Bukkit.getServer().getLogger().info("Error at scattering");
					cancel();
					return;
				}
				else{
					if(k>=teamM.getListOfTeams().size()){
						setupStartingOptions();
						scatterComplete=true;
						cancel();
					}
					else{
						Location location = findValidLocation(getUHCWorld(), radius);
						for(UUID u : teamToScatter.get(nameOfTeams.get(k))){
							locationsOfTeamSpawn.put(nameOfTeams.get(k).toLowerCase(), location);
							OfflinePlayer p = Bukkit.getOfflinePlayer(u);
							if(p.isOnline()==false){
								lateScatters.add(u);
							}
							else if(p.isOnline()==true){
								Player target = (Player) p;
								target.getInventory().clear();
								target.teleport(location);
								target.setGameMode(GameMode.SURVIVAL);
								target.setBedSpawnLocation(location);

							}
						}
						k++;
					}

				}

			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	/**
	 * Will scatter all players in game and freeze them until complete
	 */
	public void scatterFFA(){
		MessageSender.broadcast(ChatColor.GOLD + "Scattering players...");
		FFAToScatter.addAll(teamM.getPlayersInGame());
		moveE.freezePlayers();
		k=0;
		int teleportsPerSecond = 1;
		new BukkitRunnable() {

			@Override
			public void run() {
				if(FFAToScatter.isEmpty()){
					Bukkit.getServer().getLogger().info("Error at scattering");
					cancel();
					return;
				}
				else{
					for(int i = 0; i<teleportsPerSecond; i++){
						if(k>=FFAToScatter.size()){
							scatterComplete=true;
							setupStartingOptions();
							cancel();
							return;
						}
						Location location = findValidLocation(getUHCWorld(), radius);
						OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(FFAToScatter.get(k));
						if(p.isOnline()==false){
							lateScatters.add(p.getUniqueId());
						}
						else if(p.isOnline()==true){
							Player target = (Player) p;
							target.getInventory().clear();
							target.teleport(location);
							target.setBedSpawnLocation(getCenter());
							target.setGameMode(GameMode.SURVIVAL);
						}
						k++;
					}
				}
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	/** Used to get the radius of the map
	 * @return <code> Integer </code> radius of the map
	 */
	public int getRadius(){
		return radius;
	}
	/**
	 * Used to get a safe teleportable location 
	 * @param world - the world the location should be found in
	 * @param radius - the radius of the world that the locations should be found within
	 * @return <code> Location </code> location of safe spawn
	 * @see validate()
	 */
	private Location findValidLocation(World world, int radius){
		boolean valid = false;
		Location location = new Location(world, 0, 0, 0);
		while(valid ==false){
			Random random = new Random();
			int x = random.nextInt(radius * 2) - radius;
			int z = random.nextInt(radius * 2) - radius;
			x= x+ getCenter().getBlockX();
			z= z+ getCenter().getBlockZ();
			//int x = random.nextInt((radius*2) - (-radius*2) +1) + (-radius*2);
			//int z = random.nextInt((radius*2) - (-radius*2) +1) + (-radius*2);
			location.setX(x);
			location.setZ(z);
			location.setY(getUHCWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()));
			if(validate(location.clone())){
				valid=true;
				break;
			}
		}
		return location;
	}

	/**
	 * Used to check if the a given location is appropriate for teleporting conditions
	 * @param loc - the location to check
	 * @return <code> True </code> if location is safe to teleport player to
	 * @see findValidLocation()
	 */
	private boolean validate(Location loc){
		Location landBlock = loc.clone().add(0, -1, 0);
		Material landBlockType = landBlock.getBlock().getType();
		boolean valid = true;
		if(loc.getBlockY()<60){
			valid =false;
		}

		for(Material notValid : INVALID_SPAWN_BLOCKS){
			/*
			 * checking if player will land on a 3  by 3 platform
			 */
			for(BlockFace face : faces){
				if(landBlock.getBlock().getRelative(face).getType().equals(notValid)){
					valid=false;
				}
			}
		}

		/*
		 * checking if a player will spawn in a cube of air
		 */
		for(BlockFace face : BlockFace.values()){
			if(landBlock.clone().add(0, 2, 0).getBlock().getRelative(face).getType()!=Material.AIR){
				valid=false;
			}
		}

		return valid;
	}
	/**
	 * Used to handle players who were disconnected and need to be scattered explicitly in a FFA context
	 * @param p - the player who needs to be teleported
	 */
	public void lateScatterAPlayerInFFA(Player p){
		Location location = findValidLocation(getUHCWorld(), radius);
		p.teleport(location);
		p.setBedSpawnLocation(location);
	}

	/**
	 * Used to handle players who were disconnected and need to be scattered explicitly in a team context
	 * @param team - the team of the player
	 * @param p - the player who needs to be teleported 
	 */
	public void lateScatterAPlayerInATeam(String team, Player p){
		p.teleport(locationsOfTeamSpawn.get(team.toLowerCase()));
	}
	/**
	 * Used to indicate that the player no longer needs to be scattered
	 * @param p - the player who no longer needs to be scattered
	 */
	public void removePlayerFromLateScatters(Player p){
		lateScatters.remove(p.getUniqueId());
	}


	/** Used to retrieve a list of players who need to be scattered
	 * @return <code> List </code> of players who need to be scattered
	 */
	public List<UUID> getLateScatters(){
		return this.lateScatters;
	}

	/**
	 * Used to get the world the match is being played in
	 * @return <code> World </code> of match
	 */
	public World getUHCWorld(){
		//TODO make a rotation list of viable UHC maps
		return Bukkit.getServer().getWorld("lol");
	}
	/**
	 * Used to retrieve the center of the match
	 * @return <code> Location </code> of the center of the match
	 */
	public Location getCenter(){
		return getUHCWorld().getSpawnLocation();
	}
	
	/**
	 * Used to shrink the border of the world, ideally when PVP is enabled
	 */
	public void shrinkBorder(){
		getUHCWorld().getWorldBorder().setSize(400, 15*60);
		MessageSender.broadcast("The worldborder will now slowly shrink to a radius of 400.");
	}

	/**
	 * A list of blocks that we do not want players to spawn on
	 * @see findValidLocation()
	 * @see validate()
	 */
	private static final Set<Material> INVALID_SPAWN_BLOCKS = ImmutableSet.of(
			Material.STATIONARY_LAVA,
			Material.LAVA, 
			Material.WATER, 
			Material.STATIONARY_WATER, 
			Material.CACTUS,
			Material.AIR,
			Material.LEAVES,
			Material.LEAVES_2
			);

	/**
	 * Called when scattering has completed, it sets up world conditions
	 */
	private void setupStartingOptions() {
		Bukkit.getPluginManager().callEvent(new GameStartEvent());
		State.setState(State.INGAME);
		moveE.unfreezePlayers();
		MessageSender.broadcast(ChatColor.GOLD + "Scattering complete!");
		getUHCWorld().setGameRuleValue("doMobSpawning", "true");
	}


}
