package com.goldornetwork.uhc.managers.GameModeManager;

public enum State {
	NOT_RUNNING, OPEN, CLOSED, SCATTER, INGAME, ENDING;

	private static State currentState;
	
	public static void setState(State state){
		currentState = state;
	}
	public static State getState(){
		if(currentState==null){
			currentState=State.NOT_RUNNING;
		}
		return currentState;
	}
	
}
