package Game.Entities.Static;

import java.awt.Graphics;
import java.awt.Rectangle;

import Main.Handler;
import Resources.Images;

public class Car extends StaticBase {

    private Rectangle car;
	
    public Car(Handler handler,int xPosition, int yPosition) {
        super(handler);
        // Sets original position to be this one.
        this.setX(xPosition); 
        this.setY(yPosition);
    }
    
    @Override
    public void render(Graphics g) {
    	
    	g.drawImage(Images.car1, this.getX(), this.getY(), 64, 64, null);
    	car = new Rectangle(this.getX(), this.getY(), 50, 60);
    }
    	
    @Override
    public Rectangle GetCollision() {
    	return car;
    }
    
}
