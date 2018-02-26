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
	private static final int ELEVATOR_WAIT_LONG = 450;
	private static final int ELEVATOR_WAIT_SHORT = 200;
	
	// Elevators are referenced by an id
	// And oriented by ascending or descending
	Elevator (int elevatorID) {
		this.thisElevator = elevatorID;
		ascending = false;
	}
	
	// Make elevator wait for long or short time
	// This makes visualization run smoothly
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
			if (ElevatorScene.elevatorsShouldStop) { return; }
			
			// Get current floor of elevator in each iteration
			int isAtFloor = ElevatorScene.scene.getCurrentFloorForElevator(thisElevator);
			int numberOfPeopleInElevator = ElevatorScene.scene.getNumberOfPeopleInElevator(thisElevator);

			// Elevator checks if any person in it wishes to get out at the floor it's on
			// Releases as many people as are in elevator, then if some people did not exit,
			// Elevator acquires the semaphore back so that there are no excess releases
			if (!ElevatorScene.scene.elevatorIsEmpty(thisElevator)) {
				try {
					ElevatorScene.waitInElevatorSemaphoreForFloor[isAtFloor][thisElevator].release(numberOfPeopleInElevator);
					elevatorWait(false);
					numberOfPeopleInElevator = ElevatorScene.scene.getNumberOfPeopleInElevator(thisElevator);
					ElevatorScene.waitInElevatorSemaphoreForFloor[isAtFloor][thisElevator].acquire(numberOfPeopleInElevator);
					
				} catch (InterruptedException e) { e.printStackTrace(); }
				
				elevatorWait(true);
			}
			
			// Check whether elevator is at top or bottom floor
			// If so change orientation of the elevator for next iteration
			int bottomFloor = 0; int topFloor = ElevatorScene.scene.getNumberOfFloors() - 1; 
			if ( (isAtFloor == topFloor) || (isAtFloor == bottomFloor) ) {
					this.ascending = !this.ascending;
			} int orientation = (this.ascending) ? 1 : 0;
			
			
			// Elevator takes in persons while there's room in elevator
			// Elevator only takes in persons that are going in same direction as elevator (BONUS: up/down button)
			// Once no other person enters elevator, the loop brakes
			while	(!ElevatorScene.scene.elevatorIsFull(thisElevator)) {
				try {
					ElevatorScene.oneElevatorOpensAtTimeMutex.acquire();
						// critical section
						ElevatorScene.scene.currentlyOpenedElevator = thisElevator;
						ElevatorScene.waitForElevatorSemaphoreAtFloor[isAtFloor][orientation].release();
						Thread.sleep(50);
						int newPopulation = ElevatorScene.scene.getNumberOfPeopleInElevator(thisElevator);
						if (numberOfPeopleInElevator == newPopulation) {
							ElevatorScene.waitForElevatorSemaphoreAtFloor[isAtFloor][orientation].acquire();
							ElevatorScene.oneElevatorOpensAtTimeMutex.release(); break;
						} else { numberOfPeopleInElevator = newPopulation; }
					ElevatorScene.oneElevatorOpensAtTimeMutex.release();
					
				} catch (InterruptedException e) { e.printStackTrace(); }
				
				elevatorWait(false);
			}
			
			
			// Now that elevator has done it's duty on this floor it moves to the next
			if (this.ascending)	{ ElevatorScene.scene.incrementElevatorFloor(thisElevator); }
			else					{ ElevatorScene.scene.decrementElevatorFloor(thisElevator); }
			
			elevatorWait(true);
		}
	}

}
