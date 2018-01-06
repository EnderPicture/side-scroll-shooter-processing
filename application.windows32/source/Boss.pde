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

    smoothFactor = 0.1;

    projectiles = new ArrayList<Projectile>();
  }

  @Override
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

        des.x = -camera.loc.x+width*0.8;

        smoothMove();
        simplePhysicsCal();

        stateController();

        actionController();
        prjectileController();
        laserController();
      }

    } else {
      loc.x = -camera.loc.x+width*1.2;
    }

    drawImage();
  }

  void prjectileController() {
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

  void actionController() {
    if (millis() - lastMilShoot > shootDelay &&
        animationDone() && state == SHOOT) {

      // do shoot code

      for (int c = -15; c <= 15; c+=2) {
        int tempC = c;
        int delta = 100;
        PVector v = new PVector(-30,0);
        if (c > 0) {
          v.rotate((map(c,15,0,0,15)) * 0.006);
          tempC = c+delta+c*15;
        } else {
          v.rotate((map(c,-15,0,0,15))*-0.006);
          tempC = c-delta+c*15;
        }

        projectiles.add(new Projectile(loc.x+150,loc.y+tempC,v.x,v.y,projectilePower,"Sprites/Projectiles/BossProjectile0000.png"));
      }
      lastMilShoot = millis();
    } else {
      laser = animationDone() && state == LASER;
    }

  }

  void stateController() {
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

  void animationController() {
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

  boolean animationDone() {
    return ((state == SHIELD && (frameC == 0 || frameC == 8)) ||
           (state == LASER && frameC == 7) ||
           (state == SHOOT && frameC == 16));
  }

  void laserController() {
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

  @Override
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