package org.ucomplex.ucomplex.CommonDependencies.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.oneread.aghanim.components.utility.MVPCallback;

import org.ucomplex.ucomplex.CommonDependencies.Network.Retrifit.FileUploadService;
import org.ucomplex.ucomplex.CommonDependencies.Network.Retrifit.ServiceGenerator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ---------------------------------------------------
 * Created by Sermilion on 20/09/16.
 * Project: uComplex_v_2
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */
public class HttpFactory {

    private StringRequest stringRequest;
    private static HttpFactory mInstance;

    public static HttpFactory getInstance() {
        if (mInstance == null) {
            mInstance = new HttpFactory();
        }
        return mInstance;
    }

    public void cancel() {
        if (stringRequest != null)
            stringRequest.cancel();
    }

    private static final String SCHEMA = "https://";
    public static final String BASE_URL = SCHEMA + "ucomplex.org/";
    public static final String USER_EVENTS_URL = BASE_URL + "user/events?mobile=1";
    public static final String PROFILE_IMAGE_URL = BASE_URL + "files/photos/";
    public static final String AUTHENTICATIO_URL = BASE_URL + "auth?mobile=1";
    public static final String RESTORE_PASSWORD_URL = BASE_URL + "public/password?mobile=1";
    public static final String LOAD_PHOTO_URL = BASE_URL + "files/photos/";
    public static final String USER_SUBJECTS_USER_URL = BASE_URL + "student/subjects_list?mobile=1";
    public static final String USER_SUBJECTS_TEACHER_URL = BASE_URL + "teacher/subjects_list?mobile=1";
    public static final String USER_SUBJECT_URL = BASE_URL + "student/ajax/my_subjects?mobile=1";
    public static final String GET_PHOTO_URL = BASE_URL + "files/photos/";
    public static final String CALENDAR_BELT_URL = BASE_URL + "student/ajax/calendar_belt?mobile=1";
    public static final String TEACHERS_FILES_URL = BASE_URL + "student/ajax/teacher_files?mobile=1";
    public static final String STUDENTS_FILES_URL = BASE_URL + "student/my_files?mobile=1";
    public static final String DOWNLOAD_MATERIAL_URL = SCHEMA + "storage.ucomplex.org/files/users/";
    public static final String DELETE_FILE_URL = BASE_URL + "student/my_files/delete_file?mobile=1";
    public static final String RENAME_FILE_URL = BASE_URL + "student/my_files/rename_file?mobile=1";
    public static final String GET_FILE_ACCESS_URL = BASE_URL + "teacher/my_files/get_access?mobile=1";
    public static final String USERS_ONLINE_URL = BASE_URL + "student/online?mobile=1";
    public static final String USERS_SEARCH_URL = BASE_URL + "user/user_search/action";
    public static final String USERS_FRIENDS_URL = BASE_URL + "user/friends?mobile=1";
    public static final String USERS_GROUP_URL = BASE_URL + "student/ajax/my_group?mobile=1";
    public static final String USERS_LECTURERS_URL = BASE_URL + "student/ajax/my_teachers?mobile=1";
    public static final String USERS_BLACKLIST_URL = BASE_URL + "user/blacklist?mobile=1";
    private static final String UPLOAD_FILE_URL =  BASE_URL + "student/my_files/add_files?mobile=1";

    public static String encodeLoginData(String loginData) {
        byte[] authBytes;
        try {
            authBytes = loginData.getBytes("UTF-8");
            int flags = Base64.NO_WRAP | Base64.URL_SAFE;
            return Base64.encodeToString(authBytes, flags);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void httpVolley(String url,
                           final String encodedAuth,
                           Context context,
                           HashMap<String, String> params,
                           MVPCallback<String> callback) {
        if (params == null) {
            params = new HashMap<>();
        }

        final HashMap<String, String> finalParams = params;
        RequestQueue queue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url,
                response -> {
                    String utf8String;
                    try {
                        utf8String = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                        callback.onSuccess(utf8String);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            callback.onError(error);
            error.printStackTrace();

        }) {@Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return finalParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("Authorization", "Basic " + encodedAuth);
                return header;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public static void uploadFile(String filename, Uri fileUri, Context context) {
        MultipartBody.Part body = prepareFilePart("material", fileUri, context);
        RequestBody filenameBody = createPartFromString(filename);

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("file", filenameBody);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient.build()).build();
        FileUploadService client =  retrofit.create(FileUploadService.class);
        Call<ResponseBody> call = client.uploadFileWithPartMap(map, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    @NonNull
    private static RequestBody createPartFromString(String string){
        return RequestBody.create(okhttp3.MultipartBody.FORM, string);
    }

    @NonNull
    private static MultipartBody.Part prepareFilePart(String partName, Uri fileUri, Context context) {
        File file = new File(fileUri.getPath());
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(context.getContentResolver().getType(fileUri)),
                        file
                );
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
}
