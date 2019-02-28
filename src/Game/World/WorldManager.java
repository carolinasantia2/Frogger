package Game.World;

import Game.Entities.Dynamic.Player;
import Game.Entities.Static.LillyPad;
import Game.Entities.Static.Log;
import Game.Entities.Static.RareCandy;
import Game.Entities.Static.StaticBase;
import Game.Entities.Static.Tree;
import Game.Entities.Static.Turtle;
import Game.GameStates.State;
import Main.Handler;
import Resources.Images;
import UI.UIImageButton;
import UI.UIManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

/**
 * Literally the world. This class is very important to understand. Here we
 * spawn our hazards (StaticBase), and our tiles (BaseArea)
 * 
 * We move the screen, the player, and some hazards. How? Figure it out.
 */
public class WorldManager {

	private ArrayList<BaseArea> AreasAvailables; // Lake, empty and grass area (NOTE: The empty tile is just the "sand"
	// tile. Ik, weird name.)
	private ArrayList<StaticBase> StaticEntitiesAvailables; // Has the hazards: LillyPad, Log, Tree, and Turtle.

	public ArrayList<BaseArea> SpawnedAreas; // Areas currently on world
	private ArrayList<StaticBase> SpawnedHazards; // Hazards currently on world.

	private StaticBase lastSpawned;

	Long time;
	Boolean reset = true;

	Handler handler;

	private Player player; // How do we find the frog coordinates? How do we find the Collisions? This bad boy.

	UIManager object = new UIManager(handler);
	UI.UIManager.Vector object2 = object.new Vector();

	private ID[][] grid;
	private int gridWidth, gridHeight; // Size of the grid.
	private int movementSpeed; // Movement of the tiles going downwards.
	public boolean dead = false;
	private int counterCandy = 0;
	private int heart = 1;
	private UIManager uiManager;

