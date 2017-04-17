package com.example.ephtron.helloandroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText urlInput;
    private LinearLayout resultLayout;
    private RadioGroup radioButtons;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlInput = (EditText) findViewById(R.id.url_input);
        resultLayout = (LinearLayout) findViewById(R.id.result_layout);
        radioButtons = (RadioGroup) findViewById(R.id.radio_buttons);
    }

    private void updateStatusText(String s) {
        TextView statusText = (TextView) findViewById(R.id.status_text);
        statusText.setText(s);
    }

    public void establishConnection(View view) {
        ConnectivityManager connection = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connection.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            updateStatusText("Connected.");

            // Check and add "http://"
            url = urlInput.getText().toString();
            if (!url.startsWith("http://")) {
                url = "http://" + url;
            }
            new HTTPTask().execute(url);
        } else {
            updateStatusText("No connection available");
        }
    }



    public class HTTPTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                // connect to url
                //return connect(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("exception")) {
                updateStatusText(result);
            } else {
                // add result to text/image/html view
            }
        }

    }
}