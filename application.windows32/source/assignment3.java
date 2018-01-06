import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class assignment3 extends PApplet {

boolean upHold, leftHold, downHold, rightHold, shiftHold, zHold, xHold, cHold = false;

Cam2D camera;

Player player;

Boss boss;

Scores scores;

boolean gameOver;

float gravity = 0.05f;

final String[] E_FILE_NAMES = {"Sprites/Enemies/Enemy1",
                               "Sprites/Enemies/Enemy2",
                               "Sprites/Enemies/Enemy3",
                               "Sprites/Enemies/Enemy4",
                               "Sprites/Enemies/Enemy5"};

final int[] E_FRAMES = { 8, 8, 6, 6, 7};
final int[] E_HEALTH = { 3, 2, 2, 3, 3};
final int[] E_DAMAGE = {15, 5,10,20,25};
final int[] E_SCORE  = {20,20,10,10,30};

// keycodes
private final int UP_KEY    = 38;
private final int LEFT_KEY  = 37;
private final int DOWN_KEY  = 40;
private final int RIGHT_KEY = 39;
private final int SHIFT_KEY = 16;
private final int Z_KEY     = 90;
private final int X_KEY     = 88;
private final int C_KEY     = 67;

ArrayList<Enemy> enemies = new ArrayList<Enemy>();
ArrayList<Particle> particles = new ArrayList<Particle>();
ArrayList<Item> items = new ArrayList<Item>();
ArrayList<TextField> textFields = new ArrayList<TextField>();

int oldMil;          // keep track of the millisecond from last frame
float deltaTime = 0; // the time it took between last frame and this frame

int spawnLoopSpeed = 25;
float oldCamPosSpawn;

PImage backGround;
PImage ground;

int score;

boolean shownIntro, shownBossPoint, showWNY;


public void setup() {
  
  // frameRate(500);
  imageMode(CENTER);
  rectMode(CENTER);
  frameRate(500);
  camera = new Cam2D();
  player = new Player(20,"Sprites/Ship/Ship");

  backGround = loadImage("Sprites/Background.png");
  ground = loadImage("Sprites/Ground.png");

  oldMil = millis();
  oldCamPosSpawn = camera.loc.x;

  boss = new Boss(1500,0);

  scores = new Scores();


}

public void draw() {
  // background(255); no need for background
  // println(frameRate);
  // update delta millisecond
  deltaTime = (millis() - oldMil)*0.06f;
  oldMil = millis();

  // spawn enemies
  spawnController();

  pushMatrix();

  camera.des = new PVector(width/4-player.loc.x,height/2);
  camera.update();

  // draw infinite background
  int min,max;

  min = (int)((-width/2-camera.loc.x)/backGround.width)*backGround.width;
  max = (int)(width*1.5f-camera.loc.x);
  for (int x = min; x < max; x += backGround.width) {
    image(backGround,x,noise(x,0)*912);
  }

  player.update();

  for (int c = 0; c < enemies.size(); c++) {
    Enemy e = enemies.get(c);
    e.update();
  }

  for (int c = 0; c < items.size(); c++) {
    Item i = items.get(c);
    if (i.hitCharacter(player)) {
      if (i.TYPE == Item.FULLHEALTH)
        player.increaseHealth(1000);
      if (i.TYPE == Item.HEALTH20)
        player.increaseHealth(20);
      if (i.TYPE == Item.SHOOTSPEED)
        player.reloadDelay = 100;
      if (i.TYPE == Item.SHOOTSTRAIGHT)
        player.projectileSpread = 0;
      items.remove(c);
    }

    i.update();
  }



  // infinite ground
  min = (int)((-width/2-camera.loc.x)/ground.width)*ground.width;
  max = (int)(width*1.5f-camera.loc.x);
  for (int x = min; x < max; x += ground.width) {
    image(ground,x,-height/2+ground.height/2);
    pushMatrix();
    rotate(PI);
    image(ground,-x,-height/2+ground.height/2);
    popMatrix();
  }

  // draw the boss after the ground
  if (boss != null) {
    boss.update();
  }

  for (int c = 0; c < particles.size(); c++) {
    Particle p = particles.get(c);
    p.update();
  }
  popMatrix();

  scores.showScores();

  // if have not done text, do so.
  if (!shownIntro) {
    textFields.add(new TextField(
      "Welcome, arrow keys=move, Z=shoot, and Shift=slow"
      ,5000));
    shownIntro = true;
  }

  if (scores.score >= 20 && !shownBossPoint) {
    textFields.add(new TextField(
      "Yeah, that's it. The boss is at the score of 150."
      ,5000));
    shownBossPoint = true;
  }

  if (scores.score >= 100 && !showWNY) {
    textFields.add(new TextField(
      "The world NEEDS you!"
      ,2500));
    showWNY = true;
  }

  for (int c = 0; c < textFields.size(); c++) {
    TextField tf = textFields.get(c);
    tf.update();
  }

}

public void spawnController() {

  if (boss != null && !boss.active && scores.score >= 150) {
    textFields.add(new TextField(
      "Here comes the boss, shoot at the core!"
      ,5000));
    boss.active = true;
  }

  if(oldCamPosSpawn - camera.loc.x > spawnLoopSpeed) {
    if ((int)random(2) == 0) {

      if ((int)random(20) == 0) {
        Enemy e = new Enemy(width+100-camera.loc.x,
          random(-height/2+ground.height,height/2-ground.height), 1);

          e.vel.x = -2.5f;
          enemies.add(e);

      } else if ((int)random(20) == 0) {
        Enemy e = new Enemy(width+100-camera.loc.x,
          random(-height/2+ground.height,height/2-ground.height), 2);

          e.vel.x = -2;
          enemies.add(e);

      } else if ((int)random(30) == 0) {
        Enemy e = new Enemy(width+100-camera.loc.x,
          random(-height/2+ground.height,height/2-ground.height), 0);

          e.vel.x = -1;
          enemies.add(e);
      } else if ((int)random(30) == 0) {
        Enemy e = new Enemy(width+100-camera.loc.x,
          random(-height/2+ground.height,height/2-ground.height), 3);

          e.vel.x = -1;
          enemies.add(e);
      } else if ((int)random(30) == 0) {
        Enemy e = new Enemy(width+100-camera.loc.x,
          random(-height/2+ground.height,height/2-ground.height), 4);

          e.vel.x = -2.5f;
          enemies.add(e);
      }
    }


    oldCamPosSpawn = camera.loc.x;
  }
}

public void keyPressed() {
  switch (keyCode) {
    case UP_KEY:
      upHold = true;
      break;
    case LEFT_KEY:
      leftHold = true;
        break;
    case DOWN_KEY:
      downHold = true;
      break;
    case RIGHT_KEY:
      rightHold = true;
      break;
    case SHIFT_KEY:
      shiftHold = true;
      break;
    case Z_KEY:
      zHold = true;
      break;
    case X_KEY:
      xHold = true;
      break;
    case C_KEY:
      cHold = true;
      break;
  }
}

public void keyReleased() {
  switch (keyCode) {
    case UP_KEY:
      upHold = false;
      break;
    case LEFT_KEY:
      leftHold = false;
        break;
    case DOWN_KEY:
      downHold = false;
      break;
    case RIGHT_KEY:
      rightHold = false;
      break;
    case SHIFT_KEY:
      shiftHold = false;
      break;
    case Z_KEY:
      zHold = false;
      break;
    case X_KEY:
      xHold = false;
      break;
    case C_KEY:
      cHold = false;
      break;
  }
}
class Actor extends GameObject {

  PImage img;

  int cWidth, cHeight;

  float health, maxHealth;

  boolean topWall, bottomWall, rightWall, leftWall;

  boolean imgFlip;
  /**
   * start a new actor
   */
  Actor() {
    super();
  }

  /**
   * start a new actor
   * @param x_ x location
   * @param y_ y location
   */
  Actor(float x_, float y_) {
    super(x_, y_);
  }

  /**
   * start a new actor
   * @param x_        x location
   * @param y_        y location
   * @param fileName_ file location of the actor image
   */
  Actor(float x_, float y_, String fileName_) {
    super(x_, y_);
    img = loadImage(fileName_);
  }

  Actor(String fileName_) {
    super();
    img = loadImage(fileName_);
  }

  /**
   * Overrides the default blank gameObject update method
   * Should be ran every game tick
   */
  public @Override
  void update() {
    simplePhysicsCal();
    //offScreenB();
  }

  /**
   * Should be over written
   */
  public void drawImage() {
    pushMatrix();
    translate(loc.x,loc.y);

    ellipse(0,0,10,10);

    popMatrix();
  }

  /**
   * Offscreen Switch
   */
  public void offScreenS(float screenExtention) {
    if (loc.x < 0 - img.width/2       -screenExtention)
      loc.x = width + img.width/2     +screenExtention;
    if (loc.x > width + img.width/2   +screenExtention)
      loc.x = 0 - img.width/2         -screenExtention;
    if (loc.y < 0 - img.height/2      -screenExtention)
      loc.y = height + img.height/2   +screenExtention;
    if (loc.y > height + img.height/2 +screenExtention)
      loc.y = 0 - img.height/2        -screenExtention;

  }

  /**
   * Offscreen bounce
   */
  public void offScreenB() {

    if ((loc.x < 0 + img.width/2 && vel.x < 0) || (loc.x > width - img.width/2 && vel.x >= 0))
      vel.x *= -1;

    if ((loc.y < 0 + img.height/2 && vel.y < 0) || (loc.y > height - img.height/2 && vel.y >= 0))
      vel.y *= -1;

  }

  public boolean hitCharacter(Actor actor) {

    // box inside box collision

    float aWidthMin  = loc.x-cWidth/2;
    float aWidthMax  = loc.x+cWidth/2;
    float aHeightMin = loc.y-cHeight/2;
    float aHeightMax = loc.y+cHeight/2;

    float bWidthMin  = actor.loc.x-actor.cWidth/2;
    float bWidthMax  = actor.loc.x+actor.cWidth/2;
    float bHeightMin = actor.loc.y-actor.cHeight/2;
    float bHeightMax = actor.loc.y+actor.cHeight/2;

    if (aWidthMax > bWidthMin && bWidthMax > aWidthMin &&
        aHeightMax > bHeightMin && bHeightMax > aHeightMin)
        return true;
    return false;

    // ellipse(aWidthMin,loc.y   ,10,10);
    // ellipse(aWidthMax,loc.y   ,10,10);
    // ellipse(loc.x,aHeightMin   ,10,10);
    // ellipse(loc.x,aHeightMax   ,10,10);
    // ellipse(bWidthMin,actor.loc.y   ,10,10);
    // ellipse(bWidthMax,actor.loc.y   ,10,10);
    // ellipse(actor.loc.x,bHeightMin   ,10,10);
    // ellipse(actor.loc.x,bHeightMax   ,10,10);
  }

  public void decreaseHealth(float delta) {
    health -= delta;
  }

  public void increaseHealth(float delta) {
    health += delta;
    if (health > maxHealth)
      health = maxHealth;
  }

  public void checkWalls() {


    float camWidthMin  = -camera.loc.x;
    float camWidthMax  = -camera.loc.x+width;
    float camHeightMin = camera.loc.y-height;
    float camHeightMax = camera.loc.y;

    rightWall = loc.x-cWidth/2 > camWidthMax;
    leftWall  = loc.x+cWidth/2 < camWidthMin;
    topWall   = loc.y+cHeight/2 < camHeightMin;
    bottomWall= loc.y-cHeight/2 > camHeightMax;
  }

}
class Boss extends Enemy {

  ArrayList<Projectile> projectiles;

  final int SHOOT   = 10;
  final int SHIELD  = 11;
  final int LASER   = 12;

  int lastState;

  boolean animateReverse;
  int animateState;

  int lastMilSate;
  int stateSwitchDelay;

  int lastMilShoot;
  int shootDelay;

  int lastMilLaser;
  int laserDelay;

  float range = 100;

  boolean laser;

  boolean active;

  int laserPower = 10;
  int projectilePower = 20;

  PImage laserImg;

  Boss (float x_, float y_) {
    super(x_, y_, -1, 50, 17, "Sprites/Boss/Boss");
    state = (int)random(11,13);
    lastState = SHIELD;
    animationSpeed = 75;
    stateSwitchDelay = 1000;
    lastMilSate = millis();
    shootDelay = 1000;
    lastMilShoot = millis();
    laserDelay = 250;
    lastMilLaser = millis();

    laserImg = loadImage("Sprites/Projectiles/Laser0000.png");

    smoothFactor = 0.1f;

    projectiles = new ArrayList<Projectile>();
  }

  public @Override
  void update() {

    if (active) {

      if(state != DEAD && health <= 0) {
        state = DEAD;
        scores.score += 1000;

        textFields.add(new TextField(
          "Well, good job. You can now continue"
          ,5000));

        Particle particle = new Particle(loc.x, loc.y, Particle.ANIM, 4, "Sprites/Exposions/ExplosionLarge");
        particle.animationSpeed = 100;
        particles.add(particle);
      }

      if (state == DEAD) {
        acc.y = gravity;
        simplePhysicsCal();
        if (loc.y > height + img.height)
          boss = null;
      } else {

        des.x = -camera.loc.x+width*0.8f;

        smoothMove();
        simplePhysicsCal();

        stateController();

        actionController();
        prjectileController();
        laserController();
      }

    } else {
      loc.x = -camera.loc.x+width*1.2f;
    }

    drawImage();
  }

  public void prjectileController() {
    for (int c = 0; c < projectiles.size(); c++) {
      Projectile p = projectiles.get(c);

      if (p.dead) {
        projectiles.remove(p);
        continue;
      }

      if (p.hit(player)) {
        player.decreaseHealth(p.power);
        player.lastMilTint = millis();
        projectiles.remove(p);
        continue;
      }

      p.update();
    }
  }

  public void actionController() {
    if (millis() - lastMilShoot > shootDelay &&
        animationDone() && state == SHOOT) {

      // do shoot code

      for (int c = -15; c <= 15; c+=2) {
        int tempC = c;
        int delta = 100;
        PVector v = new PVector(-30,0);
        if (c > 0) {
          v.rotate((map(c,15,0,0,15)) * 0.006f);
          tempC = c+delta+c*15;
        } else {
          v.rotate((map(c,-15,0,0,15))*-0.006f);
          tempC = c-delta+c*15;
        }

        projectiles.add(new Projectile(loc.x+150,loc.y+tempC,v.x,v.y,projectilePower,"Sprites/Projectiles/BossProjectile0000.png"));
      }
      lastMilShoot = millis();
    } else {
      laser = animationDone() && state == LASER;
    }

  }

  public void stateController() {
    if (player.state == player.DEAD) {
      state = SHIELD;
    } else if (millis() - lastMilSate > stateSwitchDelay &&
               animationDone() && (int)random(200) == 0) {
      if (state == SHIELD) {
        if ((int)random(5) == 0) {
          stateSwitchDelay = 500;
          state = SHOOT;
        }
        else {
          state = LASER;
          stateSwitchDelay = 1000;
        }


      } else if (state == LASER || state == SHOOT){
        stateSwitchDelay = 750;
        state = SHIELD;
      }

      lastMilSate = millis();
    }

    animationController();

    lastState = state;
  }

  public void animationController() {
    if (lastState == SHIELD && state == SHOOT) {
      animateState = SHOOT;
      animateReverse = false;
      frameC = 8;
    }

    if (lastState == SHIELD && state == LASER) {
      animateState = LASER;
      animateReverse = false;
      frameC = 0;
    }

    if (lastState == LASER && state == SHIELD) {
      animateState = LASER;
      animateReverse = true;
    }

    if (lastState == SHOOT && state == SHIELD) {
      animateState = SHOOT;
      animateReverse = true;
    }


    if (animateState == SHOOT){
      if (animateReverse)
        animate(8,16,animationSpeed,false,true);
      else
        animate(8,16,animationSpeed,false,false);
    }

    if (animateState == LASER){
      if (animateReverse)
        animate(0,7,animationSpeed,false,true);
      else
        animate(0,7,animationSpeed,false,false);
    }


    // animate(0,0,animationSpeed,false,false);
  }

  public boolean animationDone() {
    return ((state == SHIELD && (frameC == 0 || frameC == 8)) ||
           (state == LASER && frameC == 7) ||
           (state == SHOOT && frameC == 16));
  }

  public void laserController() {
    if (laser) {
      float offsetX = 55;
      if (millis() - lastMilLaser > laserDelay) {
        lastMilLaser = millis();
        // box inside box collision

        float aWidthMin  = loc.x-laserImg.width;
        float aWidthMax  = loc.x+offsetX;
        float aHeightMin = loc.y-laserImg.height/2;
        float aHeightMax = loc.y+laserImg.height/2;

        float bWidthMin  = player.loc.x-player.cWidth/2;
        float bWidthMax  = player.loc.x+player.cWidth/2;
        float bHeightMin = player.loc.y-player.cHeight/2;
        float bHeightMax = player.loc.y+player.cHeight/2;

        if (aWidthMax > bWidthMin && bWidthMax > aWidthMin &&
            aHeightMax > bHeightMin && bHeightMax > aHeightMin) {

          player.decreaseHealth(laserPower);
          player.lastMilTint = millis();

          Particle particle = new Particle(player.loc.x, player.loc.y, Particle.TEXT, 0, "-"+laserPower);
          particle.floatUp = true;
          particles.add(particle);

        }
      }

      pushMatrix();
      translate(loc.x-laserImg.width/2+offsetX,loc.y);

      image(laserImg,0,0);

      popMatrix();

    }
  }

  public @Override
  void drawImage() {
    pushMatrix();
    translate(loc.x,loc.y);

    if (imgFlip)
      scale(-1,1);

    if (state == DEAD)
      tint(100);
    else if (millis() - lastMilTint < tintDuration)
    tint(255, 0, 0);

    if(img != null)
      image(img, 0, 0);

    noTint();

    popMatrix();
  }

}
class Cam2D extends GameObject {


  /**
   * Creates a camera where 0,0 is at the center of the screen
   * Also starts smooth panning
   */
  Cam2D() {
    super(width/2, height/2);

    des = loc.copy();
    smoothFactor = .025f;
  }

  /**
   * Creates a camera where 0,0 is at x,y
   * Also starts smooth panning
   * @param x_ x locatoin for center
   * @param y_ y locatoin for center
   */
  Cam2D(float x_, float y_) {
    super(x_, y_);

    des = loc.copy();
    smoothFactor = .1f;
  }

  /**
   * Overrides the default blank gameObject update method
   * Should be ran every game tick
   */
  public @Override
  void update() {
    smoothMove();

    if (vel.x > 0)
      vel.x = 0;

    simplePhysicsCal();
    translate(loc.x, loc.y);
  }

}
class Enemy extends Actor{

  int animationSpeed = (int)random(100-20,100+20);


  int lastMilAni;
  int frameC;

  final int TYPE;

  int tintDuration = 50;
  int lastMilTint;
  ArrayList<PImage> images;

  boolean lastHitPlayer;

  int state;

  final int DEAD = -1;
  final int NORM = 0;

  Enemy(int type_) {
    //int health_, int size_, String fileName_
    super(E_FILE_NAMES[type_]+"0000.png");
    lastMilAni = millis();
    frameC = 0;

    images = new ArrayList<PImage>();
    for (int c = 0; c < E_FRAMES[type_]; c++)
      images.add(loadImage(E_FILE_NAMES[type_]+nf(c,4)+".png"));
    img = images.get(0);

    health = E_HEALTH[type_];

    cWidth = img.width;
    cHeight = img.height;

    TYPE = type_;
  }

  Enemy(float x_, float y_, int type_) {
    super(x_, y_,E_FILE_NAMES[type_]+"0000.png");
    lastMilAni = millis();
    frameC = 0;

    images = new ArrayList<PImage>();
    for (int c = 0; c < E_FRAMES[type_]; c++)
      images.add(loadImage(E_FILE_NAMES[type_]+nf(c,4)+".png"));
    img = images.get(0);

    health = E_HEALTH[type_];

    cWidth = img.width;
    cHeight = img.height;

    TYPE = type_;
  }

  Enemy(float x_, float y_, int type_, int health_, int size_, String fileName_) {
    super(x_, y_,fileName_+"0000.png");
    lastMilAni = millis();
    frameC = 0;

    images = new ArrayList<PImage>();
    for (int c = 0; c < size_; c++)
      images.add(loadImage(fileName_+nf(c,4)+".png"));
    img = images.get(0);

    health = health_;

    cWidth = img.width;
    cHeight = img.height;

    TYPE = type_;
  }

  public @Override
  void update() {

    checkWalls();

    if(state == NORM && health <= 0) {
      scores.score += E_SCORE[TYPE];

    if ((int)random(5) == 0) {
      if ((int)random(5) == 0)
        items.add(new Item(loc.x,loc.y,Item.FULLHEALTH));
      else
        items.add(new Item(loc.x,loc.y,(int)random(1,4)));
    }

    particles.add(new Particle(loc.x, loc.y, Particle.ANIM, 4, "Sprites/Exposions/ExplosionSmall"));

      state = DEAD;
    } else if (state == DEAD) {
      acc.y = gravity;

    } else if (leftWall || topWall || bottomWall) {
      enemies.remove(this);
      return;
    }

    simplePhysicsCal();

    if (state == NORM)
      animate(0, images.size()-1,animationSpeed,true,false);

    damageIfHit();

    drawImage();
  }

  public void damageIfHit() {
    boolean hitPlayer = hitCharacter(player);



    if (TYPE < E_DAMAGE.length && TYPE >= 0 && hitPlayer && !lastHitPlayer){
      player.decreaseHealth(E_DAMAGE[TYPE]);

      Particle particle = new Particle(player.loc.x, player.loc.y, Particle.TEXT, 0, "-"+E_DAMAGE[TYPE]);
      particle.floatUp = true;
      particles.add(particle);

      player.lastMilTint = millis();
    }

    lastHitPlayer = hitPlayer;
  }

  public void animate(int min, int max, int aniSpeed, boolean loop, boolean reverse) {

    if (millis() - lastMilAni > aniSpeed) {
      if (reverse)
        frameC--;
      else
        frameC++;

      lastMilAni = millis();
    }

    if (reverse) {
      if (frameC > max)
        frameC = max;
      else if (loop && frameC < min)
        frameC = max;
      else if (!loop && frameC < min)
        frameC = min;
    } else {
      if (frameC < min)
      frameC = min;
      else if (loop && frameC > max)
      frameC = min;
      else if (!loop && frameC > max)
      frameC = max;
    }

    img = images.get(frameC);
  }

  public @Override
  void drawImage() {
    pushMatrix();
    translate(loc.x,loc.y);

    if (state == DEAD)
      tint(100);
    else if (millis() - lastMilTint < tintDuration)
    tint(255, 0, 0);

    if(img != null)
      image(img, 0, 0);

    noTint();

    popMatrix();
  }

}
class GameObject {
  PVector loc; // location Vector
  PVector vel; // Velocity Vector
  PVector acc; // Acceleration Vector

  PVector des; // Smooth pan destination, only use for smoothpan
  float smoothFactor; //Smooth factor from 0 to 1 where 0 is no smootning

  /**
   * start a new gameObject
   */
  GameObject() {
    loc = new PVector();
    vel = new PVector();
    acc = new PVector();
    des = new PVector();
  }

  /**
   * start a new gameObject
   * @param x_ x location
   * @param y_ y location
   */
  GameObject(float x_, float y_) {
    loc = new PVector(x_,y_);
    vel = new PVector();
    acc = new PVector();
    des = new PVector();
  }

  public void update(){}

  public void simplePhysicsCal() {
    vel.add(acc.copy().mult(deltaTime));
    loc.add(vel.copy().mult(deltaTime));
  }

  /**
   * do smoothpan with destination
   * sets new vel variable
   */
  public void smoothMove() {
    float vX = des.x - loc.x;
    float vY = des.y - loc.y;

    vX *= smoothFactor;
    vY *= smoothFactor;

    vel = new PVector(vX, vY);
  }

  public void setsmoothFactor(float smoothFactor_) {
    smoothFactor = smoothFactor_;
  }


}
class Item extends Actor {

  final int TYPE;

  final static int FULLHEALTH    = 0;
  final static int HEALTH20      = 1;
  final static int SHOOTSPEED    = 2;
  final static int SHOOTSTRAIGHT = 3;

  PFont font;

  float randomNumber = random(1000);

  Item(float x_, float y_, int type_) {
    super(x_,y_);
    TYPE = type_;

    font = scores.getFont();

    cWidth = 30;
    cHeight = 30;
  }

  public @Override
  void update() {
    checkWalls();


    if (rightWall || leftWall || topWall || bottomWall) {
      items.remove(this);
      return;
    }

    drawImage();
  }

  public @Override
  void drawImage() {
    pushMatrix();
    translate(loc.x,loc.y+sin((millis()+randomNumber)*0.005f)*5);

    if (TYPE == FULLHEALTH) {
      fill(0xffed2525);
      stroke(0xffffffff);
      strokeWeight(3);
      rect(0,0,cWidth,cHeight);
      noStroke();
      textAlign(CENTER);
      fill(0xffffffff);
      textFont(font, 20);
      text("+", 0, cHeight*.35f);
    } else if (TYPE == HEALTH20) {
      fill(0xffe8ad2e);
      stroke(0xffffffff);
      strokeWeight(3);
      rect(0,0,cWidth,cHeight);
      noStroke();
      textAlign(CENTER);
      fill(0xffffffff);
      textFont(font, 20);
      text("+", 0, cHeight*.35f);
    } else if (TYPE == SHOOTSPEED) {
      fill(0xff2fd9ef);
      stroke(0xffffffff);
      strokeWeight(3);
      rect(0,0,cWidth,cHeight);
      noStroke();
      textAlign(CENTER);
      fill(0xffffffff);
      textFont(font, 20);
      text("S", 1.5f, cHeight*.35f);
    } else if (TYPE == SHOOTSTRAIGHT) {
      fill(0xff2ee850);
      stroke(0xffffffff);
      strokeWeight(3);
      rect(0,0,cWidth,cHeight);
      noStroke();
      textAlign(CENTER);
      fill(0xffffffff);
      textFont(font, 20);
      text("A", 1.25f, cHeight*.35f);
    }

    popMatrix();
  }

}
class Particle extends GameObject{

  PImage img;
  PFont pixelFont;

  int lastMilAni;
  int frameC;
  ArrayList<PImage> images;

  boolean floatUp;

  float floatSpeed;
  int animationSpeed = 50;

  String storedText;
  float textAlpha;

  final int MODE;

  final static int TEXT = 0;
  final static int ANIM = 1;

  int life = 2500;
  int birthMil;

  /**
   * start a new particle
   * @param x_        x location
   * @param y_        y location
   * @param mode_     text or image mode
   * @param input_    input
   */
  Particle(float x_, float y_, int mode_, int size_, String input_) {
    super(x_, y_);
    MODE = mode_;

    if (MODE == TEXT) {
      storedText = input_;
      pixelFont = scores.getFont();
    } else {
      images = new ArrayList<PImage>();
      for (int c = 0; c < size_; c++)
        images.add(loadImage(input_+nf(c,4)+".png"));
      img = images.get(0);

    }

    floatSpeed = -1;
    birthMil = millis();
  }

  public void update() {
    if (floatUp) {

      vel.y = floatSpeed;

      simplePhysicsCal();
    }

    if (MODE == TEXT) {
      textAlpha = map(millis() - birthMil, 0, life, 255, 0);
      if (textAlpha <= 0) {
        particles.remove(this);
      }
    }

    if (MODE == ANIM) {
      animate(0,images.size()-1,animationSpeed,false,false);
    }

    drawImage();
  }

  public void animate(int min, int max, int aniSpeed, boolean loop, boolean reverse) {

    if (millis() - lastMilAni > aniSpeed) {
      if (reverse)
        frameC--;
      else
        frameC++;

      lastMilAni = millis();
    }

    if (reverse) {
      if (frameC > max)
        frameC = max;
      else if (loop && frameC < min)
        frameC = max;
      else if (!loop && frameC < min)
        frameC = min;
    } else {
      if (frameC < min)
        frameC = min;
      else if (loop && frameC > max)
        frameC = min;
      else if (!loop && frameC > max) {
        frameC = max;

        // remove after animation is done
        particles.remove(this);
        return;
      }
    }

    img = images.get(frameC);
  }

  public void drawImage() {
    pushMatrix();
    translate(loc.x,loc.y);

    if (MODE == TEXT) {
      textAlign(CENTER);
      fill(255,255,255,textAlpha);
      textFont(pixelFont, 15);
      text(storedText,0,0);
    } else {
      image(img,0,0);
    }

    popMatrix();
  }
}
class Player extends Actor {
  float dragX = 0.01f;
  float velLimit = 10;
  float inputDelta = 0.5f;

  float reloadDelay = 200;
  int lastMilreLoad;

  float projectileSpread = 0.02f;

  int animationSpeed = 250;

  int lastMilAni;
  int frameC;
  ArrayList<PImage> images;

  ArrayList<Projectile> projectiles;

  int tintDuration = 50;
  int lastMilTint;

  int state;

  final int NORM = 0;
  final int DEAD = 1;

  Player(int size_, String fileName_) {
    super(fileName_+"0000.png");
    lastMilAni = millis();
    frameC = 0;

    lastMilreLoad = millis();

    projectiles = new ArrayList<Projectile>();

    images = new ArrayList<PImage>();
    for (int c = 0; c < size_; c++)
      images.add(loadImage(fileName_+nf(c,4)+".png"));
    img = images.get(0);

    maxHealth = 100;
    health = maxHealth;

    cWidth = img.width;
    cHeight = img.height/2;
  }

  Player(float x_, float y_, int size_, String fileName_) {
    super(x_, y_,fileName_+"0000.png");
    lastMilAni = millis();
    frameC = 0;

    projectiles = new ArrayList<Projectile>();

    images = new ArrayList<PImage>();
    for (int c = 0; c < size_; c++)
      images.add(loadImage(fileName_+nf(c,4)+".png"));
    img = images.get(0);

    health = 100;

    cWidth = img.width;
    cHeight = img.height/2;
  }

  public @Override
  void update() {
    if (state == NORM && health <= 0) {
      state = DEAD;
    } else if (state == DEAD && loc.y < camera.loc.y+height/2) {
      movementControl(new PVector());
    } else if (state == NORM){
      inputCalculations();

      checkWalls();

      animationController();

      prjectileController();
    }

    drawImage();
  }

  public void prjectileController() {

    if (zHold && millis() - lastMilreLoad > reloadDelay) {
      PVector v = new PVector(30,0);
      v.rotate(random(-PI*projectileSpread,PI*projectileSpread));
      projectiles.add(new Projectile(loc.x,loc.y,v.x,v.y,1,randomTexturedProjectile()));
      lastMilreLoad = millis();
      scores.shot++;
    }

    for (int c = 0; c < projectiles.size(); c++) {
      Projectile p = projectiles.get(c);

      // if off screen
      if (p.dead) {
        projectiles.remove(p);
        continue;
      }

      // if hit boss
      if (boss != null && boss.state != boss.DEAD && p.hit(boss)) {
        if (p.hitBossPart == p.BODY) {
          boss.decreaseHealth(p.power*1);
          boss.lastMilTint = millis();

          Particle particle = new Particle(p.loc.x, p.loc.y, Particle.TEXT, 0, "-"+p.power);
          particle.floatUp = true;
          particles.add(particle);

          projectiles.remove(p);
          scores.shotHit++;
          continue;

        } else if (p.hitBossPart == p.ARM) {
          boss.decreaseHealth(p.power*.25f);
          boss.lastMilTint = millis();

          Particle particle = new Particle(p.loc.x, p.loc.y, Particle.TEXT, 0, "-"+p.power*.25f);
          particle.floatUp = true;
          particles.add(particle);

          projectiles.remove(p);
          scores.shotHit++;
          continue;

        } else if (p.hitBossPart == p.CRYSTAL) {
          boss.decreaseHealth(p.power*5);
          boss.lastMilTint = millis();

          Particle particle = new Particle(p.loc.x, p.loc.y, Particle.TEXT, 0, "-"+p.power*5);
          particle.floatUp = true;
          particles.add(particle);

          projectiles.remove(p);
          scores.shotHit++;
          continue;

        } else if (!p.bounced){
          // bounce off shell if hit

          float xDif = max(p.loc.x,boss.loc.x)-min(p.loc.x,boss.loc.x);
          float yDif = max(p.loc.y,boss.loc.y)-min(p.loc.y,boss.loc.y);

          float angle = atan2(yDif,xDif);

          float angleDifference = angle - p.vel.heading();
          angleDifference = p.loc.y < boss.loc.y ? -angleDifference : angleDifference;


         float newAngle = PI-p.vel.heading()-angleDifference*2;

         PVector temp = new PVector(p.vel.mag(),0);

         temp.rotate(newAngle);

         p.vel = temp;
         p.bounced = true;

        }
      }


      // if hit enemy
      for (int i = 0; i < enemies.size(); i++) {
        Enemy e = enemies.get(i);
        if (p.hit(e)) {
          if (e.state == e.NORM) {
            e.decreaseHealth(p.power);
            e.lastMilTint = millis();

            Particle particle = new Particle(p.loc.x, p.loc.y, Particle.TEXT, 0, "-"+p.power);
            particle.floatUp = true;
            particles.add(particle);

            scores.shotHit++;
          }

          projectiles.remove(p);
          continue;
        }
      }

      p.update();
    }
  }

  public String randomTexturedProjectile() {
    int rand = (int)random(0,8);
    return "Sprites/Projectiles/Projectile000"+rand+".png";
  }

  public void inputCalculations() {
    // make a new input vector
    PVector input = new PVector();

    // set the input vector
    if (rightHold)
      input.x += 1;
    if (leftHold)
      input.x -= 1;
    if (downHold)
      input.y += 1;
    if (upHold)
      input.y -= 1;

    // make it so when it goes diaginal that it wont go twice as fast
    input.normalize();
    movementControl(input);
  }

  public void movementControl(PVector input_) {
      // speedup for slowdown the input vector by delta



      input_.mult(inputDelta);



      // gravity
      input_.add(new PVector(0, gravity));

      // make input the acc
      acc = input_;

      // limit speed of the aircraft
      if (shiftHold)
        vel.limit(velLimit/2);
      else
        vel.limit(velLimit);

      //movement dampening on x axis
      vel.sub(new PVector(vel.x*dragX*deltaTime, 0));

      // calculate physics
      simplePhysicsCal();
  }

  public void animationController() {

    float angleStep = velLimit*.1f;
    float angleChangePoint1 = 2.5f;
    float angleChangePoint2 = 7;

    if (rightHold) {

      // boost mode
      if (vel.y > angleStep*angleChangePoint1) {
        // if going down
        if(vel.y > angleStep*angleChangePoint2)
          // larger angle for faster lowering speed
          animate(10,11,animationSpeed,true);
        else
          animate(12,13,animationSpeed,true);


      } else if (vel.y < -angleStep*angleChangePoint1) {
        // if going up
        if(vel.y < -angleStep*angleChangePoint2)
          // larger angle for faster ascending speed
          animate(18,19,animationSpeed,true);
        else
          animate(16,17,animationSpeed,true);


      } else {
        //if not up or down
        animate(14,15,animationSpeed,true);
      }


    } else {

      // not boost mode
      if (vel.y > angleStep*angleChangePoint1) {
        // if going down
        if(vel.y > angleStep*angleChangePoint2)
          // larger angle for faster lowering speed
          animate(0,1,animationSpeed,true);
        else
          animate(2,3,animationSpeed,true);


      } else if (vel.y < -angleStep*angleChangePoint1) {
        // if going up
        if(vel.y < -angleStep*angleChangePoint2)
          // larger angle for faster ascending speed
          animate(8,9,animationSpeed,true);
        else
          animate(6,7,animationSpeed,true);


      } else {
        //if not up or down
        animate(4,5,animationSpeed,true);
      }
    }
  }

  public void animate(int min, int max, int aniSpeed, boolean loop) {
    if (millis() - lastMilAni > aniSpeed) {
      frameC++;
      lastMilAni = millis();
    }

    if (frameC < min)
      frameC = min;
    else if (loop && frameC > max)
      frameC = min;
    else if (!loop && frameC > max)
      frameC = max;

    img = images.get(frameC);
  }

  public @Override
  void drawImage() {
    pushMatrix();
    translate(loc.x,loc.y);

    if (state == DEAD)
      tint(100);
    else if (millis() - lastMilTint < tintDuration)
    tint(255, 0, 0);

    if (imgFlip)
      scale(-1,1);

    if(img != null)
      image(img, 0, 0);

    noTint();

    popMatrix();
  }

  public @Override
  void checkWalls() {

    float camWidthMin  = -camera.loc.x;
    float camWidthMax  = -camera.loc.x+width;
    float camHeightMin = camera.loc.y-height;
    float camHeightMax = camera.loc.y;

    rightWall = loc.x+cWidth/2 > camWidthMax;
    leftWall  = loc.x-cWidth/2 < camWidthMin;
    topWall   = loc.y-cHeight/2 < camHeightMin+ground.height;
    bottomWall= loc.y+cHeight/2 > camHeightMax-ground.height;

    if (leftWall) {
      loc.x = camWidthMin+cWidth/2;
      vel.x = 0;
    }
    if (topWall) {
      vel.y *= -0.5f;
      loc.y = camHeightMin + ground.height + cHeight/2;
    }
    if (bottomWall) {
      vel.y *= -0.5f;
      loc.y = camHeightMax - ground.height - cHeight/2;
    }
  }



}
class Projectile {

  PVector loc; // location Vector
  PVector vel; // Velocity Vector
  PVector acc; // Acceleration Vector

  boolean dead;

  PImage img;

  boolean bounced;

  int hitBossPart;

  final int ARM = 0;
  final int BODY = 1;
  final int CRYSTAL = 2;
  final int SHELL = 3;

  int power;

  Projectile() {
    loc = new PVector();
    vel = new PVector();
    acc = new PVector();
  }

  /**
   * start a new projectile
   * @param x_ x location
   * @param y_ y location
   */
  Projectile(float x_, float y_, int power_) {
    loc = new PVector(x_,y_);
    vel = new PVector();
    acc = new PVector();
    power = power_;
  }

  Projectile(float x_, float y_, float xSpeed_, float ySpeed_, int power_, String fileName_) {
    loc = new PVector(x_,y_);
    vel = new PVector(xSpeed_, ySpeed_);
    acc = new PVector();
    img = loadImage(fileName_);
    power = power_;
  }

  public void simplePhysicsCal() {
    vel.add(acc.copy().mult(deltaTime));
    loc.add(vel.copy().mult(deltaTime));
  }

  public void drawImage() {

    pushMatrix();
    translate(loc.x,loc.y);
    rotate(vel.heading());


    if(img != null)
      image(img, 0, 0);
    else
      ellipse(0,0,10,10);

    popMatrix();
  }

  public void update() {
    simplePhysicsCal();
    drawImage();
    checkWalls();
  }

  public void checkWalls() {
    float buffer = 100;

    float camWidthMin  = -camera.loc.x-buffer;
    float camWidthMax  = -camera.loc.x+width+buffer;
    float camHeightMin = camera.loc.y-height-buffer;
    float camHeightMax = camera.loc.y+buffer;
    if(loc.x > camWidthMax || loc.x < camWidthMin || loc.y > camHeightMax || loc.y < camHeightMin) {
      dead = true;
      return;
    }
    dead = false;
  }

  public boolean hit(Actor actor) {

    // dot inside box collision
    // dot is a bit ahead of the center of the projectile based on velocity

    float aX = loc.x + vel.x/2;
    float aY = loc.y + vel.y/2;

    if (actor.getClass().getSimpleName().equals("Boss")) {
      Boss b = (Boss)actor;

      int fNum = b.frameC;

      int laserGunPath = 10;

      // ellipse(b.loc.x+80,b.loc.y,50,50);

      if (fNum == 0 || fNum == 1 || fNum == 2 || fNum == 8) {
        if (dist(aX,aY,b.loc.x+40,b.loc.y) < 310/2) {
          hitBossPart = SHELL;
          return true;
        }

        // ellipse(b.loc.x+40,b.loc.y,310,310);
      } else if (fNum == 3) {

        if(dist(aX,aY,b.loc.x+50,b.loc.y) < 230/2 && (aY < b.loc.y-laserGunPath || aY > b.loc.y+laserGunPath)) {
          hitBossPart = BODY;
          return true;
        } else if (dist(aX,aY,b.loc.x+80,b.loc.y) < 50/2) {
          hitBossPart = CRYSTAL;
          return true;
        } else {
          boolean b0 = dist(aX,aY,b.loc.x-113,b.loc.y-71 ) < 60 /2;
          boolean b1 = dist(aX,aY,b.loc.x-55 ,b.loc.y-85 ) < 77 /2;
          boolean b2 = dist(aX,aY,b.loc.x+9  ,b.loc.y-108) < 80 /2;
          boolean b3 = dist(aX,aY,b.loc.x+65 ,b.loc.y-50 ) < 200/2;
          boolean b4 = dist(aX,aY,b.loc.x-113,b.loc.y+71 ) < 60 /2;
          boolean b5 = dist(aX,aY,b.loc.x-55 ,b.loc.y+85 ) < 77 /2;
          boolean b6 = dist(aX,aY,b.loc.x+9  ,b.loc.y+108) < 80 /2;
          boolean b7 = dist(aX,aY,b.loc.x+65 ,b.loc.y+50 ) < 200/2;

          if (b0 || b1 || b2 || b3 || b4 || b5 || b6 || b7) {
            hitBossPart = ARM;
            return true;
          }
        }

        // ellipse(b.loc.x+50,b.loc.y,230,230);
        //
        // ellipse(b.loc.x-113,b.loc.y-71,  60, 60 );
        // ellipse(b.loc.x-55 ,b.loc.y-85,  77, 77 );
        // ellipse(b.loc.x+9  ,b.loc.y-108, 80, 80 );
        // ellipse(b.loc.x+65 ,b.loc.y-50,  200,200);

      } else if (fNum == 4) {

        if(dist(aX,aY,b.loc.x+50,b.loc.y) < 230/2 && (aY < b.loc.y-laserGunPath || aY > b.loc.y+laserGunPath)) {
          hitBossPart = BODY;
          return true;
        } else if (dist(aX,aY,b.loc.x+80,b.loc.y) < 50/2) {
          hitBossPart = CRYSTAL;
          return true;
        } else {

          boolean b0 = inBounds(b,aX,aY,200,57,-23,-113);

          boolean b1 = inBounds(b,aX,aY,200,57,-23,113);

          boolean b2 = dist(aX,aY,b.loc.x+65 ,b.loc.y-50 ) < 200/2;
          boolean b3 = dist(aX,aY,b.loc.x+65 ,b.loc.y+50 ) < 200/2;

          if (b0 || b1 || b2 || b3) {
            hitBossPart = ARM;
            return true;
          }
        }


        // ellipse(b.loc.x+50,b.loc.y,230,230);
        //
        // rect(   b.loc.x-23 ,b.loc.y-113, 200,57 );
        // ellipse(b.loc.x+65 ,b.loc.y-50 , 200,200);

      } else if (fNum == 5) {


        if(dist(aX,aY,b.loc.x+50,b.loc.y) < 230/2 && (aY < b.loc.y-laserGunPath || aY > b.loc.y+laserGunPath)) {
          hitBossPart = BODY;
          return true;
        } else if (dist(aX,aY,b.loc.x+80,b.loc.y) < 50/2) {
          hitBossPart = CRYSTAL;
          return true;
        } else {
          boolean b0 = dist(aX,aY,b.loc.x-74 ,b.loc.y-185) < 70 /2;
          boolean b1 = dist(aX,aY,b.loc.x-21 ,b.loc.y-158) < 70 /2;
          boolean b2 = dist(aX,aY,b.loc.x+34 ,b.loc.y-132) < 70 /2;
          boolean b3 = dist(aX,aY,b.loc.x+96 ,b.loc.y-46 ) < 159/2;
          boolean b4 = dist(aX,aY,b.loc.x-74 ,b.loc.y+185) < 70 /2;
          boolean b5 = dist(aX,aY,b.loc.x-21 ,b.loc.y+158) < 70 /2;
          boolean b6 = dist(aX,aY,b.loc.x+34 ,b.loc.y+132) < 70 /2;
          boolean b7 = dist(aX,aY,b.loc.x+96 ,b.loc.y+46 ) < 159/2;

          if (b0 || b1 || b2 || b3 || b4 || b5 || b6 || b7) {
            hitBossPart = ARM;
            return true;
          }
        }

        // ellipse(b.loc.x+50,b.loc.y,230,230);
        //
        // ellipse(b.loc.x-74 ,b.loc.y-185, 70, 70 );
        // ellipse(b.loc.x-21 ,b.loc.y-158, 70, 70 );
        // ellipse(b.loc.x+34 ,b.loc.y-132, 70, 70 );
        // ellipse(b.loc.x+96 ,b.loc.y-46,  159,159);

      } else if (fNum == 6) {

        if(dist(aX,aY,b.loc.x+50,b.loc.y) < 230/2 && (aY < b.loc.y-laserGunPath || aY > b.loc.y+laserGunPath)) {
          hitBossPart = BODY;
          return true;
        } else if (dist(aX,aY,b.loc.x+80,b.loc.y) < 50/2) {
          hitBossPart = CRYSTAL;
          return true;
        } else {
          boolean b0 = dist(aX,aY,b.loc.x-33 ,b.loc.y-215) < 70 /2;
          boolean b1 = dist(aX,aY,b.loc.x+6  ,b.loc.y-176) < 70 /2;
          boolean b2 = dist(aX,aY,b.loc.x+49 ,b.loc.y-140) < 70 /2;
          boolean b3 = dist(aX,aY,b.loc.x+96 ,b.loc.y-46 ) < 159/2;
          boolean b4 = dist(aX,aY,b.loc.x-33 ,b.loc.y+215) < 70 /2;
          boolean b5 = dist(aX,aY,b.loc.x+6  ,b.loc.y+176) < 70 /2;
          boolean b6 = dist(aX,aY,b.loc.x+49 ,b.loc.y+140) < 70 /2;
          boolean b7 = dist(aX,aY,b.loc.x+96 ,b.loc.y+46 ) < 159/2;

          if (b0 || b1 || b2 || b3 || b4 || b5 || b6 || b7) {
            hitBossPart = ARM;
            return true;
          }
        }

        // ellipse(b.loc.x+50,b.loc.y,230,230);
        //
        // ellipse(b.loc.x-33 ,b.loc.y-215, 70, 70 );
        // ellipse(b.loc.x+6  ,b.loc.y-176, 70, 70 );
        // ellipse(b.loc.x+49 ,b.loc.y-140, 70, 70 );
        // ellipse(b.loc.x+96 ,b.loc.y-46,  159,159);

      } else if (fNum == 7) {

        if(dist(aX,aY,b.loc.x+50,b.loc.y) < 230/2 && (aY < b.loc.y-laserGunPath || aY > b.loc.y+laserGunPath)) {
          hitBossPart = BODY;
          return true;
        } else if (dist(aX,aY,b.loc.x+80,b.loc.y) < 50/2) {
          hitBossPart = CRYSTAL;
          return true;
        } else {
          boolean b0 = dist(aX,aY,b.loc.x+21 ,b.loc.y-292) < 70 /2;
          boolean b1 = dist(aX,aY,b.loc.x+42 ,b.loc.y-240) < 70 /2;
          boolean b2 = dist(aX,aY,b.loc.x+70 ,b.loc.y-190) < 70 /2;
          boolean b3 = dist(aX,aY,b.loc.x+93 ,b.loc.y-140) < 70 /2;
          boolean b4 = dist(aX,aY,b.loc.x+108,b.loc.y-57 ) < 118/2;
          boolean b5 = dist(aX,aY,b.loc.x+21 ,b.loc.y+292) < 70 /2;
          boolean b6 = dist(aX,aY,b.loc.x+42 ,b.loc.y+240) < 70 /2;
          boolean b7 = dist(aX,aY,b.loc.x+70 ,b.loc.y+190) < 70 /2;
          boolean b8 = dist(aX,aY,b.loc.x+93 ,b.loc.y+140) < 70 /2;
          boolean b9 = dist(aX,aY,b.loc.x+108,b.loc.y+57 ) < 118/2;

          if (b0 || b1 || b2 || b3 || b4 || b5 || b6 || b7 || b8 || b9) {
            hitBossPart = ARM;
            return true;
          }
        }

        // ellipse(b.loc.x+50,b.loc.y,230,230);
        //
        // ellipse(b.loc.x+21 ,b.loc.y-292, 70, 70 );
        // ellipse(b.loc.x+42 ,b.loc.y-240, 70, 70 );
        // ellipse(b.loc.x+70 ,b.loc.y-190, 70, 70 );
        // ellipse(b.loc.x+93 ,b.loc.y-140, 70, 70 );
        // ellipse(b.loc.x+108 ,b.loc.y-57, 118,118);

      } else if (fNum == 9) {

        if(dist(aX,aY,b.loc.x+50,b.loc.y) < 230/2 && (aY < b.loc.y-laserGunPath || aY > b.loc.y+laserGunPath)) {
          hitBossPart = BODY;
          return true;
        } else if (dist(aX,aY,b.loc.x+80,b.loc.y) < 50/2) {
          hitBossPart = CRYSTAL;
          return true;
        } else {
          boolean b0 = dist(aX,aY,b.loc.x-67 ,b.loc.y-81 ) < 70 /2;
          boolean b1 = dist(aX,aY,b.loc.x-47 ,b.loc.y-128) < 70 /2;
          boolean b2 = dist(aX,aY,b.loc.x+2  ,b.loc.y-158) < 70 /2;
          boolean b3 = dist(aX,aY,b.loc.x+62 ,b.loc.y-163) < 70 /2;
          boolean b4 = dist(aX,aY,b.loc.x+110,b.loc.y-76 ) < 154/2;
          boolean b5 = dist(aX,aY,b.loc.x-67 ,b.loc.y+81 ) < 70 /2;
          boolean b6 = dist(aX,aY,b.loc.x-47 ,b.loc.y+128) < 70 /2;
          boolean b7 = dist(aX,aY,b.loc.x+2  ,b.loc.y+158) < 70 /2;
          boolean b8 = dist(aX,aY,b.loc.x+62 ,b.loc.y+163) < 70 /2;
          boolean b9 = dist(aX,aY,b.loc.x+110,b.loc.y+76 ) < 154/2;

          if (b0 || b1 || b2 || b3 || b4 || b5 || b6 || b7 || b8 || b9) {
            hitBossPart = ARM;
            return true;
          }
        }

        // ellipse(b.loc.x+50,b.loc.y,230,230);
        //
        // ellipse(b.loc.x-67 ,b.loc.y-81 , 70, 70 );
        // ellipse(b.loc.x-47 ,b.loc.y-128, 70, 70 );
        // ellipse(b.loc.x+2  ,b.loc.y-158, 70, 70 );
        // ellipse(b.loc.x+62 ,b.loc.y-163, 70, 70 );
        // ellipse(b.loc.x+110,b.loc.y-76 , 154,154);

      } else if (fNum == 10) {

        if(dist(aX,aY,b.loc.x+50,b.loc.y) < 230/2 && (aY < b.loc.y-laserGunPath || aY > b.loc.y+laserGunPath)) {
          hitBossPart = BODY;
          return true;
        } else if (dist(aX,aY,b.loc.x+80,b.loc.y) < 50/2) {
          hitBossPart = CRYSTAL;
          return true;
        } else {
          boolean b0 = dist(aX,aY,b.loc.x-40 ,b.loc.y-167) < 70/2;
          boolean b1 = dist(aX,aY,b.loc.x+15 ,b.loc.y-200) < 70/2;
          boolean b2 = dist(aX,aY,b.loc.x+75 ,b.loc.y-198) < 70/2;
          boolean b3 = dist(aX,aY,b.loc.x+129,b.loc.y-168) < 70/2;
          boolean b4 = dist(aX,aY,b.loc.x+156,b.loc.y-108) < 70/2;
          boolean b5 = dist(aX,aY,b.loc.x+144,b.loc.y-47 ) < 70/2;
          boolean b6 = dist(aX,aY,b.loc.x-40 ,b.loc.y+167) < 70/2;
          boolean b7 = dist(aX,aY,b.loc.x+15 ,b.loc.y+200) < 70/2;
          boolean b8 = dist(aX,aY,b.loc.x+75 ,b.loc.y+198) < 70/2;
          boolean b9 = dist(aX,aY,b.loc.x+129,b.loc.y+168) < 70/2;
          boolean b10= dist(aX,aY,b.loc.x+156,b.loc.y+108) < 70/2;
          boolean b11= dist(aX,aY,b.loc.x+144,b.loc.y+47 ) < 70/2;

          if (b0 || b1 || b2 || b3 || b4 || b5 || b6 || b7 || b8 || b9 || b10 || b11) {
            hitBossPart = ARM;
            return true;
          }
        }


        // ellipse(b.loc.x+50,b.loc.y,230,230);
        //
        // ellipse(b.loc.x-40 ,b.loc.y-167, 70, 70 );
        // ellipse(b.loc.x+15 ,b.loc.y-200, 70, 70 );
        // ellipse(b.loc.x+75 ,b.loc.y-198, 70, 70 );
        // ellipse(b.loc.x+129,b.loc.y-168, 70, 70 );
        // ellipse(b.loc.x+156,b.loc.y-108, 70, 70 );
        // ellipse(b.loc.x+144,b.loc.y-47 , 70, 70 );

      } else if (fNum == 11) {

        if(dist(aX,aY,b.loc.x+50,b.loc.y) < 230/2 && (aY < b.loc.y-laserGunPath || aY > b.loc.y+laserGunPath)) {
          hitBossPart = BODY;
          return true;
        } else if (dist(aX,aY,b.loc.x+80,b.loc.y) < 50/2) {
          hitBossPart = CRYSTAL;
          return true;
        } else {
          boolean b0 = dist(aX,aY,b.loc.x-8  ,b.loc.y-225) < 70/2;
          boolean b1 = dist(aX,aY,b.loc.x+45 ,b.loc.y-240) < 70/2;
          boolean b2 = dist(aX,aY,b.loc.x+110,b.loc.y-226) < 70/2;
          boolean b3 = dist(aX,aY,b.loc.x+147,b.loc.y-173) < 70/2;
          boolean b4 = dist(aX,aY,b.loc.x+156,b.loc.y-108) < 70/2;
          boolean b5 = dist(aX,aY,b.loc.x+144,b.loc.y-47 ) < 70/2;
          boolean b6 = dist(aX,aY,b.loc.x-8  ,b.loc.y+225) < 70/2;
          boolean b7 = dist(aX,aY,b.loc.x+45 ,b.loc.y+240) < 70/2;
          boolean b8 = dist(aX,aY,b.loc.x+110,b.loc.y+226) < 70/2;
          boolean b9 = dist(aX,aY,b.loc.x+147,b.loc.y+173) < 70/2;
          boolean b10= dist(aX,aY,b.loc.x+156,b.loc.y+108) < 70/2;
          boolean b11= dist(aX,aY,b.loc.x+144,b.loc.y+47 ) < 70/2;

          if (b0 || b1 || b2 || b3 || b4 || b5 || b6 || b7 || b8 || b9 || b10 || b11) {
            hitBossPart = ARM;
            return true;
          }
        }

        // ellipse(b.loc.x+50,b.loc.y,230,230);
        //
        // ellipse(b.loc.x-8  ,b.loc.y-225, 70, 70 );
        // ellipse(b.loc.x+45 ,b.loc.y-240, 70, 70 );
        // ellipse(b.loc.x+110,b.loc.y-226, 70, 70 );
        // ellipse(b.loc.x+147,b.loc.y-173, 70, 70 );
        // ellipse(b.loc.x+156,b.loc.y-108, 70, 70 );
        // ellipse(b.loc.x+144,b.loc.y-47 , 70, 70 );

      } else if (fNum == 12) {

        if(dist(aX,aY,b.loc.x+50,b.loc.y) < 230/2 && (aY < b.loc.y-laserGunPath || aY > b.loc.y+laserGunPath)) {
          hitBossPart = BODY;
          return true;
        } else if (dist(aX,aY,b.loc.x+80,b.loc.y) < 50/2) {
          hitBossPart = CRYSTAL;
          return true;
        } else {

          boolean b0 = inBounds(b,aX,aY,60,144,-153,-130);

          boolean b1 = inBounds(b,aX,aY,60,144,-153,130);

          boolean b2 = dist(aX,aY,b.loc.x+36 ,b.loc.y-274) < 70 /2;
          boolean b3 = dist(aX,aY,b.loc.x+92 ,b.loc.y-268) < 70 /2;
          boolean b4 = dist(aX,aY,b.loc.x+137,b.loc.y-231) < 70 /2;
          boolean b5 = dist(aX,aY,b.loc.x+144,b.loc.y-47 ) < 70 /2;
          boolean b6 = dist(aX,aY,b.loc.x+36 ,b.loc.y+274) < 70 /2;
          boolean b7 = dist(aX,aY,b.loc.x+92 ,b.loc.y+268) < 70 /2;
          boolean b8 = dist(aX,aY,b.loc.x+137,b.loc.y+231) < 70 /2;
          boolean b9 = dist(aX,aY,b.loc.x+144,b.loc.y+47 ) < 70 /2;


          if (b0 || b1 || b2 || b3 || b4 || b5 || b6 || b7 || b8 || b9) {
            hitBossPart = ARM;
            return true;
          }
        }

        // ellipse(b.loc.x+50,b.loc.y,230,230);
        //
        // ellipse(b.loc.x+36 ,b.loc.y-274, 70, 70 );
        // ellipse(b.loc.x+92 ,b.loc.y-268, 70, 70 );
        // ellipse(b.loc.x+137,b.loc.y-231, 70, 70 );
        // ellipse(b.loc.x+144,b.loc.y-47 , 70, 70 );
        // rect(   b.loc.x+153,b.loc.y-130, 60, 144);

      } else if (fNum == 13) {

        if(dist(aX,aY,b.loc.x+50,b.loc.y) < 230/2 && (aY < b.loc.y-laserGunPath || aY > b.loc.y+laserGunPath)) {
          hitBossPart = BODY;
          return true;
        } else if (dist(aX,aY,b.loc.x+80,b.loc.y) < 50/2) {
          hitBossPart = CRYSTAL;
          return true;
        } else {

          boolean b0 = inBounds(b,aX,aY,60,191,-153,-153);

          boolean b1 = inBounds(b,aX,aY,60,191,-153,153);


          boolean b2 = dist(aX,aY,b.loc.x+82 ,b.loc.y-309) < 70 /2;
          boolean b3 = dist(aX,aY,b.loc.x+130,b.loc.y-273) < 70 /2;
          boolean b4 = dist(aX,aY,b.loc.x+144,b.loc.y-47 ) < 70 /2;
          boolean b5 = dist(aX,aY,b.loc.x+82 ,b.loc.y+309) < 70 /2;
          boolean b6 = dist(aX,aY,b.loc.x+130,b.loc.y+273) < 70 /2;
          boolean b7 = dist(aX,aY,b.loc.x+144,b.loc.y+47 ) < 70 /2;


          if (b0 || b1 || b2 || b3 || b4 || b5 || b6 || b7) {
            hitBossPart = ARM;
            return true;
          }
        }

        // ellipse(b.loc.x+50,b.loc.y,230,230);
        //
        // ellipse(b.loc.x+82 ,b.loc.y-309, 70, 70 );
        // ellipse(b.loc.x+130,b.loc.y-273, 70, 70 );
        // ellipse(b.loc.x+144,b.loc.y-47 , 70, 70 );
        // rect(   b.loc.x+153,b.loc.y-153, 60, 191);

      } else if (fNum == 14) {

        if(dist(aX,aY,b.loc.x+50,b.loc.y) < 230/2 && (aY < b.loc.y-laserGunPath || aY > b.loc.y+laserGunPath)) {
          hitBossPart = BODY;
          return true;
        } else if (dist(aX,aY,b.loc.x+80,b.loc.y) < 50/2) {
          hitBossPart = CRYSTAL;
          return true;
        } else {

          boolean b0 = inBounds(b,aX,aY,60,237,-153,-178);

          boolean b1 = inBounds(b,aX,aY,60,237,-153,178);

          boolean b2 = dist(aX,aY,b.loc.x+123,b.loc.y-325) < 70 /2;
          boolean b3 = dist(aX,aY,b.loc.x+144,b.loc.y-47 ) < 70 /2;
          boolean b4 = dist(aX,aY,b.loc.x+123,b.loc.y+325) < 70 /2;
          boolean b5 = dist(aX,aY,b.loc.x+144,b.loc.y+47 ) < 70 /2;

          if (b0 || b1 || b2 || b3 || b4 || b5) {
            hitBossPart = ARM;
            return true;
          }
        }

        // ellipse(b.loc.x+50,b.loc.y,230,230);
        //
        // ellipse(b.loc.x+123,b.loc.y-325, 70, 70 );
        // ellipse(b.loc.x+144,b.loc.y-47 , 70, 70 );
        // rect(   b.loc.x+153,b.loc.y-178, 60, 237);

      } else if (fNum == 15) {

        if(dist(aX,aY,b.loc.x+50,b.loc.y) < 230/2 && (aY < b.loc.y-laserGunPath || aY > b.loc.y+laserGunPath)) {
          hitBossPart = BODY;
          return true;
        } else if (dist(aX,aY,b.loc.x+80,b.loc.y) < 50/2) {
          hitBossPart = CRYSTAL;
          return true;
        } else {

          boolean b0 = inBounds(b,aX,aY,60,253,-153,-178);

          boolean b1 = inBounds(b,aX,aY,60,253,-153,178);

          boolean b2 = dist(aX,aY,b.loc.x+145,b.loc.y-339) < 58 /2;
          boolean b3 = dist(aX,aY,b.loc.x+144,b.loc.y-47 ) < 70 /2;
          boolean b4 = dist(aX,aY,b.loc.x+145,b.loc.y+339) < 58 /2;
          boolean b5 = dist(aX,aY,b.loc.x+144,b.loc.y+47 ) < 70 /2;

          if (b0 || b1 || b2 || b3 || b4 || b5) {
            hitBossPart = ARM;
            return true;
          }
        }

        // ellipse(b.loc.x+50,b.loc.y,230,230);
        //
        // ellipse(b.loc.x+145,b.loc.y-339, 58, 58 );
        // ellipse(b.loc.x+144,b.loc.y-47 , 70, 70 );
        // rect(   b.loc.x+153,b.loc.y-178, 60, 253);

      } else if (fNum == 16) {

        if(dist(aX,aY,b.loc.x+50,b.loc.y) < 230/2 && (aY < b.loc.y-laserGunPath || aY > b.loc.y+laserGunPath)) {
          hitBossPart = BODY;
          return true;
        } else if (dist(aX,aY,b.loc.x+80,b.loc.y) < 50/2) {
          hitBossPart = CRYSTAL;
          return true;
        } else {

          boolean b0 = inBounds(b,aX,aY,60,293,-153,-205);

          boolean b1 = inBounds(b,aX,aY,60,293,-153,205);

          boolean b2 = dist(aX,aY,b.loc.x+144,b.loc.y-47 ) < 70 /2;
          boolean b3 = dist(aX,aY,b.loc.x+144,b.loc.y+47 ) < 70 /2;

          if (b0 || b1 || b2 || b3) {
            hitBossPart = ARM;
            return true;
          }
        }

        // ellipse(b.loc.x+50,b.loc.y,230,230);
        //
        // ellipse(b.loc.x+144,b.loc.y-47 , 70, 70 );
        // rect(   b.loc.x+153,b.loc.y-205, 60, 293);

      }

      return false;
    } else {

      float bWidthMin  = actor.loc.x-actor.cWidth/2;
      float bWidthMax  = actor.loc.x+actor.cWidth/2;
      float bHeightMin = actor.loc.y-actor.cHeight/2;
      float bHeightMax = actor.loc.y+actor.cHeight/2;

      return aX > bWidthMin && aX < bWidthMax && aY > bHeightMin && aY < bHeightMax;
    }

  }

  public boolean inBounds(Boss b_, float aX_, float aY_, float tempCWidth_,
                   float tempCHeight_, float offsetX_, float offsetY_) {

    float bWidthMin,bWidthMax,bHeightMin,bHeightMax;

    offsetX_ = -offsetX_;

    bWidthMin  = b_.loc.x+offsetX_-tempCWidth_/2;
    bWidthMax  = b_.loc.x+offsetX_+tempCWidth_/2;
    bHeightMin = b_.loc.y+offsetY_-tempCHeight_/2;
    bHeightMax = b_.loc.y+offsetY_+tempCHeight_/2;

    // rect(b_.loc.x+offsetX_,b_.loc.y+offsetY_,tempCWidth_,tempCHeight_);

    return aX_ > bWidthMin && aX_ < bWidthMax && aY_ > bHeightMin && aY_ < bHeightMax;
  }


}
class Scores{
  int score;
  int shot;
  int shotHit;

  private int lastKillScore;
  private int lastShotUsed;

  private int streak;

  PFont pixelFont;
  PImage hud;

  Scores() {
    pixelFont = loadFont("PressStart2P-48.vlw");
    hud = loadImage("Sprites/HUD.png");
  }

  public void showScores() {

    image(hud,width/2,height-hud.height/2);

    textAlign(CENTER);
    fill(0xffffffff);
    textFont(pixelFont, 15);
    text("SCORE:"+nf(score,10), width/2, height-35);

    fill(0xff4c0000);
    rect(width/2-400+200/2,height-32,200,25);
    if (player.health > 0) {
      float mappedValue = map(player.health,0,100,0,200);
      fill(0xffed2525);
      rect(width/2-400+mappedValue/2,height-32,mappedValue,25);
    }
    fill(0xffffffff);
    text("Health",width/2-465,height-25);


    if (boss != null && boss.active) {
      fill(0xff4c0000);
      rect(width/2+400-200/2,height-32,200,25);
      float mappedValue = map(boss.health,0,50,0,200);
      fill(0xffed2525);
      rect(width/2+400-mappedValue/2,height-32,mappedValue,25);
      fill(0xffffffff);
      text("Boss",width/2+465,height-25);
    }

    streakCalc();
    fill(0xffffffff);
    if (shotHit == 0)
      text("HIT/SHOT:NA", width/2, height-10);
    else
      text("HIT/SHOT:"+(int)(((shotHit*1.0f)/(shot*1.0f))*100)+"%", width/2, height-10);

    if (!gameOver && streak > 2) {
      textFont(scores.getFont(), sin(millis()*.005f)*3+50);
      textAlign(CENTER);
      fill(0xfff2dcb8);

      text("STREAK OF "+streak,width/2,height-50);

    }
  }

  public PFont getFont() {
    return pixelFont;
  }

  public void streakCalc() {
    if (lastKillScore < score)
      streak++;
    else
      streak = 0;
    lastKillScore = score;
    lastShotUsed = shotHit;
  }

  public int getStreak() {
    return streak;
  }
}
class TextField extends GameObject {

  int life;
  int birthMil;

  String text;

  PFont font;

  PImage img;

  TextField(String text_, int life_) {
    super();
    font = scores.getFont();
    img = loadImage("Sprites/HUD.png");
    loc = new PVector(width/2,-img.height/2);
    life = life_;
    des = loc.copy();
    smoothFactor = .1f;
    text = text_;
    birthMil = millis();
  }

  public @Override
  void update() {

    des.y = img.height/2;

    if (millis() - birthMil < life) {
      smoothMove();
      simplePhysicsCal();
    } else {
      acc.y = -0.5f;
      simplePhysicsCal();
    }

    if (loc.y < -img.height) {
      textFields.remove(this);
    }



    drawImage();
  }

  public void drawImage() {
    pushMatrix();
    translate(loc.x,loc.y);
    pushMatrix();
    scale(1,-1);

    image(img,0,0);
    popMatrix();
    textAlign(CENTER);
    fill(0xffffffff);
    textFont(font, 20);
    text(text,0,7.5f);

    popMatrix();

  }
}
  public void settings() {  size(1280, 720, P2D ); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "assignment3" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
