package klerman.pitapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static Pit mPit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPit = (Pit) findViewById(R.id.pitLayout);
    }

    /**
     * On "+" button listener.
     * add point to the Pit.
     * @param view
     */
    public void onAddNewPointButton(View view)
    {
        mPit.addView(null);
    }
}
