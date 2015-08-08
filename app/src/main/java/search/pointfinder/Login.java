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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class Login extends ActionBarActivity implements View.OnClickListener {

    Button btnLogin;
    EditText txtUserName, txtPassword;
    TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUserName =(EditText)findViewById(R.id.txtUserName);
        txtPassword=(EditText)findViewById(R.id.txtPasword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        registerLink =(TextView)findViewById(R.id.tvRegisterLink);

        btnLogin.setOnClickListener(this);
        registerLink.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {



        switch (v.getId())
        {
            case R.id.btnLogin:

                String userName=  txtUserName.getText().toString();
                String passWord =  txtPassword.getText().toString();


                AsyncTask<String, Void, String> response = new AsyncTask<String, Void, String>() {


                    @Override
                    protected String doInBackground(String... params) {

                        String url = "http://mt28.dyndns.org:8088/PointApp/api/BasePointAPI/GetUserUsingCredential?username=" + params[0] + "&password=" + params[1];
                        GetAPICall getUrl = new GetAPICall();
                        String response = null;
                        try {
                            response = getUrl.run(url);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("Response", response);
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        Gson g = new Gson();
                        Type t = new TypeToken<UserModel>() {
                        }.getType();
                        UserModel loginUser = g.fromJson(result, t);
                        if (loginUser != null && loginUser.Id > 0)
                            startActivity(new Intent(Login.this, BasePointHome.class));
                        else

                            Toast.makeText(Login.this, "Can't find your information. Please check User Name or Password ", Toast.LENGTH_SHORT).show();

                        super.onPostExecute(result);
                    }

                }.execute(userName, passWord);
                break;
            case R.id.tvRegisterLink:
                startActivity(new Intent(this,Register.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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


    public class GetAPICall {
        OkHttpClient client = new OkHttpClient();

        String run(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }


}
