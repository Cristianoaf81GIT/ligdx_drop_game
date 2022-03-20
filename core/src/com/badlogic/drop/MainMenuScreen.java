package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Texture;



// https://github.com/libgdx/libgdx.github.io/blob/master/assets/downloads/tutorials/extended-game-java/MainMenuScreen.java
// https://libgdx.com/wiki/start/simple-game-extended

public class MainMenuScreen implements Screen {
  final Drop game;

  OrthographicCamera camera;
  Texture background;

  public MainMenuScreen(final Drop game) {
    this.game = game;
    camera = new OrthographicCamera();
    camera.setToOrtho(false,800,480);
    background = new Texture(Gdx.files.internal("background.jpeg"));
  }

  @Override()
  public void render(float delta) {
   ScreenUtils.clear(0,0,0.2f,1);
   game.batch.setProjectionMatrix(camera.combined);

   game.batch.begin();
   game.batch.draw(background,0 ,0 , 800, 480);
   game.font.draw(game.batch, "Bem vindo ao hoje vai chover!", 300,300);
   game.font.draw(game.batch,"Clique em qualquer lugar para começar;",265,280);
   game.batch.end();

   if (Gdx.input.isTouched()) {
    game.setScreen(new GameScreen(game));
    dispose();
   }

  }


  @Override()
  public void resize(int width, int height) {}

  @Override()
  public void show() {}

  @Override()
  public void hide() {}

  @Override()
  public void pause() {}

  @Override()
  public void resume() {}

  @Override()
  public void dispose() {}

}
