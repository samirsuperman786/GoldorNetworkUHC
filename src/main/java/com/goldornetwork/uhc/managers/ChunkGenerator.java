package com.goldornetwork.uhc.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import com.goldornetwork.uhc.utils.MessageSender;

public class ChunkGenerator implements Runnable{

	private static ChunkGenerator instance = new ChunkGenerator();
	private MessageSender ms = new MessageSender();
	private int chunksThatNeedToBeGenerated;
	private Location center;
	private int radius;
	private World world;

	//
	private int tempXBlock;
	private int tempZBlock;
	private boolean isNeg=true;
	private boolean firstStage;
	private boolean secondStage;
	private boolean isGeneratingDone;
	private boolean startGenerating;
	private int chunksPerRun;
	private int duration=0;
	public static ChunkGenerator getInstance(){
		return instance;
	}

	public void loadGenerator(World world, Location center, int radius, int chunksPerRun){
		this.world = world;
		this.center = center;
		this.radius = radius;
		this.chunksThatNeedToBeGenerated = (radius/16) * (radius/16) * 4;
		this.tempXBlock = radius;
		this.tempZBlock = radius;
		this.firstStage=true;
		this.secondStage=false;
		this.chunksPerRun=chunksPerRun;
		this.startGenerating=true;
		ms.broadcast("generating!");
	}

	@Override
	public void run() {
		duration++;
		if(duration% 10 ==0){//take a break every 20 seconds
			return;
			
		}
		else{
			if(isGeneratingDone){
				ms.sendToOPS("Done Preloading Chunks!");
				isGeneratingDone=false;
			}
			else if(startGenerating){
				for(int i = 0; i<chunksPerRun; i++){//TODO make everything run in this for loop
					world.loadChunk(tempXBlock, tempZBlock, true);
					Location loc = new Location(world, tempXBlock, world.getHighestBlockYAt(tempXBlock, tempZBlock), tempZBlock);
					Bukkit.getServer().getPlayer("G0ldManPox").teleport(loc);
					if(tempXBlock< (-radius)){
						firstStage=false;
						secondStage=true;
						isNeg=true;
					}
					else if(tempXBlock > radius){
						
						firstStage=false;
						secondStage=false;
						isGeneratingDone=true;
					}

					if(firstStage){
						if(isNeg){
							tempZBlock-=16;
							if(tempZBlock ==0){
								tempXBlock-=16;
								isNeg=false;
							}
						}
						else{
							tempZBlock+=16;
							if(tempZBlock==radius){
								tempXBlock-=16;
								isNeg=true;
							}
						}

					}
					else if(secondStage){
						if(isNeg){
							tempZBlock-=16;
							if(tempZBlock==-radius){
								tempXBlock +=16;
								isNeg=false;
							}
						}
						else{
							tempZBlock+=16;
							if(tempZBlock==0){
								tempXBlock +=16;
								isNeg=true;
							}
						}

					}
					
					
					
				}
			}
			else{
				ms.broadcast("ah");
			}



		}


	}




}
