package game1;

import static game1.Thing.*;
import static game1.Dodger.*;

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

    static int MAX_Y = 22;
    static int MIN_Y = 3;
    static int MAX_X = 55;
    static int MIN_X = 26;
    int height;
    int deltaH;
    int width;
    int deltaW;
    static int counter = 0;

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    Thing(int x, int y, int deltaY, int deltaX) {
        this.height = y;
        this.width = x;
        this.deltaH = deltaY;
        this.deltaW = deltaX;
    }

    public Thing tickThing() {
        int newY = height + deltaH;
        int newX = width + deltaW;
        if (newY == MAX_Y && deltaW == 0) {
            counter++;
            return new Thing(randInt(MIN_X, MAX_X - 1), MIN_Y, 1, 0);
        } else if (newX == MAX_X && deltaH == 0) {
            counter++;
            return new Thing(MIN_X, randInt(MIN_Y, MAX_Y - 1), 0, 1);
        } else {
            return new Thing(newX, newY, deltaH, deltaW);
        }
    }

    public void drawThing(ConsoleSystemInterface s) {
        String disp;
        if (deltaH == 0) {
            disp = ">";
        } else {
            disp = "V";
        }
        s.print(width, height, disp, s.WHITE);
    }
}

class Dodger {
    int x;
    int y;

    Dodger(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void drawDodger(ConsoleSystemInterface s) {
        s.print(x, y, "O", s.WHITE);
    }

    public Dodger moveDodger(CharKey key) {
        if (key.isRightArrow()) {
            return new Dodger(this.x + 1, this.y);
        }
        if (key.isLeftArrow()) {
            return new Dodger(this.x - 1, this.y);
        }
        if (key.isUpArrow()) {
            return new Dodger(this.x, this.y - 1);
        }
        if (key.isDownArrow()) {
            return new Dodger(this.x, this.y - 1);
        } else {
            return new Dodger(this.x, this.y);
        }
    }
}

public class DodgeGame {

    public static void drawBoundary(ConsoleSystemInterface s) {
        String disp = "X";
        for (int x = 25; x < 56; x++) {
            for (int y = 2; y < 23; y++) {
                if (x == 25 && (y >= 2 || y <= 22)) {
                    s.print(x, y, disp, s.WHITE);
                } else if (x == 55 && (y >= 2 || y <= 22)) {
                    s.print(x, y, disp, s.WHITE);
                } else if (y == 2 && (x >= 25 || x <= 55)) {
                    s.print(x, y, disp, s.WHITE);
                } else if (y == 22 && (x >= 25 || x <= 55)) {
                    s.print(x, y, disp, s.WHITE);
                }
            }
        }
    }

    public static void main(String[] args) {
        ConsoleSystemInterface csi = new WSwingConsoleInterface("Dodge the Thing!", true);

        csi.cls();
        csi.print(1, 1, "Welcome to DODGE THE THING!", ConsoleSystemInterface.GREEN);
        csi.print(2, 3, "An exciting game where you (O) dodge things (v & >)!");
        csi.print(2, 4, "To begin, what is your name?", ConsoleSystemInterface.BLUE);
        csi.print(2, 5, "(Press ENTER after input)");

        String name = csi.input();
        csi.print(2, 6, "Hi " + name + ", press SPACE to begin!");
        csi.refresh();
        csi.waitKey(CharKey.SPACE);

        Thing tDOWN = new Thing(randInt(MIN_X, MAX_X - 1), MIN_Y, 1, 0);
        Thing tLEFT = new Thing(MIN_X, randInt(MIN_Y, MAX_Y - 1), 0, 1);

        while (true) {
            csi.cls();
            csi.print(1, 1, name + "'s SCORE:" + counter, ConsoleSystemInterface.WHITE);
            drawBoundary(csi);
            tDOWN.drawThing(csi);
            tLEFT.drawThing(csi);
            csi.refresh();
            try {
                TimeUnit.MILLISECONDS.sleep(128);
            } catch (InterruptedException IE) {
            }
            tDOWN = tDOWN.tickThing();
            tLEFT = tLEFT.tickThing();
        }
    }
}
