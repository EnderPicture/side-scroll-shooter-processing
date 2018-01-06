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

  void simplePhysicsCal() {
    vel.add(acc.copy().mult(deltaTime));
    loc.add(vel.copy().mult(deltaTime));
  }

  void drawImage() {

    pushMatrix();
    translate(loc.x,loc.y);
    rotate(vel.heading());


    if(img != null)
      image(img, 0, 0);
    else
      ellipse(0,0,10,10);

    popMatrix();
  }

  void update() {
    simplePhysicsCal();
    drawImage();
    checkWalls();
  }

  void checkWalls() {
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

  boolean hit(Actor actor) {

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

  boolean inBounds(Boss b_, float aX_, float aY_, float tempCWidth_,
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