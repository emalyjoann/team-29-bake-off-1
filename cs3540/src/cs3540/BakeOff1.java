package cs3540;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Collections;
import processing.core.PApplet;
import processing.core.PVector;

public class BakeOff1 extends PApplet {
	// when in doubt, consult the Processsing reference:
	// https://processing.org/reference/
	// The argument passed to main must match the class name
	public static void main(String[] args) {
		// Tell processing what class we want to run.
		PApplet.main("cs3540.BakeOff1");
	}

	int margin = 20; // set the margin around the squares
	int padding = 50; // padding between buttons and also their width/height
	int buttonSize = 40; // padding between buttons and also their width/height
	ArrayList<Integer> trials = new ArrayList<Integer>(); // contains the order of buttons that activate in the test
	int trialNum = 0; // the current trial number (indexes into trials array above)
	int startTime = 0; // time starts when the first click is captured
	int finishTime = 0; // records the time of the final click
	int hits = 0; // number of successful clicks
	int misses = 0; // number of missed clicks
	Robot robot; // initialized in setup
	int participantID = 0; //Jared:0 Emma:1 Isaac:2 
	PVector startPos = new PVector();
	int trialTimeStart =0;

	int numRepeats = 10; // sets the number of times each button repeats in the test

	/**
	 * https://processing.org/reference/settings_.html#:~:text=The%20settings()%20method%20runs,commands%20in%20the%20Processing%20API.
	 */
	public void settings() {
		size(700, 700);
	}

	/**
	 * // https://processing.org/reference/setup_.html
	 */
	public void setup() {
		startPos = new PVector(mouseX,mouseY);
		trialTimeStart = millis();
		// noCursor(); // hides the system cursor if you want
		noStroke(); // turn off all strokes, we're just using fills here (can change this if you
					// want)
		textFont(createFont("Arial", 16)); // sets the font to Arial size 16
		textAlign(CENTER);
		frameRate(60); // normally you can't go much higher than 60 FPS.
		ellipseMode(CENTER); // ellipses are drawn from the center (BUT RECTANGLES ARE NOT!)
		// rectMode(CENTER); //enabling will break the scaffold code, but you might find
		// it easier to work with centered rects

		try {
			robot = new Robot(); // create a "Java Robot" class that can move the system cursor
		} catch (AWTException e) {
			e.printStackTrace();
		}

		// ===DON'T MODIFY MY RANDOM ORDERING CODE==
		for (int i = 0; i < 16; i++) // generate list of targets and randomize the order
			// number of buttons in 4x4 grid
			for (int k = 0; k < numRepeats; k++)
				// number of times each button repeats
				trials.add(i);

		Collections.shuffle(trials); // randomize the order of the buttons
		System.out.println("trial order: " + trials); // print out order for reference

		surface.setLocation(0, 0);// put window in top left corner of screen (doesn't always work)
	}

	public void draw() {
		background(0); // set background to black

		if (trialNum >= trials.size()) // check to see if test is over
		{
			float timeTaken = (finishTime - startTime) / 1000f;
			float penalty = constrain(((95f - ((float) hits * 100f / (float) (hits + misses))) * .2f), 0, 100);
			fill(255); // set fill color to white
			// write to screen (not console)
			text("Finished!", width / 2, height / 2);
			text("Hits: " + hits, width / 2, height / 2 + 20);
			text("Misses: " + misses, width / 2, height / 2 + 40);
			text("Accuracy: " + (float) hits * 100f / (float) (hits + misses) + "%", width / 2, height / 2 + 60);
			text("Total time taken: " + timeTaken + " sec", width / 2, height / 2 + 80);
			text("Average time for each button: " + nf((timeTaken) / (float) (hits + misses), 0, 3) + " sec", width / 2,
					height / 2 + 100);
			text("Average time for each button + penalty: "
					+ nf(((timeTaken) / (float) (hits + misses) + penalty), 0, 3) + " sec", width / 2,
					height / 2 + 140);
			return; // return, nothing else to do now test is over
		}

		fill(255); // set fill color to white
		text((trialNum + 1) + " of " + trials.size(), 40, 20); // display what trial the user is on

		for (int i = 0; i < 16; i++)// for all button
			drawButton(i); // draw button

		fill(255, 0, 0, 200); // set fill color to translucent red
		ellipse(mouseX, mouseY, 20, 20); // draw user cursor as a circle with a diameter of 20

	}

