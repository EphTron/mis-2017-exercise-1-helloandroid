package com.example.ephtron.helloandroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

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
            updateStatusText("Connected.");

            // Check and add "http://"
            url = urlInput.getText().toString();
            if (!url.startsWith("http://")) {
                url = "http://" + url;
            }

            // check which mode is selected
            int selectedButton = radioButtons.getCheckedRadioButtonId();
            if (selectedButton == R.id.plain_button){
                mode="plain";
            }else if (selectedButton==R.id.image_button) {
                mode="image";
            }else if (selectedButton==R.id.html_button) {
                mode="html";
            }
            new HTTPTask().execute(url);
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
            e.printStackTrace();
            return result;
        }

        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) _url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = readStream(in);
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();

        }
        System.out.println(result.toString());
        return result.toString();
    }

    public void createView() {
        // Add textview 1
        TextView textView1 = new TextView(this);
        textView1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        textView1.setText("programmatically created TextView1");
        textView1.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
        textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
        resultLayout.addView(textView1);

        // Add textview 2
        TextView textView2 = new TextView(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        layoutParams.setMargins(10, 10, 10, 10); // (left, top, right, bottom)
        textView2.setLayoutParams(layoutParams);
        textView2.setText("programmatically created TextView2");
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView2.setBackgroundColor(0xffffdbdb); // hex color 0xAARRGGBB
        resultLayout.addView(textView2);
    }



    public class HTTPTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return connectToUrl(urls[0]);
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

    private void check(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_broken);

        urlText = (EditText)findViewById(R.id.url_text);


        Intent intent = getIntent();

        ArrayList<String> parsed_strings = intent.getExtras().getStringArrayList("mode_result");
        String mode=(parsed_strings.get(0));
        String result=(parsed_strings.get(1));
        String url=(parsed_strings.get(2));
        final RelativeLayout lm = (RelativeLayout) findViewById(R.id.main);

        if (mode.equals("html")) {
            WebView web = new WebView(this);
            web.setWebViewClient(new WebViewClient());
            web.loadUrl(url);
            //web.loadData(result,"text/html",null);
            lm.addView(web);
        }
        else if (mode.equals("plain")) {
            TextView txt =new TextView(this);
            txt.append(result);
            ScrollView sv = new ScrollView(this);
            sv.addView(txt);
            lm.addView(sv);
        }
        else if (mode.equals("image")) {
            ImageView img = new ImageView(this);
            new DownloadImageTask(img).execute(url);
            lm.addView(img);
        }
    }
}