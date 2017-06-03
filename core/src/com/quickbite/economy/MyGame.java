package com.quickbite.economy;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.quickbite.economy.managers.DefinitionManager;
import com.quickbite.economy.screens.GameScreen;
import com.quickbite.economy.util.Constants;
import com.quickbite.economy.util.Grid;
import com.quickbite.economy.util.TimeUtil;
import com.quickbite.spaceslingshot.util.Loader;

public class MyGame extends Game {
    public static com.quickbite.economy.util.EasyAssetManager manager;
	public static Viewport viewport, UIViewport;
    public static OrthographicCamera camera, UICamera, box2dCamera;
    public static SpriteBatch batch, UIBatch;
    public static ShapeRenderer renderer;
    public static Engine entityEngine;
    public static Grid grid;
    public static World world;
    public static Box2DDebugRenderer box2DDebugRenderer;
    public static Stage stage;

    public static BitmapFont defaultFont14;
    public static BitmapFont defaultFont20;

    @Override
	public void create () {
		manager = new com.quickbite.economy.util.EasyAssetManager();

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(width, height);
        UICamera = new OrthographicCamera(width, height);
        box2dCamera = new OrthographicCamera(width*Constants.BOX2D_SCALE, height*Constants.BOX2D_SCALE);

        viewport = new StretchViewport(width, height, camera);
        UIViewport = new StretchViewport(width, height, UICamera);

        batch = new SpriteBatch();
        UIBatch = new SpriteBatch();
        renderer = new ShapeRenderer();
        entityEngine = new Engine();
		grid = new Grid(32, 3000, 3000);
		world = new World(new Vector2(0f, 0f), true);
        box2DDebugRenderer = new Box2DDebugRenderer();
        stage = new Stage(UIViewport, UIBatch);

        Loader.INSTANCE.loadAllImgs(manager, Gdx.files.internal("images/"), true);
        Loader.INSTANCE.loadFonts(manager, Gdx.files.internal("fonts/"));

        genFonts();

        manager.finishLoading();

        DefinitionManager.INSTANCE.loadDefinitions();

        this.setScreen(new GameScreen());
	}

	private void genFonts(){
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/titillium/Titillium-Regular.otf"));
        parameter.size = 14;
        parameter.genMipMaps = true;
        parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest;
        parameter.magFilter = Texture.TextureFilter.Linear;
        defaultFont14 = generator.generateFont(parameter); // font size 14 pixels

        parameter.size = 20;
        parameter.genMipMaps = true;
        parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest;
        parameter.magFilter = Texture.TextureFilter.Linear;
        defaultFont20 = generator.generateFont(parameter); // font size 14 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
    }

	@Override
	public void render () {
        TimeUtil.INSTANCE.setDeltaTime(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		UICamera.update();

		super.render();

        stage.act();
        stage.draw();

//        UIBatch.setProjectionMatrix(UICamera.combined);
//        UIBatch.setColor(1f, 1f, 1f, 1f); //Stupid stage messes with the color so we have to reset it here
//        UIBatch.begin();
//        TutorialTest.INSTANCE.render(Gdx.graphics.getDeltaTime(), UIBatch); //TODO Maybe this could go in a better place? Needs to be after the stage though
//        UIBatch.end();
    }
	
	@Override
	public void dispose () {

	}
}
