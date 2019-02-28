package Game.GameStates;

import Main.Handler;
import Resources.Images;
import UI.UIImageButton;
import UI.UIManager;

import java.awt.*;

import Game.Entities.Dynamic.Player;

/**
 * Created by AlexVR on 7/1/2018.
 */
public class GameOver extends State {

    private int count = 0;
    private UIManager uiManager;

    public GameOver(Handler handler) {
        super(handler);
        uiManager = new UIManager(handler);
        handler.getMouseManager().setUimanager(uiManager);

        /*
         * Adds a button that by being pressed changes the State
         */
        uiManager.addObjects(new UIImageButton(40, handler.getGame().getHeight() - 200, 160, 75, Images.Restart, () -> {
            handler.getMouseManager().setUimanager(null);
            handler.getGame().reStart();            
            State.setState(handler.getGame().gameState);
        }));

//        uiManager.addObjects(new UIImageButton(33 + 192,  handler.getGame().getHeight() - 150, 128, 64, Images.Options, () -> {
//            handler.getMouseManager().setUimanager(null);
//            State.setState(handler.getGame().menuState);
//        }));

        uiManager.addObjects(new UIImageButton(40 + 175 * 2,  handler.getGame().getHeight() - 200, 160, 75, Images.Exit, () -> {
            handler.getMouseManager().setUimanager(null);
            State.setState(handler.getGame().menuState);
        }));





    }

    @Override
    public void tick() {
        handler.getMouseManager().setUimanager(uiManager);
        uiManager.tick();
        count++;
        if( count>=30){
            count=30;
        }
        if(handler.getKeyManager().pbutt && count>=30){
            count=0;
            State.setState(handler.getGame().gameState);
        }

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(Images.gameover,0,0,handler.getGame().getWidth(),handler.getGame().getHeight(),null);
        uiManager.Render(g);
        Font fontScore = new Font("IMPACT", 60, 60);
        g.setFont(fontScore);
        g.setColor(Color.CYAN);
        g.drawString("Score: " + Player.counter, 30, 768/3 +15);

    }
}
