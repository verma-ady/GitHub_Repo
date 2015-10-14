package com.swiftintern.www.githubrepo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class onListClick extends AppCompatActivity {

    public String error=new String();
    database d;
    String userid, stringJSON;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_list_click);
        d = new database(getApplicationContext());
        Intent intent = getIntent();

        if( (intent != null) && intent.hasExtra(Intent.EXTRA_TEXT) ){
            userid = intent.getStringExtra(Intent.EXTRA_TEXT);
            check();
            getuserinfo gi = new getuserinfo();
            gi.execute(userid);
        }
    }

    public void onfollowbutton ( View v ){
        ImageButton ib = (ImageButton) findViewById(R.id.following_user_info);
        String a = ib.getContentDescription().toString();
        Log.v("onfollowbutton", a);
        if( a.equals("following") ){
            Log.v("onfollowbutton if", "following  ");
            if ( d.delete(userid) ){
                ib.setContentDescription("follow");
                ib.setImageResource(R.drawable.follow);
                Toast.makeText(getApplicationContext(), userid + " Unfollowed", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Failed Unfollow request ", Toast.LENGTH_SHORT).show();
            }
        }

        else if ( a.equals("follow") ){
            Log.v("onfollowbutton else", "follow  ");
            if ( d.onAdd(userid) ){
                ib.setContentDescription("following");
                ib.setImageResource(R.drawable.following);
                Toast.makeText(getApplicationContext(), userid + " followed again", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Failed follow request ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onlistrepo( View v){
        getRepo gt = new getRepo();
        dialog = new ProgressDialog(onListClick.this);
        dialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        dialog.setMessage("Connecting To GitHub");
        dialog.show();
        gt.execute(userid);
    }

    public void check (){
        ImageButton imageButton = (ImageButton) findViewById(R.id.following_user_info);
        boolean b = d.isthere(userid);
        if ( b ){
            Log.v("onList.onfollowbutton", "found" );
            imageButton.setImageResource(R.drawable.following);
            imageButton.setContentDescription("following");
        } else {
            Log.v("onList.onfollowbutton", "not found" );
            imageButton.setImageResource(R.drawable.follow);
            imageButton.setContentDescription("follow");
        }
    }

    public class getuserinfo extends AsyncTask<String, Void, String > {

        private final String LOG_CAT = getuserinfo.class.getSimpleName();
        public String base = "https://api.github.com/users";
        public String JSONSTRING = new String();
        public String mail, name, company, location, text_data, img_url;
        public Long id, follower, following, repos;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(onListClick.this);
            dialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
            dialog.setMessage("Connecting To GitHub");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;
            URL url=null;

            Uri uri = Uri.parse(base).buildUpon().appendPath(params[0]).build();
            Log.v(LOG_CAT, uri.toString());
            try {
                url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = null;
                inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    error = "null_inputstream";
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                JSONSTRING = buffer.toString();
                return JSONSTRING;
            } catch (UnknownHostException | ConnectException e) {
                error = "null_internet" ;
                e.printStackTrace();
            } catch (IOException e) {
                error= "null_file";
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_CAT, "ErrorClosingStream", e);
                    }
                }
            }
            return error;
        }//do in background

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
            try {
                JSONObject JSON = new JSONObject(strJSON);
                name = JSON.getString("name");
                id = JSON.getLong("id");
                follower = JSON.getLong("followers");
                following = JSON.getLong("following");
                mail=JSON.getString("email");
                company=JSON.getString("company");
                location=JSON.getString("location");
                repos=JSON.getLong("public_repos");
                img_url=JSON.getString("avatar_url");
//                Bitmap bitmap =
                Log.v("Onclick", id.toString()+" "+follower.toString()+" "+following.toString()+" "+mail+" "+name+" "+company+" "+location );
            } catch ( JSONException e ){
                e.printStackTrace();
            }

            TextView head = (TextView) findViewById(R.id.head_on_list);
            if (name.equals("null")){
                head.setText("******");
            }

            else{
                head.setText(name);
            }

            TextView data = (TextView) findViewById(R.id.data_on_list);
            StringBuffer strData=new StringBuffer();
            strData.append("User ID       : " + id.toString() + "\n");
            if (mail!="null"){
                strData.append("EMail ID      : " + mail + "\n");
            } else{
                strData.append("EMail ID      : " + "********" + "\n");
            }

            if (company!="null"){
                strData.append("Company       : " + company + "\n");
            }

            if(location!="null"){
                strData.append("Location      : " + location + "\n");
            }

            strData.append("Public Repos  : " + repos.toString()  + "\n");
            strData.append("Followers     : " + follower.toString() + "\n");
            strData.append("Following     : " + following.toString() + "\n");

            text_data= strData.toString();
            Log.v("Onpost", text_data);
            data.setText(text_data);

            getbitmap gb = new getbitmap();
            gb.execute(img_url);

        }//onpost

    }//class getuserinfo


    public class getbitmap extends AsyncTask<String, Void, Bitmap>{

        public Bitmap myBitmap;
        public InputStream input = null;
        HttpURLConnection connection = null;
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0] );
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
                Log.e("Bitmap", "returned");

            } catch ( IOException e ){
                e.printStackTrace();
            } finally {
                if( connection!=null ){

                }
            }
            return myBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap b ) {
            ImageView iv = (ImageView) findViewById(R.id.imageView);
            iv.setImageBitmap(b);
            super.onPostExecute(b);
        }
    }//getbitmap


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
            } catch (UnknownHostException | ConnectException e) {
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


            if( strJSON.equals("null_inputstream") || strJSON.equals("null_file") ){
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "No Such User Id Found", Toast.LENGTH_SHORT).show();
                return  ;
            }

            if ( strJSON.equals("null_internet") ){
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                return ;
            }
            dialog.dismiss();
            Log.v("MainActivity ", "on post ");
            Intent intent = new Intent ( getApplicationContext(), Main2Activity.class).putExtra(Intent.EXTRA_TEXT, strJSON );
            startActivity(intent);

        }
    }//getrepo



    @Override
    protected void onResume() {
        d=new database(getApplicationContext());
        check();
        super.onResume();
    }

    @Override
    protected void onPause() {
        d.close();
        super.onPause();
    }

    @Override
    protected void onStop() {
        d.close();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_on_list_click, menu);
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
