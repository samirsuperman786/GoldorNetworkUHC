package com.goldornetwork.uhc.managers.GameModeManager;

public enum State {
	
	NOT_RUNNING, OPEN, SCATTER, INGAME;

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
