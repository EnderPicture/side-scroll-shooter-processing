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

  void update() {
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
      else if (!loop && frameC > max) {
        frameC = max;

        // remove after animation is done
        particles.remove(this);
        return;
      }
    }

    img = images.get(frameC);
  }

  void drawImage() {
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