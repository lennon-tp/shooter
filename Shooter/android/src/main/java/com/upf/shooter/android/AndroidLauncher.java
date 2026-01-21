package com.upf.shooter.android;

import android.os.Bundle;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.upf.shooter.SplashScreen;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.

        // Lancer directement le SplashScreen
        initialize(new Game() {
            @Override
            public void create() {
                setScreen(new SplashScreen(this));
            }
        }, configuration);
    }
}
