/*  =================================================================
 *
 * 			T-444-USTY Grunnatridi Styrikerfa
 * 			Reykjavik University
 * 			Programming Assignment 1: ElevatorThreads
 * 			Assignment Due: 01.03.2018
 * 			Author: Edda Steinunn Rúnarsdóttir
 * 			File: ElevatorScene.java
 *
 *  ================================================================= */

package com.ru.usty.elevator;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;


// Mimics an environment of some number of floors, elevators and persons
public class ElevatorScene {
	
	/**************************
	 * 	VARIABLE DECLARATIONS
	 **************************/
	
	/* BASE VARIABLE TIME VISUALIZATION WAITS */
	public static final int VISUALIZATION_WAIT_TIME = 500;
	
	/* ELEVATORSCENE CLASS ACCESS BY OTHER CLASSES */
	public static ElevatorScene scene;
	
	/* ENVIRONMENT SPECIFICATIONS */
	private int numberOfFloors;
	private int numberOfElevators;
	public int maximumElevatorPopulation;
	public int currentlyOpenedElevator;
	public static boolean elevatorsShouldStop;
	public ArrayList<Thread> elevators = null;
	
	/* SYSTEM VARIABLE COUNT MANAGEMENT */
	public ArrayList<Integer> personCount;
	public ArrayList<Integer> exitedCount = null;
	public ArrayList<Integer> elevatorPosition;
	public ArrayList<Integer> elevatorPopulation;
	
	/* SEMAPHORES/MUTEXES FOR THREAD SAFETY (MUTUAL EXCLUSION) */
	public static Semaphore exitedCountMutex;
	public static Semaphore personCountMutex;
	public static Semaphore oneElevatorOpensAtTimeMutex;
	public static ArrayList <Semaphore> elevatorPositionMutex;
	public static ArrayList <Semaphore> elevatorPopulationMutex;
	public static Semaphore waitInElevatorSemaphoreForFloor[][];
	public static Semaphore waitForElevatorSemaphoreAtFloor[][];

	
	
	/************************************
	 * 		BASE FUNCTIONS (CHANGED)
	 ************************************/
	
	// Initializes environment and variables necessary for a given problem
	public void restartScene(int numberOfFloors, int numberOfElevators) {

		/* SET THE SCENE */
		ElevatorScene.scene = this;
		/* INITIALIZE ENVIRONMENT */
		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;
		this.maximumElevatorPopulation = 6;
		this.currentlyOpenedElevator = 0;
		
		// Kill elevators from previous scene still running
		// Join elevator threads to main thread to wait for them to die (brutal)
		ElevatorScene.elevatorsShouldStop = true;
		if(elevators != null) {
			for (Thread elevator : elevators) {
				if (elevator != null) {
					if (elevator.isAlive() ) {
						try								{ elevator.join(); }
						catch (InterruptedException e)	{ e.printStackTrace(); }	
					}
				}
			}
		}
		

		/* INITIALIZE FLOORS VARIABLES */
		// person count and exited person count per floor
		personCount = new ArrayList<Integer>();
		for(int i = 0; i < numberOfFloors; i++) {
			this.personCount.add(0);
		}
		if(exitedCount == null)	{ exitedCount = new ArrayList<Integer>(); }
		else						{ exitedCount.clear(); }
		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.exitedCount.add(0);
		}
		
				
		/* INITIALIZE MUTEXES AND SEMAPHORES FOR ENVIRONMENT */
		ElevatorScene.exitedCountMutex				=		new Semaphore(1);
		ElevatorScene.personCountMutex				=		new Semaphore(1);
		ElevatorScene.oneElevatorOpensAtTimeMutex	=		new Semaphore(1);
		elevatorPositionMutex = new ArrayList<Semaphore>();
		elevatorPopulationMutex = new ArrayList<Semaphore>();
		for(int i = 0; i < numberOfElevators; i++) {
			elevatorPositionMutex.add(new Semaphore(1));
			elevatorPopulationMutex.add(new Semaphore(1));
		}
		waitForElevatorSemaphoreAtFloor = new Semaphore[numberOfFloors][2];
		for(int i = 0; i < numberOfFloors; i++) {
			waitForElevatorSemaphoreAtFloor[i][0] = new Semaphore(0);
			waitForElevatorSemaphoreAtFloor[i][1] = new Semaphore(0);
		}
		waitInElevatorSemaphoreForFloor = new Semaphore[numberOfFloors][numberOfElevators];
		for(int i = 0; i < numberOfFloors; i++) {
			for(int j = 0; j < numberOfElevators; j++) {
				waitInElevatorSemaphoreForFloor[i][j] = new Semaphore(0);
			}
		}
		elevatorsShouldStop = false;
		
