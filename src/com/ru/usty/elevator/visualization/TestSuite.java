package com.ru.usty.elevator.visualization;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.ru.usty.elevator.ElevatorScene;

public class TestSuite {

	private static ElevatorScene visualizationScene;

	public static ElevatorScene startVisualization() {

		visualizationScene = new ElevatorScene();

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				new LwjglApplication(new ElevatorGraphics(visualizationScene), "The Threaded Elevator Machine", 1200, 560, false);
			}
		}).start();

		return visualizationScene;
	}

	public static void runTest(final int sceneNumber) {

		TestSuite.initScene(sceneNumber);

		Thread testThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					TestSuite.runScene(sceneNumber);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		testThread.start();
		
		try {
			testThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Feel free to change the initScene and runScene functions
	 * for testing but they will be switched out for our own
	 * test suite during grading
	 */

	private static void initScene(final int sceneNumber) {
		switch(sceneNumber) {
		case 0:
			visualizationScene.restartScene(2, 1);
			break;
		case 1:
			visualizationScene.restartScene(4, 1);
			break;
		case 2:
			visualizationScene.restartScene(2, 1);
			break;
		case 3:
			visualizationScene.restartScene(4, 1);
			break;
		case 4:
			visualizationScene.restartScene(4, 3);
			break;
		case 5:
			visualizationScene.restartScene(3, 5);
			break;
		case 6:
			visualizationScene.restartScene(3, 5);
			break;
		case 7:
			visualizationScene.restartScene(5, 2);
			break;
		case 8:
			visualizationScene.restartScene(5, 3);
			break;
		case 9:
			visualizationScene.restartScene(2, 1);
			break;
		default:
			visualizationScene.restartScene(1, 1);
			break;
		}
	}

	private static void runScene(final int sceneNumber) throws InterruptedException {

		ArrayList<Thread> personThreads = new ArrayList<Thread>();
		
		Random random = new Random(234645236);

		switch(sceneNumber) {
		case 0:
			for(int i = 0; i < 13; i++) {
				personThreads.add(visualizationScene.addPerson(0, 1));
			}
			for(int i = 0; i < 13; i++) {
				Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME);
				personThreads.add(visualizationScene.addPerson(0, 1));
			}
			for(int i = 0; i < 37; i++) {
				Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME / 4);
				personThreads.add(visualizationScene.addPerson(0, 1));
			}
			for(int i = 0; i < 7; i++) {
				Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME);
				personThreads.add(visualizationScene.addPerson(0, 1));
			}
			break;
		case 1:
			for(int i = 0; i < 8; i++) {
				int floorOut = random.nextInt(visualizationScene.getNumberOfFloors() - 1) + 1;
				personThreads.add(visualizationScene.addPerson(0, floorOut));
			}
			for(int i = 0; i < 30; i++) {
				Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME);
				int floorOut = random.nextInt(visualizationScene.getNumberOfFloors() - 1) + 1;
				personThreads.add(visualizationScene.addPerson(0, floorOut));
			}
			break;
		case 2:
			for(int i = 0; i < 12; i++) {
				int floorIn = random.nextInt(2);
				personThreads.add(visualizationScene.addPerson(floorIn, (floorIn + 1) % 2));
			}
			for(int i = 0; i < 40; i++) {
				Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME / 4);
				int floorIn = random.nextInt(2);
				personThreads.add(visualizationScene.addPerson(floorIn, (floorIn + 1) % 2));
			}
			break;
		case 3:
			for(int i = 0; i < 20; i++) {
				int floorIn = random.nextInt(visualizationScene.getNumberOfFloors());
				int floorOut = random.nextInt(visualizationScene.getNumberOfFloors() - 1);
				if(floorOut >= floorIn) { floorOut++; }
				personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
			}
			for(int i = 0; i < 60; i++) {
				Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME / 2);
				int floorIn = random.nextInt(visualizationScene.getNumberOfFloors());
				int floorOut = random.nextInt(visualizationScene.getNumberOfFloors() - 1);
				if(floorOut >= floorIn) { floorOut++; }
				personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
			}
			break;
		case 4:
			for(int i = 0; i < 30; i++) {
				int floorIn = random.nextInt(visualizationScene.getNumberOfFloors());
				int floorOut = random.nextInt(visualizationScene.getNumberOfFloors() - 1);
				if(floorOut >= floorIn) { floorOut++; }
				personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
			}
			for(int i = 0; i < 150; i++) {
				Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME / 4);
				int floorIn = random.nextInt(visualizationScene.getNumberOfFloors());
				int floorOut = random.nextInt(visualizationScene.getNumberOfFloors() - 1);
				if(floorOut >= floorIn) { floorOut++; }
				personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
			}
			break;
		case 5:
			for(int i = 0; i < 8; i++) {
				int floorOut = random.nextInt(2);
				if(floorOut == 1) { floorOut = 2; }
				personThreads.add(visualizationScene.addPerson(1, floorOut));
			}
			for(int i = 0; i < 80; i++) {
				int floorIn = random.nextInt(2);
				int floorOut = 2;
				if(floorIn == 1) { floorIn = 2; floorOut = 0; }
				personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
			}
			for(int i = 0; i < 40; i++) {
				Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME / 2);
				for(int j = 0; j < 8; j++) {
					int floorIn = random.nextInt(2);
					int floorOut = 2;
					if(floorIn == 1) { floorIn = 2; floorOut = 0; }
					personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
				}
				int floorOut = random.nextInt(2);
				if(floorOut == 1) { floorOut = 2; }
				personThreads.add(visualizationScene.addPerson(1, floorOut));
			}
			break;
		case 6:
			for(int i = 0; i < 8; i++) {
				int floorIn = random.nextInt(visualizationScene.getNumberOfFloors());
				int floorOut = random.nextInt(visualizationScene.getNumberOfFloors() - 1);
				if(floorOut >= floorIn) { floorOut++; }
				personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
			}
			for(int i = 0; i < 12; i++) {
				int floorIn = random.nextInt(2);
				int floorOut = 2;
				if(floorIn == 1) { floorIn = 2; floorOut = 0; }
				personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
			}
			for(int i = 0; i < 30; i++) {
				Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME / 2);
				for(int j = 0; j < 4; j++) {
					int floorIn = random.nextInt(2);
					int floorOut = 2;
					if(floorIn == 1) { floorIn = 2; floorOut = 0; }
					personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
				}
				int floorIn = random.nextInt(visualizationScene.getNumberOfFloors());
				int floorOut = random.nextInt(visualizationScene.getNumberOfFloors() - 1);
				if(floorOut >= floorIn) { floorOut++; }
				personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
			}
			break;
		case 7:
			for(int i = 0; i < 20; i++) {
				int floorIn = random.nextInt(visualizationScene.getNumberOfFloors());
				int floorOut = random.nextInt(visualizationScene.getNumberOfFloors() - 1);
				if(floorOut >= floorIn) { floorOut++; }
				personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
			}
			for(int i = 0; i < 160; i++) {
				Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME / 7);
				int floorIn = random.nextInt(visualizationScene.getNumberOfFloors());
				int floorOut = random.nextInt(visualizationScene.getNumberOfFloors() - 1);
				if(floorOut >= floorIn) { floorOut++; }
				personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
			}
			break;
		case 8:
			for(int i = 0; i < 20; i++) {
				int floorIn = random.nextInt(visualizationScene.getNumberOfFloors());
				int floorOut = random.nextInt(visualizationScene.getNumberOfFloors() - 1);
				if(floorOut >= floorIn) { floorOut++; }
				personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
			}
			for(int i = 0; i < 120; i++) {
				Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME / 6);
				int floorIn = random.nextInt(visualizationScene.getNumberOfFloors());
				int floorOut = random.nextInt(visualizationScene.getNumberOfFloors() - 1);
				if(floorOut >= floorIn) { floorOut++; }
				personThreads.add(visualizationScene.addPerson(floorIn, floorOut));
			}
			break;
		case 9:
			for(int i = 0; i < 8; i++) {
				personThreads.add(visualizationScene.addPerson(0, 1));
			}
			for(int i = 0; i < 20; i++) {
				Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME);
				personThreads.add(visualizationScene.addPerson(0, 1));
			}
			break;
		default:
			for(int i = 0; i < 20; i++) {
				Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME);
				personThreads.add(visualizationScene.addPerson(0, 1));
			}
			break;
		}
		for(Thread thread : personThreads) {
			if(thread != null) {
				if(thread.isAlive()){
					thread.join();
				}
			}
		}
	}
}
