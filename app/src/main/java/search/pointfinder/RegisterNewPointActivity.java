package search.pointfinder;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;


public class RegisterNewPointActivity extends ActionBarActivity  implements  View.OnClickListener{

    Button btnRegisterPoint;
    EditText txtPointName, txtLatitude,txtLongitude, txtZCordinate,txtYCordinate,txtXCordinate,txtDescription;
    Spinner spPointType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_point);

        txtPointName =(EditText)findViewById(R.id.txtPointName);
        txtLatitude =(EditText)findViewById(R.id.txtLatitude);
        txtLongitude=(EditText)findViewById(R.id.txtLongitude);
        txtXCordinate= (EditText)findViewById(R.id.txtXCordinate);
        txtZCordinate = (EditText)findViewById(R.id.txtZCordinate);
        txtYCordinate = (EditText)findViewById(R.id.txtYCordinate);
        txtDescription = (EditText)findViewById(R.id.txtDescription);

        spPointType = (Spinner)findViewById(R.id.spPointType);

        btnRegisterPoint = (Button)findViewById(R.id.btnRegisterPoint);

        ArrayAdapter<CharSequence> ar = ArrayAdapter.createFromResource(this, R.array.pointtype, android.R.layout.simple_list_item_1);
        ar.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spPointType.setAdapter(ar);
        spPointType.setOnItemSelectedListener(new function());
        btnRegisterPoint.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_new_point, menu);
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


        switch (v.getId()) {
            case R.id.btnRegisterPoint:
                String pointName =txtPointName.getText().toString();
                String latitude=txtLatitude.getText().toString();
                String longitude=txtLongitude.getText().toString();
                String xCordinate=txtXCordinate.getText().toString();
                String zCordinate=txtZCordinate.getText().toString();
                String yCordinate=txtYCordinate.getText().toString();
                String pointType = spPointType.getSelectedItem().toString();
                String description = txtDescription.getText().toString();

                String pointJoson =pointJson(latitude, longitude, xCordinate, zCordinate, yCordinate, pointType,description,pointName);

                new AsyncTask<String,Void,String>( ){


                    @Override
                    protected String doInBackground(String... params) {

                        String pointJosonObj =params[0];

                        String url ="http://mt28.dyndns.org:8088/PointApp/api/BasePointAPI/InsertPoint";
                        PostAPICall postUrl = new PostAPICall();
                        String response = null;
                        try {
                            response = postUrl.post(url, pointJosonObj );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("Response", response);
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        Gson g = new Gson();
                        if( result=="true")
                            startActivity(new Intent(RegisterNewPointActivity.this,BasePointHome.class));
                        else

                            super.onPostExecute(result);
                    }

                }.execute(pointJoson);

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

    String pointJson(String latitude,String longitude,String xCordinate,String zCordinate,String yCordinate,String pointType ,String description, String pointName) {
        return "{\"Id\":\"0\","
                + "\"Latitude\":\""+latitude+"\","
                + "\"Longitude\":\""+longitude+"\","
                + "\"XCordinate\":\""+xCordinate+"\","
                + "\"ZCordinate\":\""+zCordinate+"\","
                + "\"YCordinate\":\""+yCordinate+"\","
                + "\"Name\":\""+pointName+"\","
                + "\"Description\":\""+description+"\","
                + "\"IsActive\":true,"
                + "\"IsDeleted\":false,"
                + "\"PointType\":\""+pointType+"\" }";
    }
    public class function implements AdapterView.OnItemSelectedListener {
        private long itemIdAtPosition;

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            itemIdAtPosition = parent.getItemIdAtPosition(position);


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}


