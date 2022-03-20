package com.badlogic.drop;


import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;


public class GameScreen implements Screen {
  final Drop game;

  Texture background;
  Texture dropImage;
  Texture bucketImage;
  Sound dropSound;
  Music rainMusic;
  OrthographicCamera camera;
  Rectangle bucket;
  Array<Rectangle> rainDrops;
  long lastDropTime;
  int dropsGathered;
  int lostDrops;

  public GameScreen(final Drop game) {
    this.game = game;

    // load game background
    background = new Texture(Gdx.files.internal("background.jpeg"));

    // load de images for droplet and bucket, 64x64 px
    dropImage = new Texture(Gdx.files.internal("drop.png"));
    bucketImage = new Texture(Gdx.files.internal("bucket.png"));

    // load the drop sound effect an rain music
    dropSound =  Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
    rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
    rainMusic.setLooping(true);
  
    // create game camera
    camera = new OrthographicCamera();
    camera.setToOrtho(false,800,480);

    // create rectangle
    bucket = new Rectangle();
    bucket.x = 800 / 2 - 64 / 2; // starts at center of screen
    bucket.y = 20; // 20 pixels at bottom of screen

    bucket.width = 64;
    bucket.height = 64;

    // create raindrops
    rainDrops = new Array<Rectangle>();
    spawRaindrop();    

  }

  private void spawRaindrop() {
    Rectangle raindrop = new Rectangle();
    raindrop.x = MathUtils.random(0, 800 - 64);
    raindrop.y = 480;
    raindrop.width = 64;
    raindrop.height = 64;
    rainDrops.add(raindrop);
    lastDropTime = TimeUtils.nanoTime();
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override 
  public void show() {
    // when game screen is on play music
    rainMusic.play();
  }

  @Override
  public void hide() {}

  @Override
  public void dispose(){
    dropImage.dispose();
    bucketImage.dispose();
    dropSound.dispose();
    rainMusic.dispose();
    background.dispose();
  }

  @Override
  public void render(float delta) {
    // clear screen with a dark color
    ScreenUtils.clear(0,0,0.2f,1);

    // camera must update its  matrices
    camera.update();

    // SpriteBatch must render at camera coordinate system
    game.batch.setProjectionMatrix(camera.combined);

    // begin a new batch and draw the bucket and all drops
    game.batch.begin();
    game.batch.draw(background, 0, 0, 800,480);
    game.font.draw(game.batch, "gotas coletadas: " + dropsGathered,5,480);
    game.font.draw(game.batch, "Gotas perdidas: " + lostDrops,670,480);
    game.batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);
    for (Rectangle raindrop : rainDrops) {
      game.batch.draw(dropImage, raindrop.x, raindrop.y);
    }
    game.batch.end();

    // process user input
    if (Gdx.input.isTouched()) {
      Vector3 touchPos = new Vector3();
      touchPos.set(Gdx.input.getX(),Gdx.input.getY(), 0);
      camera.unproject(touchPos);
      bucket.x = touchPos.x - 64 / 2;
    }

    if (Gdx.input.isKeyPressed(Keys.LEFT)) {
      bucket.x -= 350 * Gdx.graphics.getDeltaTime();
    }

    if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
      bucket.x += 350 * Gdx.graphics.getDeltaTime();
    }

    // ensure sure the bucket stays within the screen bounds
    if (bucket.x < 0) {
      bucket.x = 0;
    }

    if (bucket.x > 800 - 64) {
      bucket.x = 800 - 64;
    }
    
    // check if we need to create a new raindrop
    if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
      spawRaindrop();
    }

    // move raindrops
    Iterator<Rectangle> iter = rainDrops.iterator();
    while(iter.hasNext()) {
      Rectangle raindrop = iter.next();
      raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
      if (raindrop.y + 64 < 0) {
        iter.remove();
        lostDrops++;
      }

      if (raindrop.overlaps(bucket)) {
        dropsGathered++;
        dropSound.play();
        iter.remove();
      }
    }
  }

  @Override
  public void resize(int width, int height) {}

}
