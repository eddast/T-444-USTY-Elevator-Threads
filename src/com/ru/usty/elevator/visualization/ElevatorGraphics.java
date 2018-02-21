package com.ru.usty.elevator.visualization;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.BufferUtils;
import com.ru.usty.elevator.ElevatorScene;

import java.nio.FloatBuffer;

public class ElevatorGraphics implements ApplicationListener{

	FloatBuffer vertexBuffer;
	ElevatorScene elevatorScene;

	public ElevatorGraphics(ElevatorScene elevatorScene) {
		this.elevatorScene = elevatorScene;
	}

	@Override
	public void create() {
		Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		Gdx.gl11.glClearColor(0.4f, 0.6f, 1.0f, 1.0f);
		
		vertexBuffer = BufferUtils.newFloatBuffer(8);
		vertexBuffer.put(new float[] {-0.5f,-0.5f, -0.5f,0.5f, 0.5f,-0.5f, 0.5f,0.5f});
		vertexBuffer.rewind();
	}

	@Override
	public void dispose() {
	// TODO Auto-generated method stub
	}

	@Override
	public void pause() {
	// TODO Auto-generated method stub
	}

	float pos = 100.0f;

	@Override
	public void render() {
 
		pos += 1;

		Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT);


		Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);
		Gdx.gl11.glLoadIdentity();
		Gdx.glu.gluOrtho2D(Gdx.gl10, 0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight());

		Gdx.gl11.glColor4f(0.6f, 0.0f, 0.0f, 1.0f);

		Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, vertexBuffer);

		Gdx.gl11.glColor4f(0.6f, 0.0f, 0.0f, 1.0f);
		Gdx.gl11.glPushMatrix();
		Gdx.gl11.glTranslatef(350f, -50f, 0.0f);

		for(int elevatorNum = 0; elevatorNum < elevatorScene.getNumberOfElevators(); elevatorNum++) {

			Gdx.gl11.glTranslatef(80f, 0.0f, 0.0f);
			Gdx.gl11.glPushMatrix();
			for(int floorNum = 0; floorNum < elevatorScene.getNumberOfFloors(); floorNum++) {

				Gdx.gl11.glTranslatef(0.0f, 110f, 0.0f);
				Gdx.gl11.glPushMatrix();
				Gdx.gl11.glScalef(70.0f, 100.0f, 100.0f);
				Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
				Gdx.gl11.glPopMatrix();
				if(floorNum == elevatorScene.getCurrentFloorForElevator(elevatorNum)) {

					if(elevatorScene.isElevatorOpen(elevatorNum)) {
						Gdx.gl11.glColor4f(0.8f, 0.8f, 1.0f, 1.0f);
					}
					else {
						Gdx.gl11.glColor4f(0.3f, 0.3f, 0.6f, 1.0f);
					}
					Gdx.gl11.glPushMatrix();
					Gdx.gl11.glScalef(60.0f, 90.0f, 100.0f);
					Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
					Gdx.gl11.glPopMatrix();

					Gdx.gl11.glPushMatrix();
					Gdx.gl11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
					Gdx.gl11.glTranslatef(-15.0f, -60f, 0.0f);
					for(int peopleNum = 0; peopleNum < elevatorScene.getNumberOfPeopleInElevator(elevatorNum); peopleNum++) {
						if(peopleNum == 3) {
							Gdx.gl11.glTranslatef(30.0f, -90f, 0.0f);
						}
						Gdx.gl11.glTranslatef(0.0f, 30f, 0.0f);
						Gdx.gl11.glPushMatrix();
						Gdx.gl11.glScalef(20.0f, 20.0f, 100.0f);
						Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
						Gdx.gl11.glPopMatrix();
					}
					Gdx.gl11.glPopMatrix();
					Gdx.gl11.glColor4f(0.6f, 0.0f, 0.0f, 1.0f);
				}
			}
			Gdx.gl11.glPopMatrix();
		}

		Gdx.gl11.glTranslatef(20.0f, -30f, 0.0f);
		for(int floorNum = 0; floorNum < elevatorScene.getNumberOfFloors(); floorNum++) {

			if(elevatorScene.isButtonPushedAtFloor(floorNum)) {
				Gdx.gl11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			}
			else {
				Gdx.gl11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
			}
			Gdx.gl11.glTranslatef(0.0f, 110f, 0.0f);
			Gdx.gl11.glPushMatrix();
			Gdx.gl11.glTranslatef(30.0f, 0f, 0.0f);
			Gdx.gl11.glPushMatrix();
			Gdx.gl11.glScalef(10.0f, 10.0f, 100.0f);
			Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
			Gdx.gl11.glPopMatrix();
			Gdx.gl11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
			Gdx.gl11.glTranslatef(0.0f, 60.0f, 0.0f);
			for(int peopleNum = 0; peopleNum < elevatorScene.getNumberOfPeopleWaitingAtFloor(floorNum); peopleNum++) {

				int mod = peopleNum % 4;
				switch(mod) {
				case 0:
					Gdx.gl11.glTranslatef(20.0f, -60.0f, 0.0f);
					break;
				case 1: case 2: case 3:
					Gdx.gl11.glTranslatef(0.0f, 20.0f, 0.0f);
					break;
				}
				Gdx.gl11.glPushMatrix();
				Gdx.gl11.glScalef(15.0f, 15.0f, 100.0f);
				Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
				Gdx.gl11.glPopMatrix();
			}
			Gdx.gl11.glPopMatrix();
		}

		Gdx.gl11.glPopMatrix();

		Gdx.gl11.glPushMatrix();
		Gdx.gl11.glTranslatef(400.0f, -80f, 0.0f);
		for(int floorNum = 0; floorNum < elevatorScene.getNumberOfFloors(); floorNum++) {

			Gdx.gl11.glTranslatef(0.0f, 110f, 0.0f);
			Gdx.gl11.glPushMatrix();
			Gdx.gl11.glTranslatef(0.0f, 60f, 0.0f);
			Gdx.gl11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
			for(int peopleNum = 0; peopleNum < elevatorScene.getExitedCountAtFloor(floorNum); peopleNum++) {

				int mod = peopleNum % 4;
				switch(mod) {
				case 0:
					Gdx.gl11.glTranslatef(-20.0f, -60.0f, 0.0f);
					break;
				case 1: case 2: case 3:
					Gdx.gl11.glTranslatef(0.0f, 20.0f, 0.0f);
					break;
				}
				Gdx.gl11.glPushMatrix();
				Gdx.gl11.glScalef(15.0f, 15.0f, 100.0f);
				Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
				Gdx.gl11.glPopMatrix();
			}
			Gdx.gl11.glPopMatrix();
		}
		Gdx.gl11.glPopMatrix();

	}

	@Override
	public void resize(int arg0, int arg1) {
	// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
	// TODO Auto-generated method stub

	}

}