package com.example.ephtron.helloandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText urlInput;
    private LinearLayout resultLayout;
    private RadioGroup radioButtons;
    private String url;
    private String mode = "html";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlInput = (EditText) findViewById(R.id.url_input);
        resultLayout = (LinearLayout) findViewById(R.id.result_layout);
        radioButtons = (RadioGroup) findViewById(R.id.radio_buttons);
        updateStatusText("Ready");
    }

    private void updateStatusText(String s) {
        TextView statusText = (TextView) findViewById(R.id.status_text);
        statusText.setText(s);
    }

    public void establishConnection(View view) {
        System.out.println("Connecting");
        ConnectivityManager connection = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connection.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            updateStatusText("Internet available.");

            // Check and add "http://"
            url = urlInput.getText().toString();
            if (!url.startsWith("http://")) {
                url = "http://" + url;
            }

            // check which mode is selected
            int selectedButton = radioButtons.getCheckedRadioButtonId();
            if (selectedButton == R.id.plain_button){
                mode="plain";
                updateStatusText("Fetching Plaintext...");
            }else if (selectedButton==R.id.html_button) {
                mode="html";
                updateStatusText("Fetching Website...");
            }
            createResultView(url);
        } else {
            updateStatusText("No connection available");
        }
    }

    //  code modified from https://developer.android.com/reference/java/net/HttpURLConnection.html
    private String connectToUrl(String url){
        String result = "";

        URL _url;
        try {
            _url = new URL(url);
        } catch (MalformedURLException e) {
            System.out.println("Malformed");
            e.printStackTrace();
            result = "Error " + e.getMessage().toString();
            return result;
        }

        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) _url.openConnection();
        } catch (IOException e) {
            System.out.println("Connection Problem");
            e.printStackTrace();
            result = "Error " + e.getMessage().toString();
            return result;
        }

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = readStream(in);
        } catch (IOException e) {
            System.out.println("Stream related problem");
            e.printStackTrace();
            result = "Error " + e.getMessage().toString();
            return result;
        } finally {
            urlConnection.disconnect();
        }
        return result;
    }

    // code from http://stackoverflow.com/questions/9856195/how-to-read-an-http-input-stream
    private String readStream(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder result = new StringBuilder();
        String line;
        try {
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            System.out.println("Stream reading problem");
            e.printStackTrace();
            return "Error " + e.getMessage().toString();
        }
        System.out.println(result.toString());
        return result.toString();
    }

    public void createResultView(String url) {
        resultLayout.removeAllViews();
        updateStatusText("Done");
        if (mode.equals("html")) {
            WebView web = new WebView(this);

            web.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Log.i("WEB_VIEW_TEST", "error code:" + errorCode);
                    updateStatusText("Error - " + description);
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }
            });
            web.loadUrl(url);
            resultLayout.addView(web);
        } else if (mode.equals("plain")) {
            TextView txtView = new TextView(this);
            new HTTPTask(txtView).execute(url);
            ScrollView sv = new ScrollView(this);
            sv.addView(txtView);
            resultLayout.addView(sv);
        }
    }

    private class HTTPTask extends AsyncTask<String, Void, String> {
        TextView tView;

        public HTTPTask(TextView t){
            this.tView = t;
        }

        @Override
        protected String doInBackground(String... urls) {
            return connectToUrl(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(" #########################################"+result);
            if (result.startsWith("Error")) {
                updateStatusText(result);
            } else {
                tView.setText(result);
            }
        }
    }
}