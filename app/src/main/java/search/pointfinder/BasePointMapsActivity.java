package search.pointfinder;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
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

import static java.lang.Double.*;

public class BasePointMapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private List<PointModel> pointList = new ArrayList<PointModel>();
    private  int selectedItem =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

         selectedItem =this.getIntent().getIntExtra("selectedItem",0);
        String xvalue=  this.getIntent().getStringExtra("xvalue");
        String yvalue =  this.getIntent().getStringExtra("yvalue");

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


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onPostExecute(result);
            }


            protected List<PointModel> GetPointArrayList(JSONArray jArray){

                PolygonOptions rectOptions = new PolygonOptions();
                LatLng mapZoom = null;
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
                        double lon = Double.parseDouble(p.Longitude);
                        double lat = Double.parseDouble(p.Latitude);

                        rectOptions.add(new LatLng(lon, lat));
                        mapZoom =new LatLng(lon, lat);
                        if(selectedItem ==i)
                        {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lon, lat)).title( p.Name)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        }else   {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lon, lat)).title( p.Name));
                        }


                        list.add(p);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Polygon polygon = mMap.addPolygon(rectOptions);
                if(mapZoom!=null)
                gotoLoacation(mapZoom,8);

                return list;
            }

            private void gotoLoacation(LatLng ll, float defaultzoom)
            {
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, defaultzoom);
                mMap.moveCamera(update);
            }



        }.execute(xvalue, yvalue);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_point_maps);
        setUpMapIfNeeded();
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

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
