package search.pointfinder;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;


public class Register extends ActionBarActivity implements View.OnClickListener {

    Button btnRegister;
    EditText txtFirtName,txtLastName,txtUserName, txtEmail,txtPassword, txtPhone, txtRapeatPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        txtFirtName =(EditText)findViewById(R.id.txtFirstName);
        txtLastName=(EditText)findViewById(R.id.txtLastName);
        txtEmail = (EditText)findViewById(R.id.txtEmail);

        txtUserName = (EditText)findViewById(R.id.txtUserName);
        txtPassword = (EditText)findViewById(R.id.txtPasword);
        txtRapeatPassword = (EditText)findViewById(R.id.txtRepeatPasword);
        txtPhone = (EditText)findViewById(R.id.txtPhone);

        btnRegister = (Button)findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    @Override
    public void onClick(View v) {



        switch (v.getId())
        {
            case R.id.btnRegister:

                String firtName=txtFirtName.getText().toString();
                String lastName=txtLastName.getText().toString();
                String userName=txtUserName.getText().toString();
                String email=txtEmail.getText().toString();
                String password=txtPassword.getText().toString();
                String phone=txtPhone.getText().toString();

                String userJoson =userJson(firtName,lastName,userName, email,password, phone);

                new AsyncTask<String,Void,String>( ){


                    @Override
                    protected String doInBackground(String... params) {

                        String userJosonObj =params[0];

                        String url ="http://mt28.dyndns.org:8088/PointApp/api/BasePointAPI/InsertUser";
                        PostAPICall postUrl = new PostAPICall();
                        String response = null;
                        try {
                            response = postUrl.post(url, userJosonObj );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("Response", response);
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        Gson g = new Gson();
                        Type t = new TypeToken<UserModel>(){}.getType();
                        boolean resultRes = g.fromJson(result,t);
                        if( resultRes)
                            startActivity(new Intent(Register.this,BasePointHome.class));
                        else

                            super.onPostExecute(result);
                    }

                }.execute(userJoson);

                break;

        }
    }
    public class PostAPICall {
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String post(String url, String json) throws IOException {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }

    String userJson(String firtName,String lastName,String userName,String email,String password,String phone) {
        return "{\"Id\":\"0\","
                + "\"UserName\":\""+userName+"\","
                + "\"FirstName\":\""+firtName+"\","
                + "\"LastName\":\""+lastName+"\","
                + "\"Email\":\""+email+"\","
                + "\"Phone\":\""+phone+"\","
                + "\"IsActive\":true,"
                + "\"PassWord\":\""+password+"\" }";
    }

}
