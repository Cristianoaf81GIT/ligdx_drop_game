package com.badlogic.drop;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.audio.Music; 
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Drop extends ApplicationAdapter {
 private Texture dropImage;
 private Texture bucketImage;
 private Sound dropSound;
 private Music rainMusic;
 private OrthographicCamera camera;
 private SpriteBatch batch; 
 private Rectangle bucket;
 private Array<Rectangle> rainDrops;
 private long lastDropTime;
	
	@Override
	public void create () {
    dropImage = new Texture(Gdx.files.internal("drop.png"));
    bucketImage = new Texture(Gdx.files.internal("bucket.png"));
    

    dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
    rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
    
    camera = new OrthographicCamera();
    camera.setToOrtho(false,800,480);

    batch = new SpriteBatch();

    bucket = new Rectangle();
    bucket.x = 800 / 2 - 64 / 2;
    bucket.y = 20;
    bucket.width = 64;
    bucket.height = 64;
    

    rainDrops = new Array<Rectangle>();
    spawnRainDrop();

    rainMusic.setLooping(true);
    rainMusic.play();
	}


  private void spawnRainDrop() {
    Rectangle raindrop = new Rectangle();
    raindrop.x = MathUtils.random(0, 800-64);
    raindrop.y = 480 ;
    raindrop.width = 64;
    raindrop.height = 64;
    rainDrops.add(raindrop);
    lastDropTime = TimeUtils.nanoTime();
  }

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0.2f, 1);
    camera.update();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    batch.draw(bucketImage, bucket.x, bucket.y );
    /*batch.draw(
         new TextureRegion(bucketImage),
          bucket.x,
          bucket.y,
          0f,
          0f,
          bucket.width,
          bucket.height,
          1f,
          1f,
          270f,
          true                   
        );*/
    for (Rectangle raindrop: rainDrops) {
      batch.draw(dropImage, raindrop.x, raindrop.y);
      /*batch.draw(
          new TextureRegion(dropImage),
          raindrop.x,
          raindrop.y,
          0f,
          0f,
          raindrop.width,
          raindrop.height,
          1f,
          1f,
          -90f,
          true
      );*/
    }
    batch.end();

    if (Gdx.input.isTouched()) {
      Vector3 touchPos = new Vector3();
      touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
      camera.unproject(touchPos);
      bucket.x = touchPos.x - 64/2;
    }
   
    if(Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) 
      bucket.x -= 200 * Gdx.graphics.getDeltaTime();
   
    if(Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) 
      bucket.x += 200 * Gdx.graphics.getDeltaTime();

    if (bucket.x < 0) bucket.x = 0;
    if (bucket.x > 800 - 64) bucket.x = 800 - 64;

    if(TimeUtils.nanoTime() - lastDropTime > 1000000000) 
      spawnRainDrop();

    for (Iterator<Rectangle> iter = rainDrops.iterator(); iter.hasNext();) {
      Rectangle raindrop = iter.next();
      raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
      // raindrop.y -= (200 - raindrop.y) * Gdx.graphics.getDeltaTime();
      if (raindrop.y + 64 < 0 ) iter.remove();
      if (raindrop.overlaps(bucket)) {
        dropSound.play();
        iter.remove();
      }
    }

	}
	
	@Override
	public void dispose () {
    rainMusic.dispose();
    dropSound.dispose();
    dropImage.dispose();
    bucketImage.dispose();
    batch.dispose();
	}
}
