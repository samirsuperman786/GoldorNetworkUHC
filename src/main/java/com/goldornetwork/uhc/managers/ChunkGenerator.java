package com.goldornetwork.uhc.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;

import com.goldornetwork.uhc.UHC;
import com.goldornetwork.uhc.utils.MessageSender;

public class ChunkGenerator implements Runnable{

	private static ChunkGenerator instance = new ChunkGenerator();
	private MessageSender ms = new MessageSender();
	
	//
	private static Runtime rt = Runtime.getRuntime();
	private static int fillMemoryTolerance = 500;
	//
	private Location center;
	private int radius;
	private World world;

	//
	int di =-16;
    int dj = 0;
    // length of current segment
    int segment_length;

    // current position (i, j) and how much of current segment we passed
    int i;
    int j;
    int segment_passed;
    private int k;
	
	
	//
	private int tempXBlock;
	private int tempZBlock;
	private boolean isNeg=true;
	private boolean isGeneratingDone;
	private boolean startGenerating;
	private int chunksPerRun =15;
	private int chunksLeft;
	private int duration=0;
	
	public static ChunkGenerator getInstance(){
		return instance;
	}

	public void loadGenerator(World world, Location center, int radius){
		this.world = world;
		this.center = center;
		this.radius = 16*(Math.round(radius/16));
		this.tempXBlock = radius;
		this.tempZBlock = radius;
		this.i=0;
		this.j=0;
		this.chunksLeft=0;
		this.segment_length=1;
		this.segment_passed=0;
		this.k =0;
		this.startGenerating=true;
	}
	
	public double getTimeTillCompleteInMinutes(){
		return (((radius/16) * (radius/16) *4)/chunksPerRun)/60;
	}
	
	public boolean isGenerating(){
		return startGenerating;
	}
	
	public void pauseGeneration(){
		if(startGenerating){
			startGenerating=false;
		}
		else{
			//nothing
		}
	}
	public void resumeGeneration(){
		if(startGenerating==false){
			 startGenerating=true;
		}
	}
	
	public int availableMemory(){
		return (int) ((rt.maxMemory() - rt.totalMemory() + rt.freeMemory())/1048576);
	}
	
	public boolean availableMemoryTooLow(){
		return availableMemory()< fillMemoryTolerance;
	}
	

	@Override
	public void run() {
		duration++;
			if(isGeneratingDone){
				ms.broadcast("Done Preloading Chunks!");
				isGeneratingDone=false;
			}
			else if(startGenerating){
				if(duration% 10 ==0){//take a break every 10 seconds and save world
					if(startGenerating){
						world.save();
					}
					return;
				}
				if(duration %300 ==0){//updates every 5 minutes
					ms.broadcast((getTimeTillCompleteInMinutes() - (chunksLeft/chunksPerRun))/60 + " minutes until \"" + world.getName() +"\" will finish rendering." );
				}
				if(availableMemoryTooLow()){ //pause here
					return;
				}
				else{
					for(int c = 0; c< chunksPerRun; c++){
						if(k <((radius/16) * (radius/16) *4)){
							chunksLeft--;
							 ++k;
						    	i += di;
						        j += dj;
						        ++segment_passed;

						        if (segment_passed == segment_length) {
						            // done with current segment
						            segment_passed = 0;

						            // 'rotate' directions
						            int buffer = di;
						            di = -dj;
						            dj = buffer;

						            // increase segment length if necessary
						            if (dj == 0) {
						                ++segment_length;
						            }
						        }
						        world.loadChunk(i, j);
						        //Bukkit.getPlayer("g0ldmanpox").teleport(new Location(world, i, world.getMaxHeight(), j));
						 }
						else if(k==((radius/16) * (radius/16) *4)){
							startGenerating=false;
							isGeneratingDone=true;
						}
						
					}
				}
				
				 
				
			}
			



		


	}




}
