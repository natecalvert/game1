package game1;

import javalib.colors.*;
import javalib.funworld.*;
import javalib.worldcanvas.*;
import javalib.worldimages.*;

import tester.*;

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
    int eRADIUS = dRADIUS * 3;
    Color eCOLOR = Color.ORANGE;
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

    boolean didCollide(Explosion explosion) {
        return this.center.x == explosion.center.x && this.center.y == explosion.center.y
                || this.center.x == explosion.center.x + eRADIUS * 2 / 3 && this.center.y == explosion.center.y + eRADIUS * 2 / 3
                || this.center.x == explosion.center.x + eRADIUS * 2 / 3 && this.center.y == explosion.center.y
                || this.center.x == explosion.center.x + eRADIUS * 2 / 3 && this.center.y == explosion.center.y - eRADIUS * 2 / 3
                || this.center.x == explosion.center.x && this.center.y == explosion.center.y - eRADIUS * 2 / 3
                || this.center.x == explosion.center.x - eRADIUS * 2 / 3 && this.center.y == explosion.center.y - eRADIUS * 2 / 3
                || this.center.x == explosion.center.x - eRADIUS * 2 / 3 && this.center.y == explosion.center.y
                || this.center.x == explosion.center.x - eRADIUS * 2 / 3 && this.center.y == explosion.center.y + eRADIUS * 2 / 3
                || this.center.x == explosion.center.x && this.center.y == explosion.center.y + eRADIUS * 2 / 3;
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

    // Checks if Thing is out of bounds.
    boolean atBounds(int width, int height) {
        return this.center.x > width || this.center.y > height;
    }

    boolean hitThing(Thing thing) {
        return this.center.x == thing.center.x
                && this.center.y == thing.center.y;
    }
}

class Explosion implements Constants {

    Posn center;
    int radius;
    Color color;

    Explosion(Posn center, int radius, Color color) {
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    WorldImage explosionImage() {
        return new RectangleImage(this.center, this.radius * 2, this.radius * 2, this.color);
    }
}

// Class for world of the 'Thing's
class DodgeWorld extends World implements Constants {

    int width = wWIDTH;
    int height = wHEIGHT;
    Dodger dodger;
    Thing thing1;
    Thing thing2;
    Explosion explosion;
    int score;

    // Generates random integers used in placing Things
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    // Constructor for the world
    public DodgeWorld(Dodger dodger, Thing thing1, Thing thing2, Explosion explosion, int score) {
        super();
        this.dodger = dodger;
        this.thing1 = thing1;
        this.thing2 = thing2;
        this.explosion = explosion;
        this.score = score;
    }

    //Move Dodger on key presses
    public World onKeyEvent(String key) {
        return new DodgeWorld(this.dodger.moveDodger(key), this.thing1, this.thing2, this.explosion, this.score);
    }

    // On tick check:
    // - If Dodger is out of bounds.
    // - If Dodger hit a Thing.
    // - If Thing is ouf of bounds.
    // - If thing1 hit thing2.
    // - If Dodger hit an Explosion.
    public World onTick() {
        // If Dodger is out of bounds, end the world.
        if (this.dodger.outOfBounds(this.width, this.height)) {
            return this.endOfWorld("OutOfBounds");
        }
        // If the Thing1 is out of bounds: [Could also be thing2, as they move OoB @ same tick]
        // - 'Reset' both thing1 and thing2 to initial X or Y, with random Y or X
        // - Increment score
        if (this.thing1.atBounds(this.width, this.height)) {
            this.thing1 = new Thing(new Posn(-20, 20 + randInt(0, 14) * 40), tRADIUS, 1, 0, tCOLOR);
            this.thing2 = new Thing(new Posn(20 + randInt(0, 14) * 40, -20), tRADIUS, 0, 1, tCOLOR);
            return new DodgeWorld(this.dodger, this.thing1.moveThing(), this.thing2.moveThing(), this.explosion, this.score = this.score + 1);
        }
        // If Dodger hit either thing1 or thing2, end the world.
        if (this.dodger.didCollide(thing1) || this.dodger.didCollide(thing2)) {
            return this.endOfWorld("DodgerThingCollision");
        }
        // If thing1 and thing2 collide, create and Explosion.
        if (this.thing1.hitThing(thing2) || this.thing2.hitThing(thing1)) {
            this.explosion = new Explosion(new Posn(this.thing1.center.x, this.thing1.center.y), eRADIUS, eCOLOR);
            return new DodgeWorld(this.dodger, this.thing1.moveThing(), this.thing2.moveThing(), this.explosion, this.score);
        }// If Dodger hit an explosion, end the world.
        if (this.dodger.didCollide(explosion)) {
            return this.endOfWorld("DodgerBlewUp");
        } // Otherwise move thing1 and thing2.
        else {
            return new DodgeWorld(this.dodger, this.thing1.moveThing(), this.thing2.moveThing(), this.explosion, this.score);
        }
    }

