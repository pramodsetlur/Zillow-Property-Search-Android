package pramod.zillow;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ActionBar.Tab tab1;
        ActionBar.Tab tab2;
        try
        {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("jsonData"));
            Bundle args = new Bundle();
            args.putString("jsonObject",jsonObject.toString());

            Fragment fragmentTab1 = new BasicInfo();
            fragmentTab1.setArguments(args);

            Fragment fragmentTab2 = new Charts();
            fragmentTab2.setArguments(args);

            ActionBar actionBar = getActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            tab1 = actionBar.newTab().setText("BASIC INFO");
            tab2 = actionBar.newTab().setText("HISTORICAL ZESTIMATES");


            tab1.setTabListener(new MyTabListener(fragmentTab1));
            tab2.setTabListener(new MyTabListener(fragmentTab2));


            actionBar.addTab(tab1);
            actionBar.addTab(tab2);

        }

        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
