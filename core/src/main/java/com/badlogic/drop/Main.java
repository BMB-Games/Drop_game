package com.badlogic.drop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.awt.*;


public class Main implements ApplicationListener {
    private Texture backgroundTexture;
    private Texture bucketTexture;
    private Texture dropTexture;
    private Sound dropSound;
    private Music music;
    private SpriteBatch batch;
    private FitViewport viewport;
    private Float worldWidth = 8f;
    private Float worldHeight = 5f;
    private Sprite bucketSprite;
    private Vector2 touchPosition;
    private Array<Sprite> dropSprites;
    private float dropletTimer;
    private Rectangle bucketRect;
    private Rectangle dropRect;


    @Override
    public void create() {
        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        batch = new SpriteBatch();
        viewport = new FitViewport(worldWidth, worldHeight);
        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1,1);
        touchPosition = new Vector2();
        dropSprites = new Array<>();
        bucketRect = new Rectangle();
        dropRect = new Rectangle();


        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(backgroundTexture, 0,0, worldWidth, worldHeight);
        bucketSprite.draw(batch);
        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(batch);
        }
        batch.end();
    }

    public void createDroplet() {
        float dropWidth = 1;
        float dropHeight = 1;
        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);
    }

    private void logic() {
        float bucketWidth = bucketSprite.getWidth();
        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth - bucketWidth));
        float delta = Gdx.graphics.getDeltaTime();
        float bucketHeight = bucketSprite.getHeight();
        bucketRect.setRect(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);

        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-2f * delta);
            dropRect.setRect(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);
            if(dropRect.intersects(bucketRect)) {
                dropSprites.removeIndex(i);
                dropSound.play();
            }

            if (dropSprite.getY() < -dropHeight) {
                dropSprites.removeIndex(i);
            }
        }

        dropletTimer += delta;
        if (dropletTimer >= 1) {
            dropletTimer = 0;
            createDroplet();
        }
    }

    private void input() {
        float speed = 10f;
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucketSprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucketSprite.translateX(-speed * delta);
        }


        if (Gdx.input.isTouched()) {
            touchPosition.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPosition);
            bucketSprite.setCenterX(touchPosition.x);
        }
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}
