package com.upf.shooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;

public class SplashScreen implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Music introMusic;
    private int selectedIndex = 0;
    private boolean isPlayButtonSelected = false;

    private final String[] backgrounds = {"Desert", "Forest", "Moon", "Destroyed City"};

    public SplashScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(3.0f); // Taille augmentée

        introMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Title Music.mp3"));
        introMusic.setLooping(false);
        introMusic.play();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Affichage du titre
        font.getData().setScale(3.5f);
        GlyphLayout titleLayout = new GlyphLayout(font, "Shooter");
        font.draw(batch, titleLayout, (Gdx.graphics.getWidth() - titleLayout.width) / 2, Gdx.graphics.getHeight() - 100);

        // Affichage du menu de sélection du décor
        font.getData().setScale(2.8f);
        for (int i = 0; i < backgrounds.length; i++) {
            String text = (i == selectedIndex && !isPlayButtonSelected) ? "> " + backgrounds[i] + " <" : backgrounds[i];
            GlyphLayout optionLayout = new GlyphLayout(font, text);
            float x = (Gdx.graphics.getWidth() - optionLayout.width) / 2;
            float y = Gdx.graphics.getHeight() - 350 - (i * 80);
            font.draw(batch, optionLayout, x, y);
        }

        // Affichage du bouton "Jouer"
        font.getData().setScale(3.0f);
        String playText = isPlayButtonSelected ? "> Jouer <" : "Jouer";
        GlyphLayout playLayout = new GlyphLayout(font, playText);
        float playX = (Gdx.graphics.getWidth() - playLayout.width) / 2;
        float playY = Gdx.graphics.getHeight() - 750;
        font.draw(batch, playLayout, playX, playY);

        // Affichage du nom et promotion sous "Jouer"
        font.getData().setScale(2.5f);
        GlyphLayout nameLayout = new GlyphLayout(font, "TCHEN PAN Lennon\nL3 Info 2024-2025 UPF");
        font.draw(batch, nameLayout, (Gdx.graphics.getWidth() - nameLayout.width) / 2, playY - 120);

        batch.end();

        // Navigation avec les touches Haut / Bas
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (!isPlayButtonSelected) {
                selectedIndex = (selectedIndex + 1) % backgrounds.length;
            } else {
                isPlayButtonSelected = false; // Revenir à la sélection du décor
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (isPlayButtonSelected) {
                isPlayButtonSelected = false;
            } else if (selectedIndex == 0) {
                isPlayButtonSelected = true;
            } else {
                selectedIndex = (selectedIndex - 1 + backgrounds.length) % backgrounds.length;
            }
        }

        // Sélection avec un clic
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPos.y = Gdx.graphics.getHeight() - touchPos.y; // Conversion des coordonnées

            // Vérifier si on clique sur un décor
            for (int i = 0; i < backgrounds.length; i++) {
                float textY = Gdx.graphics.getHeight() - 350 - (i * 80);
                if (touchPos.y > textY - 40 && touchPos.y < textY + 40) {
                    selectedIndex = i;
                    isPlayButtonSelected = false;
                }
            }

            // Vérification du clic sur le bouton "Jouer"
            if (touchPos.y > playY - 50 && touchPos.y < playY + 50) {
                isPlayButtonSelected = true;
            }
        }

        // Lancer le jeu avec Entrée ou un clic sur "Jouer"
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || (Gdx.input.justTouched() && isPlayButtonSelected)) {
            game.setScreen(new GameScreen(game, backgrounds[selectedIndex]));
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        introMusic.dispose();
    }
}
