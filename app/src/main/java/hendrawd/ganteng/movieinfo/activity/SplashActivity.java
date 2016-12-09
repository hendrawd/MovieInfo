package hendrawd.ganteng.movieinfo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import hendrawd.ganteng.movieinfo.R;
import hendrawd.ganteng.movieinfo.view.CustomToast;

/**
 * @author hendrawd on 11/18/16
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //it's just for show case right now, but can be implemented for loading resources
                    //if (!BuildConfig.DEBUG)
                    Thread.sleep(3500);
                    Intent goToMainActivity = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(goToMainActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage() != null)
                        CustomToast.show(SplashActivity.this, e.getMessage());
                } finally {
                    finish();
                }
            }
        }).start();
    }
}
