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
	
	// Elevators are referenced by an id
	// And oriented by ascending or descending
	Elevator (int elevatorID) {
		this.thisElevator = elevatorID;
		ascending = false;
	}
	
	// Implements the run function to describe Elevator' functionality when started
	// Elevator attempts to release it's people as well as taking people at each floor
	// Elevator changes floors after each iteration
	@Override public void run() {
		
		while (true) {
			
			// Get people currently in elevator and which floor the elevator is at in each iteration
			int numOfPeopleInElevator = ElevatorScene.scene.getNumberOfPeopleInElevator(thisElevator);
			int elevatorCurrentFloor	 = ElevatorScene.scene.getCurrentFloorForElevator(thisElevator);
			
			// Elevator checks if any person in it wishes to get out at floor
			// Decrement elevator population if anyone got out
			/* TODO */
			
			// Elevator checks if any person is on floor waiting for it
			// Lets that person in if there's room in elevator
			// Increment elevator population if anyone got in
			/* TODO */
			
			// Check whether elevator is at top or bottom floor
			// If so change orientation of the elevator for next iteration
			int bottomFloor = 0;
			int topFloor = ElevatorScene.scene.getNumberOfFloors() - 1; 
			if ((elevatorCurrentFloor == topFloor) || (elevatorCurrentFloor == bottomFloor)) {
					this.ascending = !this.ascending;
			}
			
			// Now that elevator has done it's duty at this floor, it goes to the next
			if (this.ascending)	{ ElevatorScene.scene.incrementElevatorFloor(thisElevator); }
			else					{ ElevatorScene.scene.decrementElevatorFloor(thisElevator); }
		}
	}

}
