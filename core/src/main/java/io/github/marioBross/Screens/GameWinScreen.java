package io.github.marioBross.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.marioBross.MarioBross;
import io.github.marioBross.Scenes.Hud;

public class GameWinScreen implements Screen {
    private Stage stage;
    private Viewport viewport;

    private Game game;

    private float stateTime = 0;
    private boolean finishTheGame = false;

    public GameWinScreen(Game game) {
        this.game = game;
        viewport = new FitViewport(MarioBross.V_WIDTH, MarioBross.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((MarioBross) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameWinLabel = new Label("YOU WIN!!", font);

        table.add(gameWinLabel).expandX();


        switch (MarioBross.level) {
            case "level1.tmx":
                MarioBross.level = "level2.tmx";
                break;
            case "level2.tmx":
                MarioBross.level = "level3.tmx";
                break;
            case "level3.tmx":
                MarioBross.level = "level4.tmx";
                break;
            case "level4.tmx":
                gameWinLabel.setText("YOU WIN THE GAME!!");
                finishTheGame = true;
                break;
        }
        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stateTime += delta;

        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (finishTheGame) {
            if (stateTime > 1.8) {
                Gdx.app.exit();
            }
        }
        if (stateTime > 3) {
            game.setScreen(new PlayScreen((MarioBross) game));
            dispose();
        }

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
