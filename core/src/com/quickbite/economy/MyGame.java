package com.quickbite.economy;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.quickbite.economy.screens.GameScreen;
import com.quickbite.economy.util.Grid;
import com.quickbite.spaceslingshot.util.Loader;

public class MyGame extends Game {
    public static com.quickbite.economy.util.EasyAssetManager manager;
	public static Viewport viewport;
    public static OrthographicCamera camera;
    public static SpriteBatch batch;
    public static ShapeRenderer renderer;
    public static Engine entityEngine;
    public static Grid grid;

    private static int width = 1080, height = 720;

	@Override
	public void create () {
		manager = new com.quickbite.economy.util.EasyAssetManager();

		camera = new OrthographicCamera(width, height);
        viewport = new StretchViewport(width, height, camera);

        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        entityEngine = new Engine();
		grid = new Grid(25, 3000, 3000);

        Loader.INSTANCE.loadAllImgs(manager, Gdx.files.internal("images/"), false);

        manager.finishLoading();

        this.setScreen(new GameScreen());
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		super.render();
	}
	
	@Override
	public void dispose () {

	}
}
