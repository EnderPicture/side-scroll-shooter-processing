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
  @Override
  void update() {
    simplePhysicsCal();
    //offScreenB();
  }

  /**
   * Should be over written
   */
  void drawImage() {
    pushMatrix();
    translate(loc.x,loc.y);

    ellipse(0,0,10,10);

    popMatrix();
  }

  /**
   * Offscreen Switch
   */
  void offScreenS(float screenExtention) {
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
  void offScreenB() {

    if ((loc.x < 0 + img.width/2 && vel.x < 0) || (loc.x > width - img.width/2 && vel.x >= 0))
      vel.x *= -1;

    if ((loc.y < 0 + img.height/2 && vel.y < 0) || (loc.y > height - img.height/2 && vel.y >= 0))
      vel.y *= -1;

  }

  boolean hitCharacter(Actor actor) {

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

  void decreaseHealth(float delta) {
    health -= delta;
  }

  void increaseHealth(float delta) {
    health += delta;
    if (health > maxHealth)
      health = maxHealth;
  }

  void checkWalls() {


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