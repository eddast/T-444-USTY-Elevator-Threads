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
	
	public int sourceFloor, destinationFloor;
	
	// Person knows where they came from and where they wish to go
	// Therefore each person thread carries source and destination floor
	public Person(int getsInAtFloor, int getsOutAtFloor) {
		
		this.sourceFloor = getsInAtFloor;
		this.destinationFloor = getsOutAtFloor;
	}
	
	// Implements the run function to describe Persons' functionality when started
	// A person awaits an elevator, then gets in, then awaits their floor, then exit
	@Override public void run() {
		
		int transitElevator = 0;
		
		// Acquire a semaphore, i.e. conduct a wait for an available elevator 
		// Only one can enter elevator at time, so we have extra mutex around the wait semaphore
		try { 
			
			ElevatorScene.oneEnterElevatorAtTimeMutex.acquire();
				// critical section
				ElevatorScene.waitForElevatorSemaphoreAtFloor.get(this.sourceFloor).acquire();
				transitElevator = ElevatorScene.scene.getElevatorCurrentlyOpen();
			ElevatorScene.oneEnterElevatorAtTimeMutex.release();
			
		} catch (InterruptedException e)	{ e.printStackTrace(); }
		
		// Once wait is through, we decrement the number of people waiting
		// And similarly increment people in the elevator
		ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);
		ElevatorScene.scene.incrementElevatorPopulation(transitElevator);
		
		// Acquire some in-elevator-waiting semaphore for destination floor
		// i.e. conduct a wait for elevator to release the person at desired floor
		try {
			ElevatorScene.oneExitsElevatorAtTimeMutex.acquire();
				// critical section
				ElevatorScene.waitInElevatorSemaphoreForFloor.get(destinationFloor).acquire();
			ElevatorScene.oneExitsElevatorAtTimeMutex.release();
			
		} catch (InterruptedException e) { e.printStackTrace(); }
		
		// Decrement number of people in elevator as this person is at desired floor
		// Then explicitly let people know that a person has exited at desired floor ( for visualization) 
		ElevatorScene.scene.decrementElevatorPopulation(transitElevator);
		ElevatorScene.scene.personExitsAtFloor(this.destinationFloor);
	}
}
