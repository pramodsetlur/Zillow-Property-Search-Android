package pramod.zillow;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    boolean valid = false;
    JSONParser jsonParser;
    String address,city,state;
    private static String zillowUrl;
    boolean boolAddr = true;
    boolean boolCity = true;
    boolean boolState = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.state);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.state, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Button searchBtn = (Button) findViewById(R.id.search);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText addrTextBox = (EditText) findViewById(R.id.address);
                address = addrTextBox.getText().toString();

                EditText cityTextBox = (EditText) findViewById(R.id.city);
                city = cityTextBox.getText().toString();

                Spinner stateSpinner = (Spinner)findViewById(R.id.state);
                state = stateSpinner.getSelectedItem().toString();

                if(0==address.compareTo(""))
                {
                    TextView errorAddress = (TextView) findViewById(R.id.errorAddress);
                    errorAddress.setVisibility(View.VISIBLE);
                    boolAddr = false;
                }
                else
                {
                    TextView errorAddress = (TextView) findViewById(R.id.errorAddress);
                    errorAddress.setVisibility(View.INVISIBLE);
                    boolAddr = true;
                }

                if(0==city.compareTo(""))
                {
                    TextView errorCity = (TextView) findViewById(R.id.errorCity);
                    errorCity.setVisibility(View.VISIBLE);
                    boolCity = false;
                }
                else
                {
                    TextView errorCity = (TextView) findViewById(R.id.errorCity);
                    errorCity.setVisibility(View.INVISIBLE);
                    boolCity = true;
                }

                if(0==state.compareTo("Choose State"))
                {
                    TextView errorState = (TextView) findViewById(R.id.errorState);
                    errorState.setVisibility(View.VISIBLE);
                    boolState = false;
                }
                else
                {
                    TextView errorState = (TextView) findViewById(R.id.errorState);
                    errorState.setVisibility(View.INVISIBLE);
                    boolState = true;
                }

                if(boolAddr && boolCity && boolState)
                {
                    String formattedAddress = new String(address.replaceAll("\\s+","+"));
                    String formattedCity = new String(city.replaceAll("\\s+","+"));

                    zillowUrl=new String("http://zillowrealestate-env.elasticbeanstalk.com/?street="+formattedAddress+"&city="+formattedCity+"&state="+state);
                    new FetchTask().execute(zillowUrl);
                }

            }
        });

        ImageView zillowLogo = (ImageView) findViewById(R.id.zillowLogo);
        zillowLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//TODO: clicking on the link
            }
        });
    }

    private class FetchTask extends AsyncTask<String, Void, JSONObject>
    {

        @Override
        protected JSONObject doInBackground(String... params)
        {
            try
            {
                JSONObject jsonObject;
                System.out.println(params[0]);
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                HttpResponse response = client.execute(get);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String json = reader.readLine();
                System.out.println(json);
                jsonObject = new JSONObject(json);
                return jsonObject;
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(JSONObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                String valid = (String)result.get("valid");
                if(!valid.equals("0"))
                {
                    TextView noMatchFound = (TextView) findViewById(R.id.noMatchFound);
                    noMatchFound.setVisibility(View.VISIBLE);
                }
                else
                {
                    TextView noMatchFound = (TextView) findViewById(R.id.noMatchFound);
                    noMatchFound.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("jsonData",result.toString());
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
