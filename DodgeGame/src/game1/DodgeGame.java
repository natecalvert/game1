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
    int tRADIUS = 20;
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
    public Dodger moveDodger (String key) {
        switch (key) {
            case "right":
                return new Dodger(new Posn(this.center.x + dRADIUS*2, this.center.y),
                        this.radius, this.color);
            case "left":
                return new Dodger(new Posn(this.center.x - dRADIUS*2, this.center.y),
                        this.radius, this.color);
            case "up":
                return new Dodger(new Posn(this.center.x, this.center.y - dRADIUS*2),
                        this.radius, this.color);
            case "down":
                return new Dodger(new Posn(this.center.x, this.center.y + dRADIUS*2),
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
    public Thing moveThing () {
        if (this.dX == 0){
            return new Thing(new Posn(this.center.x, this.center.y + tRADIUS*2), this.radius, this.dX, this.dY, this.color);
        } else if (this.dY == 0){
            return new Thing(new Posn(this.center.x + tRADIUS*2, this.center.y), this.radius, this.dX, this.dY, this.color);
        } else { //This should NEVER HAPPEN.
            return new Thing(new Posn(300, 300), 300, 1, 1, Color.ORANGE);
        }
    }

    // Checks if Thing is at bounds.
    boolean atBounds(int width, int height) {
        return this.center.x > width || this.center.y > height;
    }
}

// Class for world of the 'Thing's
class DodgeWorld extends World implements Constants {

    int width = wWIDTH;
    int height = wHEIGHT;
    Dodger dodger;
    Thing thing1;
    Thing thing2;
    
    // Generates random integers used in placing Thing
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    // Constructor for the world
    public DodgeWorld(Dodger dodger, Thing thing1, Thing thing2) {
        super();
        this.dodger = dodger;
        this.thing1 = thing1;
        this.thing2 = thing2;
    }
    
    //Move Dodger on key presses
    public World onKeyEvent(String key){
        return new DodgeWorld(this.dodger.moveDodger(key), this.thing1, this.thing2);
    }

    // On tick check:
    // - If Dodger is in bounds.
    // - If Dodger hit a Thing.
    // - If the Thing's hit eachother.
    public World onTick(){
        // If Dodger is at bounds, don't allow movement in past bounds.
        if (this.dodger.outOfBounds(this.width, this.height)){
            return this.endOfWorld("Out of Bounds");
        } if (this.thing1.atBounds(this.width, this.height)){
            this.thing1 = new Thing(new Posn(-20, 20+randInt(0,14)*40), tRADIUS, 1, 0, tCOLOR);
            this.thing2 = new Thing(new Posn(20+randInt(0,14)*40, -20), tRADIUS, 0, 1, tCOLOR);
            return new DodgeWorld(this.dodger, this.thing1.moveThing(), this.thing2.moveThing());
        }
        //if (this.dodger.hitThing){
          //  return this.endOfWorld("You didn't dodge the thing");
        //} if (this.thing.hitThing){
          //  return //new impassable object
        //}
        else {
            return new DodgeWorld(this.dodger, this.thing1.moveThing(), this.thing2.moveThing());
        }
    }
    
    // Background image for world 
    public WorldImage background = 
            new RectangleImage(new Posn(this.width/2, this.height/2), this.width, this.height, wCOLOR);
    
    // Produce image of world by adding Dodger and Thing to the background
    public WorldImage makeImage(){
        return new OverlayImages(this.background, 
                new OverlayImages(this.dodger.dodgerImage(), 
                        new OverlayImages(this.thing1.thingImage(),
                        this.thing2.thingImage())));
    }
    
    // Produce image of world by adding fail state explanation to background
    public WorldImage lastImage(String s){
        return new OverlayImages(this.makeImage(), 
                new TextImage(new Posn(this.width/2, this.height/2), s, sCOLOR));
    }
    
    //Check:
    // - If Dodger is in bounds.
    // - If Dodger hit a Thing.
    // - If the Thing's hit eachother.
    public WorldEnd worldEnds(){
        if (this.dodger.outOfBounds(this.width, this.height)){
            return new WorldEnd(true, new OverlayImages(this.makeImage(),
            new TextImage(new Posn(this.width/2, this.height/2), "Out of Bounds", sCOLOR)));
        } else {
            return new WorldEnd(false, this.makeImage());
        }
    }
}

public class DodgeGame implements Constants{
    
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    
    public static void main(String[] args) {
        DodgeWorld w1 = new DodgeWorld(new Dodger(new Posn(300, 300), dRADIUS, dCOLOR), 
                                       new Thing(new Posn(20, 20+randInt(0,14)*40), tRADIUS, 1, 0, tCOLOR),
                                       new Thing(new Posn(20+randInt(0,14)*40, 20), tRADIUS, 0, 1, tCOLOR));
        w1.bigBang(wWIDTH, wHEIGHT, 0.1);

    }
}