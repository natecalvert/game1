package game1;

import javalib.colors.*;
import javalib.funworld.*;
import javalib.worldcanvas.*;
import javalib.worldimages.*;

import java.awt.Color;
import java.util.Random;
import java.awt.*;

interface Constants {

    int wWIDTH = 600;
    int wHEIGHT = 600;
    Color wCOLOR = Color.BLACK;
    int dRADIUS = 20;
    Color dCOLOR = Color.WHITE;
    int tRADIUS = dRADIUS;
    Color tCOLOR = Color.RED;
    Color sCOLOR = Color.BLUE;
}

// Class of Dodger, which is user controlled
class Dodger implements Constants {

    Posn center;
    int radius;
    Color color;

    // Constructor for Dodger
    Dodger(Posn center, int radius, Color color) {
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    // Produce the image of the Dodger at it's curent location
    WorldImage dodgerImage() {
        return new DiskImage(this.center, this.radius, this.color);
    }

    // Moves the Dodger based on user inputs
    public Dodger moveDodger(String key) {
        switch (key) {
            case "right":
                return new Dodger(new Posn(this.center.x + dRADIUS * 2, this.center.y),
                        this.radius, this.color);
            case "left":
                return new Dodger(new Posn(this.center.x - dRADIUS * 2, this.center.y),
                        this.radius, this.color);
            case "up":
                return new Dodger(new Posn(this.center.x, this.center.y - dRADIUS * 2),
                        this.radius, this.color);
            case "down":
                return new Dodger(new Posn(this.center.x, this.center.y + dRADIUS * 2),
                        this.radius, this.color);
            default:
                return this;
        }
    }

    // Checks if Dodger is out of bounds.
    boolean outOfBounds(int width, int height) {
        return this.center.x < 0
                || this.center.x > width
                || this.center.y < 0
                || this.center.y > height;
    }

    boolean didCollide(Thing thing) {
        return this.center.x == thing.center.x
                && this.center.y == thing.center.y;
    }
}
// Class of Thing, which is randomly generated and automatically moved

class Thing implements Constants {

    Posn center;
    int radius;
    int dX;
    int dY;
    Color color;

    // Constructor for Thing
    Thing(Posn center, int radius, int dX, int dY, Color color) {
        this.center = center;
        this.radius = radius;
        this.dX = dX;
        this.dY = dY;
        this.color = color;
    }

    // Produce the image of the Thing at it's curent location
    WorldImage thingImage() {
        return new DiskImage(this.center, this.radius, this.color);
    }

    // Moves the Thing
    public Thing moveThing() {
        if (this.dX == 0) {
            return new Thing(new Posn(this.center.x, this.center.y + tRADIUS * 2), this.radius, this.dX, this.dY, this.color);
        } else if (this.dY == 0) {
            return new Thing(new Posn(this.center.x + tRADIUS * 2, this.center.y), this.radius, this.dX, this.dY, this.color);
        } else { //This should NEVER HAPPEN.
            return new Thing(new Posn(300, 300), 300, 1, 1, Color.ORANGE);
        }
    }

    // Checks if Thing is at bounds.
    boolean atBounds(int width, int height) {
        return this.center.x > width || this.center.y > height;
    }

//    boolean hitThing(Thing thing) {
//        return this.center.x == thing.center.x
//                && this.center.y == thing.center.y;
//    }
}

// Class for world of the 'Thing's
class DodgeWorld extends World implements Constants {

    int width = wWIDTH;
    int height = wHEIGHT;
    Dodger dodger;
    Thing thing1;
    Thing thing2;
    int score;

    // Generates random integers used in placing Things
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    // Constructor for the world
    public DodgeWorld(Dodger dodger, Thing thing1, Thing thing2, int score) {
        super();
        this.dodger = dodger;
        this.thing1 = thing1;
        this.thing2 = thing2;
        this.score = score;
    }

    //Move Dodger on key presses
    public World onKeyEvent(String key) {
        return new DodgeWorld(this.dodger.moveDodger(key), this.thing1, this.thing2, this.score);
    }