	public void mousePressed() // test to see if hit was in target!
	{
		if (trialNum >= trials.size()) // check if task is done
			return;

		if (trialNum == 0) // check if first click, if so, record start time
			{
				startTime = millis();
				//trialTimeStart = startTime;
			}

		if (trialNum == trials.size() - 1) // check if final click
		{
			finishTime = millis();
			// write to terminal some output:
			System.out.println("we're all done!");
		}

		Rectangle bounds = getButtonLocation(trials.get(trialNum));

		int hit = 0;
		// check to see if cursor was inside button
		if ((mouseX > bounds.x && mouseX < bounds.x + bounds.width)
				&& (mouseY > bounds.y && mouseY < bounds.y + bounds.height)) // test to see if hit was within bounds
		{
			//System.out.println("HIT! " + trialNum + " " + (millis() - startTime)); // success
			hits++;
			hit = 1;
		} else {
			//System.out.println("MISSED! " + trialNum + " " + (millis() - startTime)); // fail
			misses++;
		}
		
		/*
		 * 
		 * a) Trial number (increments with each button click)
b) Participant ID (Assign everyone on your team a number, 1 through 4)
c) X position of the cursor at beginning of trial (in pixels)
d) Y position of the cursor at beginning of trial (in pixels)
e) X position of the center of the target (in pixels)
f) Y position of the center of the target (in pixels)
g) Width of target (in pixels) 
h) Time taken (in seconds, e.g., 0.653)
i) Whether they clicked the target successfully (true of false, 0 or 1, or some similar encoding) 
		 * 
		 */
		
		float timeTaken = (millis()-trialTimeStart);
		timeTaken/=1000; //Convert ms to S
		System.out.printf("%d,%d,%.0f,%.0f,%d,%d,%d,%.3f,%d\n", 
				trialNum,participantID,startPos.x,startPos.y,bounds.x,bounds.y,bounds.height,
				timeTaken ,hit);
		startPos = new PVector(mouseX,mouseY);
		trialTimeStart = millis();

		trialNum++; // Increment trial number
		if(trialNum%20 ==0) {
			padding++;	
			System.out.println("Padding incresed");
		}
		if(trialNum%5==0) {
			buttonSize++;			
		}
		

		// in this example design, I move the cursor back to the middle after each click
		// Note. When running from eclipse the robot class affects the whole screen not
		// just the GUI, so the mouse may move outside of the GUI.
		// robot.mouseMove(width/2, (height)/2); //on click, move cursor to roughly
		// center of window!
	}

	// probably shouldn't have to edit this method
	public Rectangle getButtonLocation(int i) // for a given button ID, what is its location and size
	{
		int x = (i % 4) * (padding + buttonSize) + margin;
		int y = (i / 4) * (padding + buttonSize) + margin;

		return new Rectangle(x, y, buttonSize, buttonSize);
	}

	// you can edit this method to change how buttons appear
	public void drawButton(int i) {
		Rectangle bounds = getButtonLocation(i);

		if (trialNum == trials.size() - 1 || trials.get(trialNum) == trials.get(trialNum + 1)) {
			// Check if the current trial is the last one or if the next trial has the same target as the current one.
			if (trials.get(trialNum) == i) {
				fill(0, 255, 255); // Cyan
			} else {
				fill(200); // Gray
			}
		} else if (trials.get(trialNum) == i) {
			fill(0, 255, 255); // Cyan
		} else if (trials.get(trialNum + 1) == i) {
			fill(4, 129, 134); // Another color
		} else {
			fill(200); // Gray
		}

		rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}


	public void mouseMoved() {
		// can do stuff everytime the mouse is moved (i.e., not clicked)
		// https://processing.org/reference/mouseMoved_.html
	}

	public void mouseDragged() {
		// can do stuff everytime the mouse is dragged
		// https://processing.org/reference/mouseDragged_.html
	}

//	public void keyPressed() {
//		if (trialNum >= trials.size()) // Check if the test is over
//			return;
//
//		if (keyCode == UP) {
//			//moveCursorToNextSquare(0, -1); // Move the cursor up
//			mouseSnap(new PVector(0, -1)); 
//		} else if (keyCode == DOWN) {
//			//moveCursorToNextSquare(0, 1); // Move the cursor down
//			mouseSnap(new PVector(0, 1));
//		} else if (keyCode == LEFT) {
//			mouseSnap(new PVector(-1, 0));
////			moveCursorToNextSquare(-1, 0); // Move the cursor left
//		} else if (keyCode == RIGHT) {
//			mouseSnap(new PVector(1, 0));
////			moveCursorToNextSquare(1, 0); // Move the cursor right
//		}
//	}
	
	/* Snap the mouse in the direction given by dir.
	 * dir is a PVector of unit length meaning that all values are -1,0,1
	 * for both the x and y values
	 * Example call to move right would be mouseSnap(new PVector(1.0f,0.0f));
	 */
	public void mouseSnap(PVector dir) {
		int stepSize = 90;
		int unitX = (MouseInfo.getPointerInfo().getLocation().x-230)/stepSize;
		int remX = (MouseInfo.getPointerInfo().getLocation().x-230)%stepSize;
		if(remX >stepSize/2) {
			unitX++;
		}
		int unitY = (MouseInfo.getPointerInfo().getLocation().y-250)/stepSize;
		int remY = (MouseInfo.getPointerInfo().getLocation().y-250)%stepSize;
		if(remY >stepSize/2) {
			unitY++;
		}
		unitX += dir.x;
		unitY+= dir.y;
		robot.mouseMove(230+(90*unitX), 250+(90*unitY));
	}

	// Method to move the cursor to the next square
	void moveCursorToNextSquare(int dx, int dy) {
		int squareSize = padding + buttonSize; // Calculate the size of each square
		int gridWidth = 4; // Number of squares in each row
		int gridHeight = 4; // Number of squares in each column

		// Get the current position of the cursor
		int currentX = MouseInfo.getPointerInfo().getLocation().x;
		int currentY = MouseInfo.getPointerInfo().getLocation().y;

		// Calculate the new position of the cursor
		int newX = currentX + dx * squareSize;
		int newY = currentY + dy * squareSize;

		// Ensure the new position stays within the bounds of the button grid
		newX = constrain(newX, margin, margin + gridWidth * squareSize);
		newY = constrain(newY, margin, margin + gridHeight * squareSize);

		// Use the robot to move the cursor to the new position
		robot.mouseMove(newX, newY);
	}
}
