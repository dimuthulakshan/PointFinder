package search.pointfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class PointHome extends ActionBarActivity  implements  View.OnClickListener {
    List<PointModel> pointList = new ArrayList<PointModel>();
    ListView pointHomeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pointHomeListView=(ListView) findViewById(R.id.pointHomeListView);
        setContentView(R.layout.activity_point_home);


        AsyncTask<String, Void, String> response = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                String url = "http://mt28.dyndns.org:8088/PointApp/api/BasePointAPI/GetAllBasePoints";
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

                try {
                    JSONArray array = new JSONArray(result);
                    pointList = GetPointArrayList(array);
                    populateList();

                } catch (JSONException e) {
                    e.printStackTrace();
                }



                Log.d("Response", pointList.toString());
                super.onPostExecute(result);
            }


            protected List<PointModel> GetPointArrayList(JSONArray jArray){
                List<PointModel> list = new ArrayList();
                for(int i=0; i<jArray.length(); i++){
                    PointModel p = new PointModel();
                    JSONObject json_data = null;
                    try {
                        json_data = jArray.getJSONObject(i);
                        p.Id=json_data.getInt("Id");
                        p.Name=json_data.getString("Name");
                        p.XCordinate=json_data.getString("XCordinate");
                        p.YCordinate=json_data.getString("YCordinate");
                        p.ZCordinate=json_data.getString("ZCordinate");
                        p.Longitude=json_data.getString("Longitude");
                        p.Latitude=json_data.getString("Latitude");
                        p.PointType=json_data.getString("PointType");
                        p.ImageUrl=json_data.getString("ImageUrl");
                        p.Description=json_data.getString("Description");
                        list.add(p);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return list;
            }

        }.execute();


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

    private void populateList(){
        ArrayAdapter<PointModel> adapter = new PointHomeListAdapter();
        pointHomeListView.setAdapter(adapter);
    }




    private  class PointHomeListAdapter extends ArrayAdapter<PointModel>{
        public PointHomeListAdapter()
        {
            super(PointHome.this,R.layout.point_listview_item,pointList);
        }


        public View getView(int position, View view, ViewGroup parent){
            if(view==null)
                view = getLayoutInflater().inflate(R.layout.point_listview_item,parent,false);

            PointModel currentPoint = pointList.get(position);
            TextView name = (TextView)view.findViewById(R.id.txtPointNameSearchResult);
            name.setText(currentPoint.Name);
            TextView yvalue = (TextView)view.findViewById(R.id.txtYCordinateSearchResult);
            yvalue.setText(currentPoint.YCordinate);
            TextView xvalue = (TextView)view.findViewById(R.id.txtXCordinateSearchResult);
            xvalue.setText(currentPoint.XCordinate);

            ImageView pointImageView = (ImageView)view.findViewById(R.id.imgPointImageViewSearchResult);
            Uri uri;
            if(currentPoint.ImageUrl != null && !currentPoint.ImageUrl.isEmpty()) {
                uri = Uri.parse(currentPoint.ImageUrl);
                pointImageView.setImageURI(uri);
            }
            return view;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_point_home, menu);
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
            case R.id.btnPointSearch:
        }
    }
}