    // On tick check:
    // - If Dodger is in bounds.
    // - If Dodger hit a Thing.
    // - If the Thing's hit eachother.
    public World onTick() {
        // If Dodger is at bounds, don't allow movement in past bounds.
        if (this.dodger.outOfBounds(this.width, this.height)) {
            return this.endOfWorld("Out of Bounds");
        }
        if (this.thing1.atBounds(this.width, this.height)) {
            this.thing1 = new Thing(new Posn(-20, 20 + randInt(0, 14) * 40), tRADIUS, 1, 0, tCOLOR);
            this.thing2 = new Thing(new Posn(20 + randInt(0, 14) * 40, -20), tRADIUS, 0, 1, tCOLOR);
            return new DodgeWorld(this.dodger, this.thing1.moveThing(), this.thing2.moveThing(), this.score = this.score + 1);
        }
        if (this.dodger.didCollide(thing1) || this.dodger.didCollide(thing2)) {
            return this.endOfWorld("You didn't Dodge the Thing!");
        }
//        if (this.thing1.hitThing(thing2) || this.thing2.hitThing(thing1)) {
//            return new DodgeWorld(this.dodger, this.thing1.moveThing(), this.thing2.moveThing());
//        } 
        else {
            return new DodgeWorld(this.dodger, this.thing1.moveThing(), this.thing2.moveThing(), this.score);
        }
    }

    // Background image for world 
    public WorldImage background
            = new RectangleImage(new Posn(this.width / 2, this.height / 2), this.width, this.height, wCOLOR);

    // Produce image of world by adding Dodger and Thing to the background
    public WorldImage makeImage() {
        return new OverlayImages(this.background,
                new OverlayImages(this.dodger.dodgerImage(),
                        new OverlayImages(this.thing1.thingImage(),
                                this.thing2.thingImage())));
    }

    // Produce image of world by adding fail state explanation to background
    public WorldImage lastImage(String s) {
        return new OverlayImages(this.makeImage(),
                new TextImage(new Posn(this.width / 2, this.height / 2), s, sCOLOR));
    }

    //Check:
    // - If Dodger is in bounds.
    // - If Dodger hit a Thing.
    // - If the Thing's hit eachother.
    public WorldEnd worldEnds() {
        if (this.dodger.outOfBounds(this.width, this.height)) {
            return new WorldEnd(true, new OverlayImages(this.makeImage(),
                    new TextImage(new Posn(this.width / 2, this.height / 2), "Out of Bounds!" + " SCORE: " + this.score, sCOLOR)));
        }
        if (this.dodger.didCollide(thing1) || this.dodger.didCollide(thing2)) {
            return new WorldEnd(true, new OverlayImages(this.makeImage(),
                    new TextImage(new Posn(this.width / 2, this.height / 2), "You didn't Dodge the Thing!" + " SCORE: " + this.score, sCOLOR)));
        } else {
            return new WorldEnd(false, this.makeImage());
        }
    }
}

public class DodgeGame implements Constants {

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static void main(String[] args) {
        Dodger testDodge = new Dodger(new Posn(300, 300), dRADIUS, dCOLOR);
        Thing testThing = new Thing(new Posn(300, 300), tRADIUS, 0, 0, tCOLOR);
        Thing testThing2 = new Thing(new Posn(300, 300), tRADIUS, 0, 0, tCOLOR);

        System.out.println(testDodge.didCollide(testThing) + " should be " + true);
//        System.out.println(testThing.hitThing(testThing2) + " should be " + true);
//        System.out.println(testThing2.hitThing(testThing) + " should be " + true);

        DodgeWorld w1 = new DodgeWorld(new Dodger(new Posn(300, 300), dRADIUS, dCOLOR),
                new Thing(new Posn(20, 20 + randInt(0, 14) * 40), tRADIUS, 1, 0, tCOLOR),
                new Thing(new Posn(20 + randInt(0, 14) * 40, 20), tRADIUS, 0, 1, tCOLOR), 0);
        w1.bigBang(wWIDTH, wHEIGHT, 0.05);

    }
}