    // Background image for world 
    public WorldImage background
            = new RectangleImage(new Posn(this.width / 2, this.height / 2), this.width, this.height, wCOLOR);

    // Produce image of world by adding Dodger and Thing to the background
    public WorldImage makeImage() {
        return new OverlayImages(this.background,
                new OverlayImages(this.dodger.dodgerImage(),
                        new OverlayImages(this.explosion.explosionImage(),
                                new OverlayImages(this.thing1.thingImage(),
                                        this.thing2.thingImage()))));
    }

    // Produce image of world by adding fail state explanation to background
    public WorldImage lastImage(String s) {
        return new OverlayImages(this.makeImage(),
                new TextImage(new Posn(this.width / 2, this.height / 2), s, sCOLOR));
    }

    //Check if World should end:
    // - When Dodger is out of bounds.
    // - If Dodger hit a Thing.
    // - If Dodger hit and Explosion.
    public WorldEnd worldEnds() {
        if (this.dodger.outOfBounds(this.width, this.height)) {
            return new WorldEnd(true, new OverlayImages(this.makeImage(),
                    new TextImage(new Posn(this.width / 2, this.height / 2), "Out of Bounds!" + " SCORE: " + this.score, sCOLOR)));
        }
        if (this.dodger.didCollide(thing1) || this.dodger.didCollide(thing2)) {
            return new WorldEnd(true, new OverlayImages(this.makeImage(),
                    new TextImage(new Posn(this.width / 2, this.height / 2), "You didn't 'Dodge the Thing'!" + " SCORE: " + this.score, sCOLOR)));
        }
        if (this.dodger.didCollide(explosion)) {
            return new WorldEnd(true, new OverlayImages(this.makeImage(),
                    new TextImage(new Posn(this.width / 2, this.height / 2), "You blew up!" + " SCORE: " + this.score, sCOLOR)));
        } else {
            return new WorldEnd(false, this.makeImage());
        }
    }
}

class Testing implements Constants {

    // examples of the Dodger class
    Dodger d1 = new Dodger(new Posn(300, 300), dRADIUS, dCOLOR);
    Dodger d1Left = new Dodger(new Posn(260, 300), dRADIUS, dCOLOR);
    Dodger d1Right = new Dodger(new Posn(340, 300), dRADIUS, dCOLOR);
    Dodger d1Up = new Dodger(new Posn(300, 260), dRADIUS, dCOLOR);
    Dodger d1Down = new Dodger(new Posn(300, 340), dRADIUS, dCOLOR);

    Dodger d2 = new Dodger(new Posn(-20, 300), dRADIUS, dCOLOR);
    Dodger d3 = new Dodger(new Posn(620, 300), dRADIUS, dCOLOR);
    Dodger d4 = new Dodger(new Posn(300, -20), dRADIUS, dCOLOR);
    Dodger d5 = new Dodger(new Posn(300, 620), dRADIUS, dCOLOR);
    
    Dodger d0 = new Dodger(new Posn(20,20), dRADIUS, dCOLOR);

    // examples of the Thing class
    Thing t1 = new Thing(new Posn(300, 300), tRADIUS, 1, 0, tCOLOR);
    Thing t1Right = new Thing(new Posn(340, 300), tRADIUS, 1, 0, tCOLOR);
    Thing t1Right2 = new Thing(new Posn(380, 300), tRADIUS, 1, 0, tCOLOR);
    Thing t2 = new Thing(new Posn(300, 300), tRADIUS, 0, 1, tCOLOR);
    Thing t2Down = new Thing(new Posn(300, 340), tRADIUS, 0, 1, tCOLOR);
    Thing t2Down2 = new Thing(new Posn(300, 380), tRADIUS, 0, 1, tCOLOR);

    Thing t3 = new Thing(new Posn(620, 300), tRADIUS, 1, 0, tCOLOR);
    Thing t4 = new Thing(new Posn(300, 620), tRADIUS, 0, 1, tCOLOR);

