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

  @Override
  void update() {
    checkWalls();


    if (rightWall || leftWall || topWall || bottomWall) {
      items.remove(this);
      return;
    }

    drawImage();
  }

  @Override
  void drawImage() {
    pushMatrix();
    translate(loc.x,loc.y+sin((millis()+randomNumber)*0.005)*5);

    if (TYPE == FULLHEALTH) {
      fill(#ed2525);
      stroke(#ffffff);
      strokeWeight(3);
      rect(0,0,cWidth,cHeight);
      noStroke();
      textAlign(CENTER);
      fill(#ffffff);
      textFont(font, 20);
      text("+", 0, cHeight*.35);
    } else if (TYPE == HEALTH20) {
      fill(#e8ad2e);
      stroke(#ffffff);
      strokeWeight(3);
      rect(0,0,cWidth,cHeight);
      noStroke();
      textAlign(CENTER);
      fill(#ffffff);
      textFont(font, 20);
      text("+", 0, cHeight*.35);
    } else if (TYPE == SHOOTSPEED) {
      fill(#2fd9ef);
      stroke(#ffffff);
      strokeWeight(3);
      rect(0,0,cWidth,cHeight);
      noStroke();
      textAlign(CENTER);
      fill(#ffffff);
      textFont(font, 20);
      text("S", 1.5, cHeight*.35);
    } else if (TYPE == SHOOTSTRAIGHT) {
      fill(#2ee850);
      stroke(#ffffff);
      strokeWeight(3);
      rect(0,0,cWidth,cHeight);
      noStroke();
      textAlign(CENTER);
      fill(#ffffff);
      textFont(font, 20);
      text("A", 1.25, cHeight*.35);
    }

    popMatrix();
  }

}