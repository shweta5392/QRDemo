package com.example.qrdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText nameEdt;
    ImageView qrCOde;
    private Button postDataBtn;
    private TextView responseTV,response_url,response_code,response_text,person_name;
    String strNumber,strcode,strtext,strname,straccountno,strphoneno,strResponse;
    private ProgressBar loadingPB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        nameEdt = findViewById(R.id.idEdtName);
        postDataBtn = findViewById(R.id.idBtnPost);
      //  responseTV = findViewById(R.id.idTVResponse);
        loadingPB = findViewById(R.id.idLoadingPB);
        qrCOde = findViewById(R.id.imageview);
        response_url = findViewById(R.id.urlResponse);
       /* response_code = findViewById(R.id.responseCode);
        response_text = findViewById(R.id.responseText);
        person_name = findViewById(R.id.personName);*/

        // adding on click listener to our button.
        postDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validating if the text field is empty or not.
                if (nameEdt.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter the values", Toast.LENGTH_SHORT).show();
                    return;
                }
                // calling a method to post the data and passing our name and job.
                postData();
            }
        });
    }

    public void postData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            strNumber =  nameEdt.getText().toString();
            Toast.makeText(this, "Number is:"+strNumber, Toast.LENGTH_SHORT).show();
            //  object.put("parameter","value");
            object.put("mobilno",strNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //  stsdl  =  object.tos
        HttpsTrustManager.allowAllSSL();
        // Enter the correct url for your api service site
        String url = "https://202.143.96.44:1831/api/Mob/UserInfo";//getResources().getString(R.string.url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", "Details:" + response);
                        responseTV.setText("String Response : " + response.toString());
                        try {
                            strname = response.getString("personName");
                            strphoneno = response.getString("mobileno");
                            straccountno = response.getString("accountno");

                            //for url creation
                            strResponse = "upi://pay?pn="+strname.replaceAll("\\s+", "_")+"@ybl&pn="+strphoneno+"&pac="+straccountno;
                            Log.d("TAGParser","parseData:"+strResponse);
                            response_url.setText(strResponse);

                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            i.putExtra("response",strResponse);
                            startActivity(i);

                          /*  //String encoding
                            Map<EncodeHintType,Object> hints = null;
                            String encoding = guessAppropriateEncoding(strResponse);
                            if (encoding != null) {
                                hints = new EnumMap<EncodeHintType,Object>(EncodeHintType.class);
                                hints.put(EncodeHintType.CHARACTER_SET, encoding);
                            }*/

                            //for QRCOde Generator
                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                            try {
                                BitMatrix bitMatrix = multiFormatWriter.encode(strResponse,
                                        BarcodeFormat.QR_CODE, 500, 500);
                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                qrCOde.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                          /*  // JSONObject jsonObject = response.getJSONObject(response.toString());
                            strcode = response.getString("responseCode");
                            strtext = response.getString("responseText");
                            strname = response.getString("personName");
                            Log.i("TAGParser","parseData:"+strname);
                            response_code.setText("" +strcode);
                            response_text.setText("" +strtext);
                            person_name.setText("" +strname);*/

                        } catch (JSONException e) {
                            Log.d("TAG", "profile: " + e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseTV.setText(error.getMessage());
            }
        });
        requestQueue.add(jsonObjectRequest);

    }


  /*  private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }*/
}