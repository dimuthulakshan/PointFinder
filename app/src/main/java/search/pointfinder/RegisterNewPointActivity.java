package search.pointfinder;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class RegisterNewPointActivity extends ActionBarActivity  implements  View.OnClickListener{

    Uri imageUrl = null;
    Button btnRegisterPoint;
    EditText txtPointName, txtLatitude,txtLongitude, txtZCordinate,txtYCordinate,txtXCordinate,txtDescription;
    Spinner spPointType;
    ImageView pointImageImageView;
    private static final  String baseUrlForImage= "http://mt28.dyndns.org:8088/PointApp/images/";
    private String imageData;




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
        pointImageImageView = (ImageView)findViewById(R.id.imageViewPointImage);
        spPointType = (Spinner)findViewById(R.id.spPointType);

        btnRegisterPoint = (Button)findViewById(R.id.btnRegisterPoint);

        ArrayAdapter<CharSequence> ar = ArrayAdapter.createFromResource(this, R.array.pointtype, android.R.layout.simple_list_item_1);
        ar.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spPointType.setAdapter(ar);
        spPointType.setOnItemSelectedListener(new function());
        btnRegisterPoint.setOnClickListener(this);

        pointImageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Point Image"),1);
            }
        });
    }
    public void onActivityResult(int reqCode, int resCode,Intent data){

        if(resCode==RESULT_OK){
            if(reqCode==1){
                imageUrl = data.getData();
                Uri selectedImageUri = data.getData();
                if (Build.VERSION.SDK_INT < 19) {
                    String selectedImagePath = getPath(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
                    SetImage(bitmap);
                } else {
                    ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        parcelFileDescriptor.close();
                        SetImage(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private void SetImage(Bitmap image) {
        this.pointImageImageView.setImageBitmap(image);
        // upload
        imageData = encodeTobase64(image);
        // upload
        String imageData = encodeTobase64(image);


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
                if( result.equals("true")) {
                    startActivity(new Intent(RegisterNewPointActivity.this, BasePointHome.class));
                }
                else
                {
                    Toast.makeText(RegisterNewPointActivity.this, "Point registration is failed..", Toast.LENGTH_SHORT).show();
                }
            }

        }.execute(imageData);






    }

    public static String encodeTobase64(Bitmap image) {
        System.gc();

        if (image == null)return null;

        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] b = baos.toByteArray();

        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT); // min minSdkVersion 8

        return imageEncoded;
    }


    public String getPath(Uri uri) {
        if( uri == null ) {
            return null;
        }
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
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
                String imageUrlStr = imageUrl.toString();

                String pointJoson =pointJson(latitude, longitude, xCordinate, zCordinate, yCordinate, pointType,description,pointName,imageUrlStr);

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
                        if( result.equals("true")) {
                            startActivity(new Intent(RegisterNewPointActivity.this, BasePointHome.class));
                        }
                        else
                        {
                            Toast.makeText(RegisterNewPointActivity.this, "Point registration is failed..", Toast.LENGTH_SHORT).show();
                        }


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

    String pointJson(String latitude,String longitude,String xCordinate,String zCordinate,String yCordinate,String pointType ,String description, String pointName,String imageUrl) {
        return "{\"Id\":\"0\","
                + "\"Latitude\":\""+latitude+"\","
                + "\"Longitude\":\""+longitude+"\","
                + "\"XCordinate\":\""+xCordinate+"\","
                + "\"ZCordinate\":\""+zCordinate+"\","
                + "\"YCordinate\":\""+yCordinate+"\","
                + "\"Name\":\""+pointName+"\","
                + "\"Description\":\""+description+"\","
                + "\"ImageUrl\":\""+imageUrl+"\","
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


