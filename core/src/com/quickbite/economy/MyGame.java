package com.quickbite.economy;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.quickbite.economy.managers.DefinitionManager;
import com.quickbite.economy.managers.ItemDefManager;
import com.quickbite.economy.screens.GameScreen;
import com.quickbite.economy.util.Grid;
import com.quickbite.economy.managers.ProductionsManager;
import com.quickbite.economy.util.TimeUtil;
import com.quickbite.spaceslingshot.util.Loader;

public class MyGame extends Game {
    public static com.quickbite.economy.util.EasyAssetManager manager;
	public static Viewport viewport, UIViewport;
    public static OrthographicCamera camera;
    public static SpriteBatch batch;
    public static ShapeRenderer renderer;
    public static Engine entityEngine;
    public static Grid grid;
    public static World world;
    public static Box2DDebugRenderer box2DDebugRenderer;
    public static Stage stage;

    @Override
	public void create () {
		manager = new com.quickbite.economy.util.EasyAssetManager();

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(width, height);
        viewport = new StretchViewport(width, height, camera);
        UIViewport = new StretchViewport(width, height);

        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        entityEngine = new Engine();
		grid = new Grid(25, 3000, 3000);
		world = new World(new Vector2(0f, 0f), true);
        box2DDebugRenderer = new Box2DDebugRenderer();
        stage = new Stage(UIViewport, batch);

        Loader.INSTANCE.loadAllImgs(manager, Gdx.files.internal("images/"), false);
        Loader.INSTANCE.loadFonts(manager, Gdx.files.internal("fonts/"));

        manager.finishLoading();

        ProductionsManager.INSTANCE.readProductionJson();
        DefinitionManager.INSTANCE.readDefinitionsJson();
        ItemDefManager.INSTANCE.readDefinitionJson();

        this.setScreen(new GameScreen());
	}

	@Override
	public void render () {
        TimeUtil.INSTANCE.setDeltaTime(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		super.render();

        stage.act();
        stage.draw();
	}
	
	@Override
	public void dispose () {

	}
}
