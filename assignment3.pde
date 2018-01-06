boolean upHold, leftHold, downHold, rightHold, shiftHold, zHold, xHold, cHold = false;

Cam2D camera;

Player player;

Boss boss;

Scores scores;

boolean gameOver;

float gravity = 0.05;

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


void setup() {
  size(1280, 720, P2D );
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

void draw() {
  // background(255); no need for background
  // println(frameRate);
  // update delta millisecond
  deltaTime = (millis() - oldMil)*0.06;
  oldMil = millis();

  // spawn enemies
  spawnController();

  pushMatrix();

  camera.des = new PVector(width/4-player.loc.x,height/2);
  camera.update();

  // draw infinite background
  int min,max;

  min = (int)((-width/2-camera.loc.x)/backGround.width)*backGround.width;
  max = (int)(width*1.5-camera.loc.x);
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
  max = (int)(width*1.5-camera.loc.x);
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

void spawnController() {

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

          e.vel.x = -2.5;
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

          e.vel.x = -2.5;
          enemies.add(e);
      }
    }


    oldCamPosSpawn = camera.loc.x;
  }
}

void keyPressed() {
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

void keyReleased() {
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