	public WorldManager(Handler handler) {
		this.handler = handler;

		AreasAvailables = new ArrayList<>(); // Here we add the Tiles to be utilized.
		StaticEntitiesAvailables = new ArrayList<>(); // Here we add the Hazards to be utilized.

		AreasAvailables.add(new GrassArea(handler, 0));
		AreasAvailables.add(new WaterArea(handler, 0));
		AreasAvailables.add(new StreetArea(handler, 0));

		StaticEntitiesAvailables.add(new LillyPad(handler, 0, 0));
		StaticEntitiesAvailables.add(new Log(handler, 0, 0));
		StaticEntitiesAvailables.add(new Tree(handler, 0, 0));
		StaticEntitiesAvailables.add(new Turtle(handler, 0, 0));

		SpawnedAreas = new ArrayList<>();
		SpawnedHazards = new ArrayList<>();

		player = new Player(handler);

		gridWidth = handler.getWidth() / 64;
		gridHeight = handler.getHeight() / 64;
		movementSpeed = 1;
		Player.internalCounter = 0;
		Player.counter=0;
		// movementSpeed = 20; I dare you.

		/*
		 * Spawn Areas in Map (2 extra areas spawned off screen) To understand this, go
		 * down to randomArea(int yPosition)
		 */
		for (int i = 0; i < gridHeight + 2; i++) {
			if (i >= gridHeight - 2) {
				SpawnedAreas.add(new GrassArea(handler, (-2 + i) * 64));
			} else {
				SpawnedAreas.add(randomArea((-2 + i) * 64));
			}
		}

		player.setX((gridWidth / 2) * 64);
		player.setY((gridHeight - 3) * 64);

		// Not used atm.
		grid = new ID[gridWidth][gridHeight];
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridHeight; y++) {
				grid[x][y] = ID.EMPTY;
			}
		}
	}

	public void tick() {

		if (this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[2])) {
			this.object2.word = this.object2.word + this.handler.getKeyManager().str[1];
		}
		if (this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[0])) {
			this.object2.word = this.object2.word + this.handler.getKeyManager().str[2];
		}
		if (this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[1])) {
			this.object2.word = this.object2.word + this.handler.getKeyManager().str[0];
		}
		if (this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[3])) {
			this.object2.addVectors();
		}
		if (this.handler.getKeyManager().keyJustPressed(this.handler.getKeyManager().num[4])
				&& this.object2.isUIInstance) {
			this.object2.scalarProduct(handler);
		}

		if (this.reset) {
			time = System.currentTimeMillis();
			this.reset = false;
		}

		if (this.object2.isSorted) {

			if (System.currentTimeMillis() - this.time >= 2000) {
				this.object2.setOnScreen(true);
				this.reset = true;
			}

		}

		for (BaseArea area : SpawnedAreas) {
			area.tick();
		}
		for (StaticBase hazard : SpawnedHazards) {
			hazard.tick();
		}

		for (int i = 0; i < SpawnedAreas.size(); i++) {
			SpawnedAreas.get(i).setYPosition(SpawnedAreas.get(i).getYPosition() + movementSpeed);

			// Check if Area (thus a hazard as well) passed the screen.
			if (SpawnedAreas.get(i).getYPosition() > handler.getHeight()) {
				// Replace with a new random area and position it on top
				SpawnedAreas.set(i, randomArea(-2 * 64));
			}
			// Make sure players position is synchronized with area's movement
			if (SpawnedAreas.get(i).getYPosition() < player.getY()
					&& player.getY() - SpawnedAreas.get(i).getYPosition() < 3) {
				player.setY(SpawnedAreas.get(i).getYPosition());
			}
		}

		HazardMovement();
		HazardCollision();
		HazardWaterCollision();
		CandyCollision();

		player.tick();
		// make player move the same as the areas
		player.setY(player.getY() + movementSpeed);

		object2.tick();

	}

	private void HazardMovement() {
		for (int i = 0; i < SpawnedHazards.size(); i++) {

			// Moves hazard down
			SpawnedHazards.get(i).setY(SpawnedHazards.get(i).getY() + movementSpeed);

			// Moves Log or Turtle to the right
			if (SpawnedHazards.get(i) instanceof Log) {
				SpawnedHazards.get(i).setX(SpawnedHazards.get(i).getX() + 1);
				

				// Verifies the hazards Rectangles aren't null and
				// If the player Rectangle intersects with the Log or Turtle Rectangle, then
				// move player to the right.
				if (SpawnedHazards.get(i).GetCollision() != null
						&& player.getPlayerCollision().intersects(SpawnedHazards.get(i).GetCollision())) {
					player.setX(player.getX() + 1);
				}

			}
			// EXPERIMENTING WITH REVERSE TURTLES ****************************
			if (SpawnedHazards.get(i) instanceof Turtle) {
				SpawnedHazards.get(i).setX(SpawnedHazards.get(i).getX() - 1);
				

				// Verifies the hazards Rectangles aren't null and
				// If the player Rectangle intersects with the Log or Turtle Rectangle, then
				// move player to the right.
				if (SpawnedHazards.get(i).GetCollision() != null
						&& player.getPlayerCollision().intersects(SpawnedHazards.get(i).GetCollision())) {
					player.setX(player.getX() - 1);
				}

			}
			
			//EXPERIMENTING WITH REVERSE TURTLES *****************************
			

			// if hazard has passed the screen height, then remove this hazard.
			if (SpawnedHazards.get(i).getY() > handler.getHeight()) { 
				SpawnedHazards.remove(i);
			}
			// Makes logs and turtles "loop"
			if(SpawnedHazards.get(i) instanceof Log && SpawnedHazards.get(i).getX() > 576) {  
				SpawnedHazards.get(i).setX(-(SpawnedHazards.get(i).getWidth() + 50)); 
			}
			else if(SpawnedHazards.get(i) instanceof Turtle && SpawnedHazards.get(i).getX() < -80) {  
				SpawnedHazards.get(i).setX(SpawnedHazards.get(i).getWidth() + 576); 
			}
			
		}
	}
	/*NEW
	 * Check if the have a collision with the tree.
	 */
	private void HazardCollision() {

		for (int i = 0; i < SpawnedHazards.size(); i++) {

			if (!SpawnedHazards.isEmpty() && SpawnedHazards.get(i )instanceof Tree
					&& SpawnedHazards.get(i).GetCollision() != null
					&& player.getPlayerCollision().intersects(SpawnedHazards.get(i).GetCollision())) {

				if (player.facing.equals("UP")) {
					player.setY(player.getY()+64);
					Player.counter--;
					Player.internalCounter--;

				}if (player.facing.equals("DOWN")){
					player.setY(player.getY()-64);
					Player.internalCounter++;

				}if (player.facing.equals("LEFT")){
					player.setX(player.getX()+16);

				}if (player.facing.equals("RIGHT")){
					player.setX(player.getX()-16);
				}
			}
		}
	}
	/*NEW
	 * Check if the player intersect a water hazard
	 */
	public void HazardWaterCollision() {

		for (int i = 0; i < SpawnedHazards.size(); i++) {

			if ((SpawnedHazards.get(i)instanceof Log || SpawnedHazards.get(i)instanceof LillyPad 
					|| SpawnedHazards.get(i)instanceof Turtle)
					&&!handler.getWorld().SpawnedHazards.isEmpty()
					&& SpawnedHazards.get(i).GetCollision() != null
					&& player.getPlayerCollision().intersects(SpawnedHazards.get(i).GetCollision())){
				dead = false;
			}
		}
	}
	public void CandyCollision() {
		for (int i = 0; i < SpawnedHazards.size(); i++) {

			if (!SpawnedHazards.isEmpty() && SpawnedHazards.get(i)instanceof RareCandy
					&& SpawnedHazards.get(i).GetCollision() != null
					&& player.getPlayerCollision().intersects(SpawnedHazards.get(i).GetCollision())) {
				SpawnedHazards.remove(i);
				counterCandy++;
			}
		}
	}

	public void render(Graphics g) {

		for (BaseArea area : SpawnedAreas) {
			area.render(g);
		}

		for (StaticBase hazards : SpawnedHazards) {
			hazards.render(g);

		}

		if (counterCandy >=0 && counterCandy<5) {
		player.render(g);
		}else
		if (counterCandy >= 5) {
			player.renderVenasaur(g);
		}
		this.object2.render(g);
		MiniMenu(g, Color.BLACK);
	}

	/*
	 * Given a yPosition, this method will return a random Area out of the Available
	 * ones.) It is also in charge of spawning hazards at a specific condition.
	 */
	private BaseArea randomArea(int yPosition) {
		Random rand = new Random();

		// From the AreasAvailable, get me any random one.
		BaseArea randomArea = AreasAvailables.get(rand.nextInt(AreasAvailables.size()));

		if (randomArea instanceof GrassArea) {
			randomArea = new GrassArea(handler, yPosition);
			SpawnHazard2(yPosition);
		} else if (randomArea instanceof WaterArea) {
			randomArea = new WaterArea(handler, yPosition);
			SpawnHazard(yPosition);
		} else {
			randomArea = new StreetArea(handler, yPosition);
		}
		return randomArea;
	}

	/*
	 * Given a yPosition this method will add a new hazard to the SpawnedHazards
	 * ArrayList
	 */
	private void SpawnHazard(int yPosition) {
		Random rand = new Random();
		int randInt;
		int choice;
		if (lastSpawned == null) { 
			choice = rand.nextInt(7);									 
		}
		else if(lastSpawned instanceof LillyPad) { // Checks if the last Spawn was a LillyPad 
			choice = 3;
		}
		else if(lastSpawned instanceof Turtle ) { // Checks if the last Spawn was a Turtle
			choice = 2;
		}
		else if (lastSpawned instanceof Log) // Checks if the last Spawn was a Log
			choice = 6;
		else
			choice = 6;
		if (choice <= 2) {
			for(int n = 0; n < rand.nextInt(3) + 1; n++) { // random logs from 1 to 4
				randInt = (128 * rand.nextInt(4)) + 64;
				SpawnedHazards.add(new Log(handler, randInt, yPosition));
				lastSpawned = new Log(handler, 0, 0); // dummy Log
			}
			
		} else if (choice > 5) {

			for (int m = 0; m < rand.nextInt(7); m++) { // Adds random Lillypads
				randInt = (64 * rand.nextInt(8)); 
				SpawnedHazards.add(new LillyPad(handler, randInt, yPosition));
				lastSpawned = new LillyPad(handler, 0, 0); // dummy LillyPad

			}
		} else {
			randInt = (64 * rand.nextInt(5)) + 32;
			SpawnedHazards.add(new Turtle(handler, randInt, yPosition));
			lastSpawned = new Turtle(handler, 0, 0); // dummy Turtle

		}

	}
	/*NEW
	 * Given a yPosition this method will spawn a new tree.
	 */
	private void SpawnHazard2(int yPosition) {
		Random rand = new Random();
		int randInt;
		int choice = rand.nextInt(9);
		for (int m = 0; m < rand.nextInt(9); m++) {
			if (choice <=5) {
				randInt = 64 * rand.nextInt(9);
				SpawnedHazards.add(new Tree (handler, randInt, yPosition));
			}else if (choice > 5) {
				randInt = 64 * rand.nextInt(9);
				SpawnedHazards.add(new RareCandy (handler, randInt, yPosition));
			}
		}
	}
	/*NEW
	 * Display a menu on top of the screen that display the score.
	 */
	private void MiniMenu(Graphics g, Color d) {
		Font fontScore = new Font("IMPACT", 30, 28);
		g.setColor(d);
		g.fillRect(0, 0, 576, 32);
		g.setColor(Color.WHITE);
		g.setFont(fontScore);
		g.drawString("Sapito Score: " + Player.counter, 3, 25);
		g.drawImage(Images.heart, 520, 0, 30, 30, null);
		g.drawString(""+heart, 550, 25);
		g.drawImage(Images.rareCandy, 360, 0, 30, 30, null);
		g.drawString("" + counterCandy, 400, 25);
	}
}
