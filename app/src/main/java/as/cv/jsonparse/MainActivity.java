package as.cv.jsonparse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Adapter myAdapter;
    ArrayList<String> returnValue;
    Button upload;
    public static Uri[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        myAdapter = new Adapter(this);
        recyclerView.setAdapter(myAdapter);
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

        OpenCamera();
    }
    private void OpenCamera(){
        Pix.start(MainActivity.this, 100, 5);
    }
    private String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e("val", "requestCode ->  " + requestCode+"  resultCode "+resultCode);
        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {
                    returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    myAdapter.addImage(returnValue);
                }
            }
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(MainActivity.this, 100, 5);
                } else {
                    Toast.makeText(MainActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    private void upload(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Silahkan Tunggu ..");
        loading.setCancelable(false);
        loading.setIndeterminate(false);
        loading.show();
        String sendJson = null;
       try {
           JSONObject result = new JSONObject();
           JSONArray array = new JSONArray();
           for (String i  : returnValue){
               JSONObject gambar = new JSONObject();
               File f = new File(i);
               Bitmap d = new BitmapDrawable(this.getResources(), f.getAbsolutePath()).getBitmap();
               //Bitmap scaled = com.fxn.utility.Utility.getScaledBitmap(512, com.fxn.utility.Utility.getExifCorrectedBitmap(f));
               Bitmap scaled = com.fxn.utility.Utility.getScaledBitmap(512, d);
               String base64 = getStringImage(scaled);
               gambar.put("gambar", base64);
               array.put(gambar);
           }
           result.put("result",array);
           sendJson = result.toString();
       }catch (Exception e){
           e.printStackTrace();
       }

        AndroidNetworking.post("http://192.168.1.5/json/des18_4.php")
                .addBodyParameter("gambar", sendJson)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray   = response.getJSONArray("result");
                            JSONObject c          = jsonArray.getJSONObject(0);
                            String alert          = c.getString("alert");
                            loading.dismiss();
                            Toast.makeText(MainActivity.this, alert, Toast.LENGTH_LONG).show(); // do anything with response
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.dismiss();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        loading.dismiss();
                    }
                });
    }

}
