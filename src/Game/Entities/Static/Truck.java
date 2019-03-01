package Game.Entities.Static;

import java.awt.Graphics;
import java.awt.Rectangle;

import Main.Handler;
import Resources.Images;

public class Truck extends StaticBase {

    private Rectangle truck;
	
    public Truck(Handler handler,int xPosition, int yPosition) {
        super(handler);
        // Sets original position to be this one.
        this.setX(xPosition); 
        this.setY(yPosition);
    }
    
    @Override
    public void render(Graphics g) {
    	
    	g.drawImage(Images.truck, this.getX(), this.getY(), 64, 64, null);
    	truck = new Rectangle(this.getX(), this.getY(), 60, 64);
    }
    
    public Rectangle GetCollision() {
    	return truck;
    }
}