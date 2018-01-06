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
    smoothFactor = .1;
    text = text_;
    birthMil = millis();
  }

  @Override
  void update() {

    des.y = img.height/2;

    if (millis() - birthMil < life) {
      smoothMove();
      simplePhysicsCal();
    } else {
      acc.y = -0.5;
      simplePhysicsCal();
    }

    if (loc.y < -img.height) {
      textFields.remove(this);
    }



    drawImage();
  }

  void drawImage() {
    pushMatrix();
    translate(loc.x,loc.y);
    pushMatrix();
    scale(1,-1);

    image(img,0,0);
    popMatrix();
    textAlign(CENTER);
    fill(#ffffff);
    textFont(font, 20);
    text(text,0,7.5);

    popMatrix();

  }
}
