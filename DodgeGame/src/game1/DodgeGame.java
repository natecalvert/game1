package game1;

import static game1.Thing.*;
import net.slashie.libjcsi.*;
import net.slashie.libjcsi.examples.*;
import net.slashie.libjcsi.examples.luck.*;
import net.slashie.libjcsi.examples.luck.toybox.*;
import net.slashie.libjcsi.jcurses.*;
import net.slashie.libjcsi.textcomponents.*;
import net.slashie.libjcsi.wswing.*;
import net.slashie.util.*;

import java.util.concurrent.TimeUnit;
import java.util.*;
import java.math.*;

class Thing {

    static int MAX_HEIGHT = 24;
    static int MAX_WIDTH = 79;
    int height;
    int deltaH;
    int width;
    int deltaW;

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    Thing(int y, int x, int deltaY, int deltaX) {
        this.height = y;
        this.width = x;
        this.deltaH = deltaY;
        this.deltaW = deltaX;
    }

    public Thing tick() {
        int newY = height + deltaH;
        int newX = width + deltaW;
        if (newY == MAX_HEIGHT) {
            return new Thing(0, randInt(1, MAX_WIDTH-1), 1, 0);
        } else if (newX == MAX_WIDTH) {
            return new Thing(randInt(1, MAX_HEIGHT-1), 0, 0, 1);
        } else {
            return new Thing(newY, newX, deltaH, deltaW);
        }
    }

    public void draw(ConsoleSystemInterface s) {
        String disp;
        switch (deltaH) {
            case 0:
                disp = ">";
                break;
            default:
                disp = "v";
                break;
        }
        s.print(width, height, disp, s.WHITE);
    }

}

public class DodgeGame {

    // IDEA: 5x5 (7x7?) grid.
    //       'Rocks' randomly spawn around edge and move through middle.
    //          - Top to bottom + left to right.
    //        Player starts in middle of grid, trys to avoid being hit.
    //        Score is number of rocks that have cleared the screen.
    //        No win condition, only fail: got hit by a rock.
    //        MAYBE ADD: 
    //         If 2 rocks collide they fill the square, blocking it from player use.
    public static void main(String[] args) {
        ConsoleSystemInterface csi = new WSwingConsoleInterface("Dodge the Thing!", true);

        csi.cls();
        csi.print(1, 1, "Welcome to DODGE THE THING!", ConsoleSystemInterface.GREEN);
        csi.print(2, 3, "An exciting game where you (X) dodge things (v or >)!");
        csi.print(2, 4, "To begin, what is your name?", ConsoleSystemInterface.BLUE);
        csi.print(2, 5, "(Press ENTER after input)");

        String name = csi.input();
        csi.print(2, 6, "Hi " + name + ", press SPACE to begin!");
        csi.refresh();
        csi.waitKey(CharKey.SPACE);
        
        Thing tDOWN1 = new Thing(0, randInt(1, MAX_WIDTH-1), 1, 0);
        Thing tDOWN2 = new Thing(0, randInt(1, MAX_WIDTH-1), 1, 0);
        Thing tDOWN3 = new Thing(0, randInt(1, MAX_WIDTH-1), 1, 0);
        Thing tLEFT1 = new Thing(randInt(1, MAX_HEIGHT-1), 0, 0, 1);
        Thing tLEFT2 = new Thing(randInt(1, MAX_HEIGHT-1), 0, 0, 1);
        Thing tLEFT3 = new Thing(randInt(1, MAX_HEIGHT-1), 0, 0, 1);
        
        while (true) {
            csi.cls();
            tDOWN1.draw(csi);
            tLEFT1.draw(csi);
            tDOWN2.draw(csi);
            tLEFT2.draw(csi);
            tDOWN3.draw(csi);
            tLEFT3.draw(csi);
            csi.refresh();
            try {
                TimeUnit.MILLISECONDS.sleep(16 * 4);
            } catch (InterruptedException IE){
                
            }
            tDOWN1 = tDOWN1.tick();
            tDOWN2 = tDOWN2.tick();
            tDOWN3 = tDOWN3.tick();
            tLEFT1 = tLEFT1.tick();
            tLEFT2 = tLEFT2.tick();
            tLEFT3 = tLEFT3.tick();
        }
    }

}
