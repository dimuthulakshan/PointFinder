package search.pointfinder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class BasePointHome extends ActionBarActivity implements  View.OnClickListener {

    Button btnPointSearch;
    EditText txtXCordinateSearch, txtYCordinateSearch;
    List<PointModel> pointList = new ArrayList<PointModel>();
    ListView pointListView;
    ImageView selectedpointImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_point_home);


        txtXCordinateSearch =(EditText)findViewById(R.id.txtXCordinateSearch);
        txtYCordinateSearch=(EditText)findViewById(R.id.txtYCordinateSearch);
        btnPointSearch = (Button)findViewById(R.id.btnPointSearch);
        pointListView = (ListView) findViewById(R.id.lvResultPointslistView);
        btnPointSearch.setOnClickListener(this);
        pointListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedpointImageView = (ImageView)view.findViewById(R.id.imgPointImageViewSearchResult);
                //Intent intent =new Intent(BasePointHome.this,BasePointMapsActivity.class);
                //int selectedItem =position;
                //String xvalue=  txtXCordinateSearch.getText().toString();
                //String yvalue =  txtYCordinateSearch.getText().toString();
                //intent.putExtra("xvalue",xvalue);
                //intent.putExtra("yvalue",yvalue);
                //intent.putExtra("selectedItem",selectedItem);
               // startActivity(intent);
                PointModel seletedPoint=  pointList.get(position);

                ImageView pointImageView = (ImageView)view.findViewById(R.id.imgPointImageViewSearchResult);
                Uri uri;
                if(seletedPoint.ImageUrl != null && !seletedPoint.ImageUrl.isEmpty()) {
                    uri = Uri.parse(seletedPoint.ImageUrl);
                    pointImageView.setImageURI(uri);
                    loadPhoto(pointImageView,200,200,position);

                }
            }
        });

    }

    void loadPhoto(ImageView imageView, int width, final int height, final int position ) {

        ImageView tempImageView = imageView;


        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
                (ViewGroup) findViewById(R.id.layout_root));
        ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
        image.setImageDrawable(tempImageView.getDrawable());
                                    imageDialog.setView(layout);
                imageDialog.setPositiveButton("Go to Mapp", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Intent intent =new Intent(BasePointHome.this,BasePointMapsActivity.class);
                        int selectedItem =position;
                        String xvalue=  txtXCordinateSearch.getText().toString();
                        String yvalue =  txtYCordinateSearch.getText().toString();
                        intent.putExtra("xvalue",xvalue);
                        intent.putExtra("yvalue",yvalue);
                        intent.putExtra("selectedItem",selectedItem);
                         startActivity(intent);
                    }

        });


        imageDialog.create();
        imageDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base_point_home, menu);
        return true;
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnPointSearch:

                String xvalue=  txtXCordinateSearch.getText().toString();
                String yvalue =  txtYCordinateSearch.getText().toString();

                if(xvalue!=null && !xvalue.isEmpty() && yvalue!=null && !yvalue.isEmpty()){
                    AsyncTask<String, Void, String> response = new AsyncTask<String, Void, String>() {

                        @Override
                        protected String doInBackground(String... params) {
                            String url = "http://mt28.dyndns.org:8088/PointApp/api/BasePointAPI/GetNearestPointsForSeletedXyCordinates?xvalue="+params[0]+"&yvalue="+params[1];
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
                            Type t = new TypeToken<PointModel>() {
                            }.getType();

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
                                    p.Distance=json_data.getString("Distance");
                                    list.add(p);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            return list;
                        }

                    }.execute(xvalue, yvalue);

                }else   {
                    Toast.makeText(BasePointHome.this, "Please provide x y search data..", Toast.LENGTH_SHORT).show();
                }

            case R.id.lvResultPointslistView: {

                PointModel selItem = (PointModel) pointListView.getSelectedItem();
            }

        }

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_registerNewPoint) {
            startActivity(new Intent(BasePointHome.this,RegisterNewPointActivity.class));
        }
        if (id == R.id.action_basePointHome) {
            startActivity(new Intent(BasePointHome.this,PointHome.class));
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

    private void populateList(){
        ArrayAdapter<PointModel> adapter = new PointListAdapter();
        pointListView.setAdapter(adapter);
    }




    private  class PointListAdapter extends ArrayAdapter<PointModel>{
        public PointListAdapter()
        {
            super(BasePointHome.this,R.layout.point_listview_item,pointList);
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

            TextView distance = (TextView)view.findViewById(R.id.txtPointNameDistanceResult);
            distance.setText(currentPoint.Distance);

            Log.d("List Item", currentPoint.toString());

            ImageView pointImageView = (ImageView)view.findViewById(R.id.imgPointImageViewSearchResult);
            Uri uri;
            if(currentPoint.ImageUrl != null && !currentPoint.ImageUrl.isEmpty()) {
                uri = Uri.parse(currentPoint.ImageUrl);
                pointImageView.setImageURI(uri);
            }
            return view;

        }

    }


}
