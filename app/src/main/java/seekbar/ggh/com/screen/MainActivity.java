package seekbar.ggh.com.screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import seekbar.ggh.com.soundsrecord.R;


/**
 * 投屏
 */
public class MainActivity extends Activity {
    private Button screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        screen = (Button) findViewById(R.id.screen);


        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScreenActivity.class));
                }

        });

    }


}