    // examples of the Explosion class
    Explosion e0 = new Explosion(new Posn(-60, -60), eRADIUS, eCOLOR);
    Explosion e1 = new Explosion(new Posn(300, 300), eRADIUS, eCOLOR);

    // examples of the DodgeWorld class
    DodgeWorld dw1 = new DodgeWorld(d1, t1, t2, e0, 0);
    DodgeWorld dw1Left = new DodgeWorld(d1Left, t1, t2, e0, 0);
    DodgeWorld dw1Right = new DodgeWorld(d1Right, t1, t2, e0, 0);
    DodgeWorld dw1Up = new DodgeWorld(d1Up, t1, t2, e0, 0);
    DodgeWorld dw1Down = new DodgeWorld(d1Down, t1, t2, e0, 0);

    DodgeWorld dw2 = new DodgeWorld(d2, t1, t2, e0, 0);
//    DodgeWorld dw3 = new DodgeWorld(d1, t3, t4, e0, 0);
//    DodgeWorld dw3Tick = new DodgeWorld(d1, t5, t6, e0, 1);
    DodgeWorld dw4 = new DodgeWorld(d1, t1, t2Down, e0, 0);
    DodgeWorld dw5 = new DodgeWorld(d1, t1Right, t2, e0, 0);
    DodgeWorld dw6 = new DodgeWorld(d1Left, t1Right, t2Down, e0, 0);
    DodgeWorld dw6Tick = new DodgeWorld(d1Left, t1Right2, t2Down2, e0, 0);
    DodgeWorld dw7 = new DodgeWorld(d0, t1, t2, e0, 0);
    DodgeWorld dw7Tick = new DodgeWorld(d0, t1Right, t2Down, e1, 0);
    DodgeWorld dw8 = new DodgeWorld(d1, t1, t2, e1, 0);

    // test method moveDodger in Dodger class
    boolean testMoveDodger(Tester t) {
        return t.checkExpect(this.d1.moveDodger("left"),
                this.d1Left, "test moveDodger - Left " + "\n")
                && t.checkExpect(this.d1.moveDodger("right"),
                        this.d1Right, "test moveDodger - Right " + "\n")
                && t.checkExpect(this.d1.moveDodger("up"),
                        this.d1Up, "test moveDodger - Up " + "\n")
                && t.checkExpect(this.d1.moveDodger("down"),
                        this.d1Down, "test moveDodger - Down " + "\n");
    }

    // test method moveThing in Thing class
    boolean testMoveThing(Tester t) {
        return t.checkExpect(this.t1.moveThing(),
                this.t1Right, "test moveThing - X Move " + "\n")
                && t.checkExpect(this.t2.moveThing(),
                        this.t2Down, "test moveThing - Y Move " + "\n");
    }

    // test didCollide methods in Dodger class
    boolean testDidCollide(Tester t) {
        return t.checkExpect(this.d1.didCollide(t1),
                true, "test didCollide  - thing1 " + "\n")
                && t.checkExpect(this.d1.didCollide(t2),
                        true, "test didCollide - thing2 " + "\n")
                && t.checkExpect(this.d1Right.didCollide(t1Right),
                        true, "test didCollide - thing1Right " + "\n")
                && t.checkExpect(this.d1Down.didCollide(t2Down),
                        true, "test didCollide - thing2Down " + "\n")
                && t.checkExpect(this.d1.didCollide(e1),
                        true, "test didCollide - explosion1")
                && t.checkExpect(this.d1Left.didCollide(e1),
                        true, "test didCollide - explosion1")
                && t.checkExpect(this.d1Right.didCollide(e1),
                        true, "test didCollide - explosion1")
                && t.checkExpect(this.d1Up.didCollide(e1),
                        true, "test didCollide - explosion1")
                && t.checkExpect(this.d1Down.didCollide(e1),
                        true, "test didCollide - explosion1");
    }

    // test method hitThing in Thing class
    boolean testHitThing(Tester t) {
        return t.checkExpect(this.t1.hitThing(t1),
                true, "test hitThing " + "\n")
                && t.checkExpect(this.t2.hitThing(t2),
                        true, "test hitThing " + "\n")
                && t.checkExpect(this.t1.hitThing(t2),
                        true, "test hitThing " + "\n");
    }

