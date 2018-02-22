/*  =================================================================
 *
 * 			T-444-USTY Grunnatridi Styrikerfa
 * 			Reykjavik University
 * 			Programming Assignment 1: ElevatorThreads
 * 			Assignment Due: 01.03.2018
 * 			Author: Edda Steinunn Rúnarsdóttir
 * 			File: Elevator.java
 *
 *  ================================================================= */

package com.ru.usty.elevator;

// An elevator thread with orientation and identification number
// Mimics behavior of an elevator ferrying people to other floors
public class Elevator implements Runnable{

	// An elevator needs an id and orientation
	public boolean ascending; public int thisElevator;
	private static final int ELEVATOR_WAIT_LONG = 300;
	private static final int ELEVATOR_WAIT_SHORT = 100;
	
	// Elevators are referenced by an id
	// And oriented by ascending or descending
	Elevator (int elevatorID) {
		this.thisElevator = elevatorID;
		ascending = false;
	}
	
	// Make elevator wait for long or short time
	// Makes visualization prettier
	private void elevatorWait(boolean isMovingBetweenFloors) {
		
		int waitTime;
		if (isMovingBetweenFloors)	{ waitTime = ELEVATOR_WAIT_LONG; }
		else							{ waitTime = ELEVATOR_WAIT_SHORT; }
		
		try								{ Thread.sleep(waitTime); }
		catch (InterruptedException e)	{ e.printStackTrace(); }
	}
	
	// Implements the run function to describe Elevator' functionality when started
	// Elevator attempts to release it's people as well as taking people at each floor
	// Elevator changes floors after each iteration
	@Override public void run() {
		
		while (true) {
			
			// Stop thread when elevators are explicitly notified to stop
			if (ElevatorScene.scene.elevatorsShouldStop) { return; }
			
			// Get current floor of elevator in each iteration
			int isAtFloor = ElevatorScene.scene.getCurrentFloorForElevator(thisElevator);
			int numberOfPeopleInElevator = ElevatorScene.scene.getNumberOfPeopleInElevator(thisElevator);
			
			// Elevator checks if any person in it wishes to get out at floor
			if (!ElevatorScene.scene.elevatorIsEmpty(thisElevator)) {
				try {
					ElevatorScene.oneElevatorOpensAtTimeMutex.acquire();
						// critical section
						ElevatorScene.waitInElevatorSemaphoreForFloor.get(isAtFloor).release(numberOfPeopleInElevator);
						elevatorWait(true);
						numberOfPeopleInElevator = ElevatorScene.scene.getNumberOfPeopleInElevator(thisElevator);
						ElevatorScene.waitInElevatorSemaphoreForFloor.get(isAtFloor).acquire(numberOfPeopleInElevator);
					ElevatorScene.oneElevatorOpensAtTimeMutex.release();
					
				} catch (InterruptedException e) { e.printStackTrace(); }
				elevatorWait(false);
			}
			
			// Elevator takes in person/s waiting if there are any
			// Lets in persons in IF there's room in elevator
			while	(!ElevatorScene.scene.noOneWaitingAtFloor(isAtFloor) &&
					 !ElevatorScene.scene.elevatorIsFull(thisElevator)) {
				try {
					ElevatorScene.oneElevatorOpensAtTimeMutex.acquire();
						// critical section
						ElevatorScene.waitForElevatorSemaphoreAtFloor.get(isAtFloor).release();
					ElevatorScene.oneElevatorOpensAtTimeMutex.release();
					elevatorWait(false);
					
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
			
			// Check whether elevator is at top or bottom floor
			// If so change orientation of the elevator for next iteration
			int bottomFloor = 0; int topFloor = ElevatorScene.scene.getNumberOfFloors() - 1; 
			if ( (isAtFloor == topFloor) || (isAtFloor == bottomFloor) ) {
					this.ascending = !this.ascending;
			}
			
			// Now that elevator is done on this floor it moves to the next
			if (this.ascending)	{ ElevatorScene.scene.incrementElevatorFloor(thisElevator); }
			else					{ ElevatorScene.scene.decrementElevatorFloor(thisElevator); }
			
			elevatorWait(true);
		}
	}

}
