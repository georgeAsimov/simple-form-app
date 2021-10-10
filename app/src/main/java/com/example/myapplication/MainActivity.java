package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity{
    AutoCompleteTextView submittedCity;
    EditText txtSubmittedName;
    EditText txtSubmittedPhone;

//    String apiUrl = "myUrlHere";
    static final String[] cities = new String[]{
            "Ankara", "Istanbul", "Mersin", "Aydin", "Izmir", "Bursa", "Manisa", "Zonguldak"
    };
    // Thread executor
    ExecutorService executor = Executors.newSingleThreadExecutor();

    private final OkHttpClient client = new OkHttpClient();
    MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    final String URL = "http://10.0.2.2/ApplicationProject/saveForm.php";
    private final String LOG = "DEMO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submittedCity = findViewById(R.id.submitCity);
        txtSubmittedName = findViewById(R.id.submitName);
        txtSubmittedPhone = findViewById(R.id.submitPhoneNumber);

        ArrayAdapter<String> arrStrAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, cities);
        submittedCity.setAdapter(arrStrAdapter);

    }

    public void onSubmitForm (View view){
        String cityName = submittedCity.getText().toString();
        String phoneNumber = txtSubmittedPhone.getText().toString();
        String nameSurname = txtSubmittedName.getText().toString();
        // do in the background, but deprecated
        // new PostData().execute(nameSurname, phoneNumber, cityName);

        // do the same as above, newer; Runnable is replaced by a lambda
        executor.execute(() -> postJson(createJsonData(nameSurname, phoneNumber, cityName), URL));
        submittedCity.setText("");
        submittedCity.setHint(submittedCity.getHint());
        txtSubmittedName.setText("");
        txtSubmittedName.setHint(txtSubmittedName.getHint());
        txtSubmittedPhone.setText("");
        txtSubmittedPhone.setHint(txtSubmittedPhone.getHint());
    }

//    @SuppressLint("StaticFieldLeak")
//    class PostData extends AsyncTask<String, Void, Void>{
//        protected Void doInBackground(@NonNull String... params){
//            JSONObject jsonParams = createJsonData(params[0], params[1], params[2]);
//            postJson(jsonParams, URL);
//            return null;
//        }
//    }


    public JSONObject createJsonData (String name, String phone, String city) {
        try {
            JSONObject jsonParams = new JSONObject();
            JSONObject jsonPostData = new JSONObject();
            jsonParams.put("name", name);
            jsonParams.put("phone", phone);
            jsonParams.put("city", city);
            jsonPostData.put("data", jsonParams);

            return jsonPostData;
        } catch (JSONException ex) {
            return null;
        }
    }

    public void postJson(@NonNull JSONObject data, String URL){
        RequestBody requestBody = RequestBody.create(data.toString(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();
        try{
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    ResponseBody body = response.body();
                    assert body != null;
                    Log.d(LOG, body.string());
                }
            });

        } finally {
            System.out.println("Fuck you");
        }
    }



}
