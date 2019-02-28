package Game.Entities.Static;

import java.awt.Graphics;
import java.awt.Rectangle;

import Main.Handler;
import Resources.Images;

public class RareCandy extends StaticBase {

    private Rectangle RareCandy;
	
    public RareCandy(Handler handler,int xPosition, int yPosition) {
        super(handler);
        this.setY(yPosition);
        this.setX(xPosition);
        
    }
    
    @Override
    public void render(Graphics g) {
    	
    	g.drawImage(Images.rareCandy, this.getX()+16, this.getY()+16, 44, 44, null);
    	RareCandy = new Rectangle(this.getX(), this.getY(), 64,64);
    }
    
    
    @Override
    public Rectangle GetCollision() {
    	return RareCandy;
    }
}