		/* INITIALIZE ELEVATOR VARIABLES */
		// Position, population and elevator threads
		elevatorPosition = new ArrayList<Integer>();
		elevatorPopulation = new ArrayList<Integer>();
		elevators = new ArrayList<Thread>();
		for(int i = 0; i < numberOfElevators; i++) {
			this.elevatorPosition.add(0);
			this.elevatorPopulation.add(0);
			elevators.add(new Thread(new Elevator(i)));
			elevators.get(i).start();
		}

	}

	// Adds a person to the environment waiting at some floor for an elevator
	public Thread addPerson(int sourceFloor, int destinationFloor) {
		Thread personThread = new Thread(new Person(sourceFloor, destinationFloor));
		personThread.start(); incrementNumberOfPeopleWaitingAtFloor(sourceFloor);
		
		return personThread;
	}

	// Gets the number of people in a given elevator
	public int getNumberOfPeopleInElevator(int elevator) { 
		if (elevator < elevatorPopulation.size())	{ return elevatorPopulation.get(elevator); }
		else 										{ return elevatorPopulation.get(0); }
	}
	// Gets the position/the current floor for a given elevator
	public int getCurrentFloorForElevator(int elevator) { 
		if (elevator < elevatorPosition.size()) 		{ return elevatorPosition.get(elevator); }
		else											{ return elevatorPosition.get(0); }
	}
	
	
	/****************************************
	 * 			NEW HELPER FUNCTIONS
	 ****************************************/
	
	// Determines whether a given elevator is full
	public boolean elevatorIsFull (int elevator) {
		return getNumberOfPeopleInElevator(elevator) >= maximumElevatorPopulation;
	}
	
	// Determines whether a given elevator is empty
	public boolean elevatorIsEmpty (int elevator) {
		return getNumberOfPeopleInElevator(elevator) == 0;
	}
	
	/**
	 * Following functions have a critical section protected by semaphore mutexes
	 * This is because increment and decrement of same values cannot happen
	 * simultaneously by multiple threads
	 */
	
	// When elevator has picked up people person count is decremented
	public void decrementNumberOfPeopleWaitingAtFloor (int floor) {
		try {
			ElevatorScene.personCountMutex.acquire();
				// critical section
				personCount.set(floor, (personCount.get(floor)-1));
			ElevatorScene.personCountMutex.release();
			
		} catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	// When people enter and can't get in elevator, persons waiting are incremented
	public void incrementNumberOfPeopleWaitingAtFloor (int floor) {
		try {
			ElevatorScene.personCountMutex.acquire();
				// critical section
				personCount.set(floor, (personCount.get(floor)+1));
			ElevatorScene.personCountMutex.release();
			
		} catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	// Decrement at which floor a given elevator is on
	public void decrementElevatorFloor (int elevator) {
		try {
			ElevatorScene.elevatorPositionMutex.get(elevator).acquire();
				// critical section
				elevatorPosition.set(elevator, (elevatorPosition.get(elevator)-1));
			ElevatorScene.elevatorPositionMutex.get(elevator).release();
			
		} catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	// Increment at which floor a given elevator is on
	public void incrementElevatorFloor (int elevator) {

		if(elevatorPosition.get(elevator) != getNumberOfFloors() - 1) {
			try {
				ElevatorScene.elevatorPositionMutex.get(elevator).acquire();
					// critical section
					elevatorPosition.set(elevator, (elevatorPosition.get(elevator)+1));
				ElevatorScene.elevatorPositionMutex.get(elevator).release();
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
	// Decrement elevator's person count for a given elevator
	public void decrementElevatorPopulation (int elevator) {
		try {
			ElevatorScene.elevatorPopulationMutex.get(elevator).acquire();
				// critical section
				elevatorPopulation.set(elevator, elevatorPopulation.get(elevator)-1);
			ElevatorScene.elevatorPopulationMutex.get(elevator).release();
				
		} catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	// Decrement elevator's person count for a given elevator
	public void incrementElevatorPopulation (int elevator) {
		try {
			ElevatorScene.elevatorPopulationMutex.get(elevator).acquire();
				// critical section
				elevatorPopulation.set(elevator, elevatorPopulation.get(elevator)+1);
			ElevatorScene.elevatorPopulationMutex.get(elevator).release();
				
		} catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	// Gets currently opened elevator
	public int getElevatorCurrentlyOpen() { return this.currentlyOpenedElevator; }
	
	/*****************************************************
	 * 			PROJECT BASE FUNCTIONS (UNCHANGED)
	 *****************************************************/

	// Gets number of floors in problem
	public int getNumberOfFloors() { return numberOfFloors; }
	
	// Gets number of persons waiting at a given floor
	public int getNumberOfPeopleWaitingAtFloor(int floor) { 
		if (floor < personCount.size()) { 
			return personCount.get(floor);
		} else {
			return 0;
		}
	}

	// Sets number of floors in problem
	public void setNumberOfFloors(int numberOfFloors) { this.numberOfFloors = numberOfFloors; }

	// Gets number of elevators in problem
	public int getNumberOfElevators() { return numberOfElevators; }

	// Sets number of elevators in problem
	public void setNumberOfElevators(int numberOfElevators) { this.numberOfElevators = numberOfElevators; }

	// Checks whether elevator is currently open (i.e. at floor where persons await)
	public boolean isElevatorOpen(int elevator) { return isButtonPushedAtFloor(getCurrentFloorForElevator(elevator)); }
	
	// Checks whether people are awaiting elevator
	public boolean isButtonPushedAtFloor(int floor) { return (getNumberOfPeopleWaitingAtFloor(floor) > 0); }
	
	// Gets how many have exited at a given floor
	public int getExitedCountAtFloor(int floor) {
		if (floor < getNumberOfFloors() && floor < exitedCount.size())	{ return exitedCount.get(floor); }
		else																{ return 0; }
	}

	// Let the system know that a person has exited.
	// Person calls it when let off elevator before thread returns
	public void personExitsAtFloor(int floor) {
		try {
			exitedCountMutex.acquire();
				// critical section
				exitedCount.set(floor, (exitedCount.get(floor) + 1));
			exitedCountMutex.release();

		} catch (InterruptedException e) { e.printStackTrace(); }
	}
	
}
