package Game.Entities.Dynamic;

import Game.Entities.EntityBase;
import Game.GameStates.State;
import Game.World.WaterArea;
import Main.Handler;
import Resources.Images;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/*
 * The Frog.
 */
public class Player extends EntityBase {
	private Handler handler;


	private Rectangle player;
	public String facing = "UP";
	private Boolean moving = false;
	private int moveCoolDown=0;

	public static int counter = 0;
	public static int internalCounter = 0;

	private int index =0;

	public Player(Handler handler) {
		super(handler);
		this.handler = handler;
		this.handler.getEntityManager().getEntityList().add(this);

		player = new Rectangle(); 	// see UpdatePlayerRectangle(Graphics g) for its usage.
	}

	public void tick(){

		if (handler.getWorld().dead) {
			State.setState(handler.getGame().gameOverState);
		}

		if(moving) {
			animateMovement();
		}

		if(!moving){
			move();
		}
		HazardCollision2();
	}

	private void reGrid() {
		if(facing.equals("UP")) {
			if(this.getX() % 64 >= 64 / 2 ) {
				this.setX(this.getX() + (64 - this.getX() % 64));
			}
			else {
				this.setX(this.getX() - this.getX() % 64);
			}
			setY(getY()-64);
		}
	}

	private void move(){
		if(moveCoolDown< 25){
			moveCoolDown++;
		}
		index=0;

		if (player.getY() >= 760 ) {
			State.setState(handler.getGame().gameOverState);
		}else

			/////////////////MOVE UP///////////////
			if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_W) && !moving && facing.equals("UP") && player.getY() >= 32){
				moving=true;

				internalCounter++;
				if ( internalCounter > counter) {
					counter = internalCounter;
				}

			}else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_W) && !moving && !facing.equals("UP")){
				if(facing.equals("DOWN")) {
					if(this.getX() % 64 >= 64 / 2 ) {

						this.setX(this.getX() + (64 - this.getX() % 64));
					}
					else {
						this.setX(this.getX() - this.getX() % 64);
					}
					setY(getY() + 64);
				}
				if(facing.equals("LEFT")) {
					setY(getY() + 64);
				}
				if(facing.equals("RIGHT")) {
					setX(getX()-64);
					setY(getY()+64);
				}
				facing = "UP";
			}

		/////////////////MOVE LEFT///////////////
			else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_A) && !moving && facing.equals("LEFT") && player.getX() >= 64){
				moving=true;
			}else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_A) && !moving&& !facing.equals("LEFT")){
				if(facing.equals("RIGHT")) {
					setX(getX()-64);
				}
				reGrid();
				facing = "LEFT";
			}

		/////////////////MOVE DOWN///////////////
			else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_S) && !moving && facing.equals("DOWN")){
				moving=true;
				internalCounter--;

			}else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_S) && !moving && !facing.equals("DOWN")){
				reGrid();
				if(facing.equals("RIGHT")){
					setX(getX()-64);
				}
				facing = "DOWN";
			}

		/////////////////MOVE RIGHT///////////////
			else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_D) && !moving && facing.equals("RIGHT") && player.getX() <= 511 ){
				moving=true;
			}else if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_D) && !moving&& !facing.equals("RIGHT")){
				if(facing.equals("LEFT")) {
					setX(getX()+64);
				}
				if(facing.equals("UP")) {
					setX(getX()+64);
					setY(getY()-64);
				}
				if(facing.equals("DOWN")) {
					if(this.getX() % 64 >= 64 / 2 ) {
						this.setX(this.getX() + (64 - this.getX() % 64));
					}
					else {
						this.setX(this.getX() - this.getX() % 64);
					}
					setX(getX()+64);
				}
				facing = "RIGHT";
			}
	}

	private void animateMovement(){
		if(index==8) {
			moving = false;
			index = 0;
		}
		moveCoolDown = 0;
		index++;
		switch (facing) {
		case "UP":
			if (this.getX() % 64 >= 64 / 2) {
				this.setX(this.getX() + (64 - this.getX() % 64));
			} else {
				this.setX(this.getX() - this.getX() % 64);
			}
			setY(getY() - (8));
			break;

		case "LEFT":
			setX(getX() - (8));
			break;

		case "DOWN":
			if (this.getX() % 64 >= 64 / 2) {
				this.setX(this.getX() + (64 - this.getX() % 64));
			} else {
				this.setX(this.getX() - this.getX() % 64);
			}
			setY(getY() + (8));
			break;

		case "RIGHT":
			setX(getX() + (8));
			break;

		}
	}

	public void render(Graphics g){

		if(index>=8){
			index=0;
			moving = false;
		}

		switch (facing) {
		case "UP":
			g.drawImage(Images.BulbasaurUp[index], getX(), getY()-64, getWidth(), getHeight(), null);
			break;
		case "DOWN":
			g.drawImage(Images.BulbasaurDown[index], getX(), getY(), getWidth(), getHeight(), null);
			break;
		case "LEFT":
			g.drawImage((Images.BulbasaurLeft[index]), getX(), getY(), getWidth(), getHeight(), null);
			break;
		case "RIGHT":
			g.drawImage((Images.BulbasaurRight[index]), getX()-64, getY(), getWidth(), getHeight(), null);
			break;
		}
		UpdatePlayerRectangle(g);
	}
	
	public void renderVenasaur(Graphics g){

		if(index>=8){
			index=0;
			moving = false;
		}

		switch (facing) {
		case "UP":
			g.drawImage(Images.VenasaurUp[index], getX(), getY()-64, getWidth(), getHeight(), null);
			break;
		case "DOWN":
			g.drawImage(Images.VenasaurDown[index], getX(), getY(), getWidth(), getHeight()+10, null);
			break;
		case "LEFT":
			g.drawImage((Images.VenasaurLeft[index]), getX(), getY(), getWidth()+15, getHeight()+15, null);
			break;
		case "RIGHT":
			g.drawImage((Images.VenasaurRight[index]), getX()-64, getY(), getWidth()+15, getHeight()+15, null);
			break;
		}
		UpdatePlayerRectangle(g);
	}

	// Rectangles are what is used as "collisions." 
	// The hazards have Rectangles of their own.
	// This is the Rectangle of the Player.
	// Both come in play inside the WorldManager.
	private void UpdatePlayerRectangle(Graphics g) {

		player = new Rectangle(this.getX(), this.getY(), getWidth(), getHeight());

		if (facing.equals("UP")){
			player = new Rectangle(this.getX(), this.getY() - 64, getWidth(), getHeight());
		}
		else if (facing.equals("RIGHT")) {
			player = new Rectangle(this.getX() - 64, this.getY(), getWidth(), getHeight());
		}
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private static BufferedImage rotateClockwise90(BufferedImage src) {
		int width = src.getWidth();
		int height = src.getHeight();

		BufferedImage dest = new BufferedImage(height, width, src.getType());

		Graphics2D graphics2D = dest.createGraphics();
		graphics2D.translate((height - width) / 2, (height - width) / 2);
		graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
		graphics2D.drawRenderedImage(src, null);

		return dest;
	}

	public Rectangle getPlayerCollision() {
		return player;
	}
	/*NEW
	 * Check if the player is in the water area and kill him.
	 */
	public void HazardCollision2() {
		handler.getWorld().dead = false;
		for (int i = 0; i < handler.getWorld().SpawnedAreas.size(); i++) {

			if (!handler.getWorld().SpawnedAreas.isEmpty() && handler.getWorld().SpawnedAreas.get(i) instanceof WaterArea
					&& (handler.getWorld().SpawnedAreas.get(i).getYPosition() < player.getY())
					&& (handler.getWorld().SpawnedAreas.get(i).getYPosition()+10 > player.getY())) {
				handler.getWorld().dead = true;
			}
		}
	}

}