    // test method outOfBounds in Dodger class
    boolean testOutOfBounds(Tester t) {
        return t.checkExpect(this.d2.outOfBounds(wWIDTH, wHEIGHT),
                true, "test outOfBounds - Left " + "\n")
                && t.checkExpect(this.d3.outOfBounds(wWIDTH, wHEIGHT),
                        true, "test outOfBounds - Right " + "\n")
                && t.checkExpect(this.d4.outOfBounds(wWIDTH, wHEIGHT),
                        true, "test outOfBounds - Top " + "\n")
                && t.checkExpect(this.d5.outOfBounds(wWIDTH, wHEIGHT),
                        true, "test outOfBounds - Bottom " + "\n");
    }

    // test method atBounds in Dodger class
    boolean testAtBounds(Tester t) {
        return t.checkExpect(this.t3.atBounds(wWIDTH, wHEIGHT),
                true, "test atBounds - Right " + "\n")
                && t.checkExpect(this.t4.atBounds(wWIDTH, wHEIGHT),
                        true, "test atBounds - Bottom " + "\n");
    }

    // helper for testRandInt: boolean for if RandInt is in range given
    boolean checkRandInt() {
        int rand = DodgeWorld.randInt(0, 14);
        return (rand <= 14 && rand >= 0);
    }

    // test method randInt in DodgeWorld class
    boolean testRandInt(Tester t) {
        return t.checkExpect(this.checkRandInt(),
                true, "test RandInt")
                && t.checkExpect(this.checkRandInt(),
                        true, "test RandInt")
                && t.checkExpect(this.checkRandInt(),
                        true, "test RandInt")
                && t.checkExpect(this.checkRandInt(),
                        true, "test RandInt")
                && t.checkExpect(this.checkRandInt(),
                        true, "test RandInt");
    }

    // test method onKeyEvent in DodgeWorld class
    boolean testOnKeyEvent(Tester t) {
        return t.checkExpect(this.dw1.onKeyEvent("left"),
                this.dw1Left, "test onKeyEvent - Left " + "\n")
                && t.checkExpect(this.dw1.onKeyEvent("right"),
                        this.dw1Right, "test onKeyEvent - Right " + "\n")
                && t.checkExpect(this.dw1.onKeyEvent("up"),
                        this.dw1Up, "test onKeyEvent - Up " + "\n")
                && t.checkExpect(this.dw1.onKeyEvent("down"),
                        this.dw1Down, "test onKeyEvent - Down " + "\n");
    }

    // test method onTick in DodgeWorld class
    boolean testOnTick(Tester t) {
        return t.checkExpect(this.dw2.onTick(),
                this.dw2.endOfWorld("OutOfBounds"), "test onTick - Dodger OoB " + "\n")
                && // An onTick call when things are OoB returns new things at random locations: cannot test in this way.
                //		t.checkExpect(this.dw3.onTick(), 
                //					  this.dw3Tick, "test onTick - Things OoB " + "\n") &&
                t.checkExpect(this.dw4.onTick(),
                        this.dw4.endOfWorld("DodgerThingCollision"), "test onTick - Dodger Thing1 Collide " + "\n")
                && t.checkExpect(this.dw5.onTick(),
                        this.dw5.endOfWorld("DodgerThingCollision"), "test onTick - Dodger Thing2 Collide " + "\n")
                && t.checkExpect(this.dw6.onTick(),
                        this.dw6Tick, "test onTick - Normal Move Things " + "\n")
                && t.checkExpect(this.dw7.onTick(),
                        this.dw7Tick, "test onTick - Generate Explosion " + "\n")
                && t.checkExpect(this.dw8.onTick(),
                        this.dw8.endOfWorld("DodgerBlewUp"), "test onTick - Dodger Explosion Collide " + "\n");
    }

}

public class DodgeGame implements Constants {

    public static void main(String[] args) {

        Testing test = new Testing();
        Tester.runReport(test, false, false);

        DodgeWorld w1 = new DodgeWorld(new Dodger(new Posn(300, 300), dRADIUS, dCOLOR),
                new Thing(new Posn(20, 20 + DodgeWorld.randInt(0, 14) * 40), tRADIUS, 1, 0, tCOLOR),
                new Thing(new Posn(20 + DodgeWorld.randInt(0, 14) * 40, 20), tRADIUS, 0, 1, tCOLOR),
                new Explosion(new Posn(-60, -60), eRADIUS, eCOLOR), 0);
        w1.bigBang(wWIDTH, wHEIGHT, 0.07);
    }
}
