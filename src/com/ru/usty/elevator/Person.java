/*  =================================================================
 *
 * 			T-444-USTY Grunnatridi Styrikerfa
 * 			Reykjavik University
 * 			Programming Assignment 1: ElevatorThreads
 * 			Assignment Due: 01.03.2018
 * 			Author: Edda Steinunn Rúnarsdóttir
 * 			File: Person.java
 *
 *  ================================================================= */

package com.ru.usty.elevator;

// A person thread with source and destination floor
// Mimics behavior of a person taking an elevator to a floor
public class Person implements Runnable {
	
	private static final int IS_ASCENDING = 1;
	private static final int IS_DESCENDING = 0;
	public int sourceFloor, destinationFloor, orientation;
	
	public Person(int getsInAtFloor, int getsOutAtFloor) {
		
		// Person thread carries source and destination floor
		this.sourceFloor = getsInAtFloor;
		this.destinationFloor = getsOutAtFloor;
		
		// To implement up/down button, we need to know
		// whether person is ascending or descending
		if (this.sourceFloor <= destinationFloor)	{ this.orientation = IS_ASCENDING; }
		else											{ this.orientation = IS_DESCENDING; }
	}
	
	// Implements the run function to describe Persons' functionality when started
	// A person awaits an elevator, then gets in, then awaits their floor, then exit
	@Override public void run() {
		
		// Elevator person travels in
		int transitElevator = 0;
		
		// Acquire a semaphore, i.e. conduct a wait for an available elevator 
		// Semaphore is indexed by source floor to know where person is waiting
		// and (bonus) orientation, as in up/down button for elevator
		try { 
			ElevatorScene.waitForElevatorSemaphoreAtFloor[sourceFloor][orientation].acquire();
			
			// Once person boards an elevator, it increments elevator population
			// and decrements floor population (gets off a floor, gets in elevator)
			// Person realises the elevator it's in
			transitElevator = ElevatorScene.scene.getElevatorCurrentlyOpen();
			ElevatorScene.scene.incrementElevatorPopulation(transitElevator);
			ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);
			
		} catch (InterruptedException e)	{ e.printStackTrace(); }
		
		// Acquire in-elevator-waiting semaphore for destination floor for elevator its in
		// (i.e. person waits in elevator for source floor)
		try { ElevatorScene.waitInElevatorSemaphoreForFloor[destinationFloor][transitElevator].acquire(); }
		catch (InterruptedException e) { e.printStackTrace(); }
		
		// Decrement number of people in elevator as this person is at desired floor
		// Then explicitly let people know that a person has exited at desired floor ( for visualization) 
		ElevatorScene.scene.decrementElevatorPopulation(transitElevator);
		ElevatorScene.scene.personExitsAtFloor(this.destinationFloor);
	}
}
