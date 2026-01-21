package com.upf.shooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameScreen implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private String selectedBackground;
    private Player player;
    private Joystick joystick;
    private ArrayList<Enemy> enemies;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Explosion> explosions;
    private float enemySpawnTimer;
    private float enemySpawnRate = 1.5f;
    private Sound laserSound;
    private Sound explosionSound;
    private Sound playerExplosionSound;
    private Music gameOverMusic;
    private boolean gameOver;
    private float gameOverTimer;

    private final Random random = new Random();

    private class ParallaxLayer {
        Texture texture;
        float x, speed;

        public ParallaxLayer(String path, float speed) {
            if (Gdx.files.internal(path).exists()) {
                this.texture = new Texture(Gdx.files.internal(path));
            } else {
                System.out.println("Erreur: Fichier manquant -> " + path);
                this.texture = new Texture(Gdx.files.internal("backgrounds/desert/desert_sky.png")); // Par défaut
            }
            this.x = 0;
            this.speed = speed;
        }

        public void update(float delta) {
            x -= speed * delta * 200;
            if (x <= -texture.getWidth()) {
                x += texture.getWidth();
            }
        }

        public void draw(SpriteBatch batch) {
            batch.draw(texture, x, 0, texture.getWidth(), Gdx.graphics.getHeight());
            batch.draw(texture, x + texture.getWidth(), 0, texture.getWidth(), Gdx.graphics.getHeight());
        }

        public void dispose() {
            texture.dispose();
        }
    }

    private ArrayList<ParallaxLayer> layers;

    private final HashMap<String, String[]> backgroundsMap = new HashMap<String, String[]>() {{
        put("Desert", new String[]{
            "backgrounds/desert/desert_sky.png",
            "backgrounds/desert/desert_mountain.png",
            "backgrounds/desert/desert_moon.png",
            "backgrounds/desert/desert_dunemid.png",
            "backgrounds/desert/desert_dunefrontt.png"
        });
        put("Forest", new String[]{
            "backgrounds/forest/forest_sky.png",
            "backgrounds/forest/forest_mountain.png",
            "backgrounds/forest/forest_back.png",
            "backgrounds/forest/forest_mid.png",
            "backgrounds/forest/forest_short.png"
        });
        put("Moon", new String[]{
            "backgrounds/moon/moon_sky.png",
            "backgrounds/moon/moon_earth.png",
            "backgrounds/moon/moon_back.png",
            "backgrounds/moon/moon_mid.png",
            "backgrounds/moon/moon_floor.png"
        });
        put("Destroyed City", new String[]{
            "backgrounds/city destroyed/parallaxcitydestroyedsky.png",
            "backgrounds/city destroyed/parallaxcitydestroyedbuildingssmoke.png" ,
            "backgrounds/city destroyed/parallaxcitydestroyedbuildingreflexion.png",
            "backgrounds/city destroyed/parallaxcitydestroyedbuildings.png",
            "backgrounds/city destroyed/parallaxcitydestroyedwater.png"

        });
    }};

    public GameScreen(Game game, String selectedBackground) {
        this.game = game;
        this.selectedBackground = selectedBackground;
        this.gameOver = false;
        this.gameOverTimer = 0;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        layers = new ArrayList<>();
        player = new Player();
        joystick = new Joystick();
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        explosions = new ArrayList<>();
        enemySpawnTimer = 0;
        laserSound = Gdx.audio.newSound(Gdx.files.internal("music/Laser Joueur.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("music/Explode1.wav"));
        playerExplosionSound = Gdx.audio.newSound(Gdx.files.internal("music/Explode2.wav"));
        gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Game Over.mp3"));

        if (backgroundsMap.containsKey(selectedBackground)) {
            String[] paths = backgroundsMap.get(selectedBackground);
            float[] speeds = {0f, 0.1f, 0.3f, 0.6f, 1.2f};

            for (int i = 0; i < paths.length; i++) {
                layers.add(new ParallaxLayer(paths[i], speeds[i]));
            }
        } else {
            System.out.println("Erreur: Aucun fond trouvé pour " + selectedBackground);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameOver) {
            gameOverTimer += delta;
            if (gameOverTimer >= 3) {
                game.setScreen(new SplashScreen(game)); // Retour à l'écran d'accueil après 3 sec
            }
            return;
        }

        for (ParallaxLayer layer : layers) {
            layer.update(delta);
        }

        player.updateWithJoystick(joystick.getKnobPercentX(), joystick.getKnobPercentY(), delta);

        if (Gdx.input.justTouched() && player.canFire()) {
            projectiles.add(new Projectile(player.getPosition().x + 50, player.getPosition().y + 20));
            laserSound.play();
            player.resetFireTimer();
        }

        projectiles.removeIf(projectile -> !projectile.isActive());
        for (Projectile projectile : projectiles) {
            projectile.update(delta);
        }

        enemySpawnTimer += delta;
        if (enemySpawnTimer >= enemySpawnRate) {
            enemies.add(new Enemy(random.nextInt(3) + 1));
            enemySpawnTimer = 0;
        }

        enemies.removeIf(enemy -> !enemy.isActive());
        for (Enemy enemy : enemies) {
            enemy.update(delta);
        }

        // Vérification des collisions entre le joueur et les ennemis
        for (Enemy enemy : enemies) {
            if (player.getBounds().overlaps(enemy.getBounds())) {
                explosions.add(new Explosion(player.getPosition().x, player.getPosition().y));
                playerExplosionSound.play();
                gameOverMusic.play();
                gameOver = true;
                gameOverTimer = 0;
                return;
            }
        }

        for (int i = 0; i < projectiles.size(); i++) {
            Projectile projectile = projectiles.get(i);

            for (int j = 0; j < enemies.size(); j++) {
                Enemy enemy = enemies.get(j);

                if (projectile.getBounds().overlaps(enemy.getBounds())) {
                    explosions.add(new Explosion(enemy.getPosition().x, enemy.getPosition().y));
                    explosionSound.play();

                    enemies.remove(j);
                    projectiles.remove(i);
                    break;
                }
            }
        }

        explosions.removeIf(explosion -> !explosion.isActive());
        for (Explosion explosion : explosions) {
            explosion.update(delta);
        }

        batch.begin();
        for (ParallaxLayer layer : layers) {
            layer.draw(batch);
        }
        player.draw(batch);
        for (Projectile projectile : projectiles) {
            projectile.draw(batch);
        }
        for (Enemy enemy : enemies) {
            enemy.draw(batch);
        }
        for (Explosion explosion : explosions) {
            explosion.draw(batch);
        }
        batch.end();

        joystick.render();
    }

    @Override
    public void resize(int width, int height) {
        // Implémentation du redimensionnement (adapter la caméra ou le viewport si nécessaire)
    }

    @Override
    public void pause() {
        // Implémentation si nécessaire lors de la mise en pause
    }

    @Override
    public void resume() {
        // Implémentation si nécessaire lors de la reprise
    }

    @Override
    public void hide() {
        // Implémentation si nécessaire lors du changement d'écran
    }

    @Override
    public void dispose() {
        batch.dispose();
        joystick.dispose();
        laserSound.dispose();
        explosionSound.dispose();
        playerExplosionSound.dispose();
        gameOverMusic.dispose();
        for (ParallaxLayer layer : layers) {
            layer.dispose();
        }
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
        for (Projectile projectile : projectiles) {
            projectile.dispose();
        }
        for (Explosion explosion : explosions) {
            explosion.dispose();
        }
    }
}
