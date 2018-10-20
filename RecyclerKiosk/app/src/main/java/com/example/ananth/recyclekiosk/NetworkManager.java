package com.example.ananth.recyclekiosk;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetworkManager {
    private static OkHttpClient client = new OkHttpClient();
    public static BehaviorSubject<User> userBasicInfoSubject = BehaviorSubject.create();
    public static PublishSubject<User> userBasicInfoErrorSubject = PublishSubject.create();
    public static boolean retrievingUser = false;
    public static void getUserInfo(final String uri) {
        retrievingUser = true;
        final Request request = new Request.Builder()
                .header("User-Agent","Bob")
                .url(uri)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    String responseString = responseBody.string();
                    Log.v("response",responseString);
                    User user = getUserFromStatsGTResponse(responseString);
                    if (user == null) {
                        userBasicInfoErrorSubject.onNext(new User());
                    } else {
                        user.setId(uri.substring(uri
                                .indexOf("user/")+5));
                        userBasicInfoSubject.onNext(user);
                    }
                    retrievingUser = false;
                }
            }
        });
    }

    private static User getUserFromStatsGTResponse(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            User user = new User();
            user.setName(obj.getString("name"));
            user.setEmail(obj.getString("email"));
            user.setSchool(obj.getString("school"));
            user.setMajor(obj.getString("major"));
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
