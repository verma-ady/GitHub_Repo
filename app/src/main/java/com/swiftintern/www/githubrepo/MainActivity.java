package com.swiftintern.www.githubrepo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.swiftintern.www.githubrepo.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    database b;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b = new database ( getApplicationContext());
        Log.v("Main", "oncreate");

//        Fragment frag = (Fragment) getFragmentManager().findFragmentById(R.layout.fragment_home);
    }


    @Override
    protected void onDestroy() {
        b.close();
        super.onDestroy();
    }

    public void onsearch( View v ){
        Log.v("Main", "onclick");
        EditText ed= (EditText)findViewById(R.id.search);
        if(ed.getText().length() == 0 ){
            Toast.makeText(getApplicationContext(), "Please Enter User ID", Toast.LENGTH_SHORT ).show();
        }

        else {
            String value = ed.getText().toString();
            getRepo gt = new getRepo();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
            dialog.setMessage("Connecting To GitHub");
            dialog.show();
            gt.execute(value);
        }
    }

    public class getRepo extends AsyncTask<String, Void, String > {

        private final String LOG_CAT = getRepo.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {

            Log.v(LOG_CAT, "URL is " + "doInBackground");
            String error=null;
            if( params.length == 0 ){
                return "null_noInput";
            }

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            String base = "https://api.github.com/users";
            String repo= "repos";

            URL url = null;
            try {

                Uri uri = Uri.parse(base).buildUpon().appendPath(params[0]).appendPath(repo).build();

                //url = new URL("https://api.github.com/users/verma-ady/repos");
                Log.v(LOG_CAT, uri.toString());
                url= new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if(inputStream==null){
                    return "null_inputstream";
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line ;

                while ( (line=bufferedReader.readLine())!=null ){
                    buffer.append(line + '\n');
                }

                if (buffer.length() == 0) {
                    return "null_inputstream";
                }

                String stringJSON = new String();
                stringJSON = buffer.toString();
                Log.v(LOG_CAT, stringJSON );
                return stringJSON;
            } catch (UnknownHostException e) {
                error = "null_internet" ;
                e.printStackTrace();
            } catch (IOException e) {
                error= "null_file";
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_CAT, "ErrorClosingStream", e);
                    }
                }
            }
            return error;
        }//doinbackground

        @Override
        protected void onPostExecute(String strJSON) {


            if( strJSON=="null_inputstream" || strJSON=="null_file" ){
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "No Such User Id Found", Toast.LENGTH_SHORT).show();
                return  ;
            }

            if ( strJSON=="null_internet" ){
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                return ;
            }
            dialog.dismiss();
            Log.v("MainActivity ", "on post " );
            Intent intent = new Intent ( MainActivity.this, Main2Activity.class).putExtra(Intent.EXTRA_TEXT, strJSON );
            startActivity(intent);

        }
    }//getrepo

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
