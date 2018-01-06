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

  @Override
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

  void damageIfHit() {
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

  void animate(int min, int max, int aniSpeed, boolean loop, boolean reverse) {

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

  @Override
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