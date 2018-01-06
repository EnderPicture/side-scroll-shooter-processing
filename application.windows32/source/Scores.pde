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

  void showScores() {

    image(hud,width/2,height-hud.height/2);

    textAlign(CENTER);
    fill(#ffffff);
    textFont(pixelFont, 15);
    text("SCORE:"+nf(score,10), width/2, height-35);

    fill(#4c0000);
    rect(width/2-400+200/2,height-32,200,25);
    if (player.health > 0) {
      float mappedValue = map(player.health,0,100,0,200);
      fill(#ed2525);
      rect(width/2-400+mappedValue/2,height-32,mappedValue,25);
    }
    fill(#ffffff);
    text("Health",width/2-465,height-25);


    if (boss != null && boss.active) {
      fill(#4c0000);
      rect(width/2+400-200/2,height-32,200,25);
      float mappedValue = map(boss.health,0,50,0,200);
      fill(#ed2525);
      rect(width/2+400-mappedValue/2,height-32,mappedValue,25);
      fill(#ffffff);
      text("Boss",width/2+465,height-25);
    }

    streakCalc();
    fill(#ffffff);
    if (shotHit == 0)
      text("HIT/SHOT:NA", width/2, height-10);
    else
      text("HIT/SHOT:"+(int)(((shotHit*1.0)/(shot*1.0))*100)+"%", width/2, height-10);

    if (!gameOver && streak > 2) {
      textFont(scores.getFont(), sin(millis()*.005)*3+50);
      textAlign(CENTER);
      fill(#f2dcb8);

      text("STREAK OF "+streak,width/2,height-50);

    }
  }

  PFont getFont() {
    return pixelFont;
  }

  void streakCalc() {
    if (lastKillScore < score)
      streak++;
    else
      streak = 0;
    lastKillScore = score;
    lastShotUsed = shotHit;
  }

  int getStreak() {
    return streak;
  }
}