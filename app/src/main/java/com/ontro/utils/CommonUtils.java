package com.ontro.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.ontro.Constants;
import com.ontro.R;
import com.ontro.RecyclerItemClickListener;
import com.ontro.adapters.LocationAdapter;
import com.ontro.dto.LocationModel;
import com.ontro.rest.ApiClient;
import com.ontro.rest.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by umm on 29-Mar-17.
 */

public class CommonUtils {
    public static int TIMEOUT_VALUE = 10000;
    public static int Badminton = 1;
    public static int Basketball = 2;
    public static int Carrom = 3;
    public static int Cricket = 4;
    public static int Football = 5;
    public static int Tennis = 6;
    public static int Volleyball = 7;
    public static String default_image = "http://ideomind.in/demo/ontro/public/img/default.jpg", locationid = "", cityid = "1";
    public static Typeface typeface_regular;
    public static PreferenceHelper preferenceHelper;
    public static ApiInterface apiInterface;
    public static RecyclerView locationdata;
    private static EditText location_edit;
    private static LocationAdapter locationadapter;
    private static CircularProgressView location_progress;
    private static ArrayList<LocationModel> locationarrayList = new ArrayList<>();

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String convertDateFormat(String date, String inputFormat, String outputFormat) {

        String result = "";
        SimpleDateFormat sdf;
        SimpleDateFormat sdf1;

        try {
            sdf = new SimpleDateFormat(inputFormat);
            sdf1 = new SimpleDateFormat(outputFormat);
            result = sdf1.format(sdf.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            sdf = null;
            sdf1 = null;
        }
        return result;
    }

    public static String convertTimeFormatIntoAmOrPmFormat(String openingTime) {
        String time = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = dateFormat.parse(openingTime);
            DateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
            time = simpleDateFormat.format(date).toLowerCase();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }


    public static int sportCheck(String sportId) {
        int sportImage = 0;
        switch (sportId) {
            case "1":
                sportImage = R.drawable.ic_badminton_white;
                break;
            case "2":
                sportImage = R.drawable.ic_basketball_white;
                break;
            case "3":
                sportImage = R.drawable.ic_carrom_white;
                break;
            case "4":
                sportImage = R.drawable.ic_cricket_white;
                break;
            case "5":
                sportImage = R.drawable.ic_football_white;
                break;
            case "6":
                sportImage = R.drawable.ic_tennis_white;
                break;
            case "7":
                sportImage = R.drawable.ic_volley_white;
                break;
        }
        return sportImage;
    }

    public static int scoreUpdateSport(String sportid) {
        int sportImage = 0;
        switch (sportid) {
            case "1":
                sportImage = R.drawable.ic_badminton_grey;
                break;
            case "2":
                sportImage = R.drawable.ic_basketball_grey;
                break;
            case "3":
                sportImage = R.drawable.ic_carrom_grey;
                break;
            case "4":
                sportImage = R.drawable.ic_cricket_grey;
                break;
            case "5":
                sportImage = R.drawable.ic_football_gray;
                break;
            case "6":
                sportImage = R.drawable.ic_tennis_grey;
                break;
            case "7":
                sportImage = R.drawable.ic_volley_grey;
                break;
        }
        return sportImage;
    }

    public static String sportNameCheck(String sportid) {
        String sportname = "";
        switch (sportid) {
            case "1":
                sportname = "Badminton";
                break;
            case "2":
                sportname = "Basketball";
                break;
            case "3":
                sportname = "Carrom";
                break;
            case "4":
                sportname = "Cricket";
                break;
            case "5":
                sportname = "Football";
                break;
            case "6":
                sportname = "Tennis";
                break;
            case "7":
                sportname = "Volleyball";
                break;
        }
        return sportname;
    }

    public static int sportIdCheck(String sportName) {
        int sportId = 0;
        switch (sportName) {
            case "Badminton":
                sportId = 1;
                break;
            case "Basketball":
                sportId = 2;
                break;
            case "Carrom":
                sportId = 3;
                break;
            case "Cricket":
                sportId = 4;
                break;
            case "Football":
                sportId = 5;
                break;
            case "Tennis":
                sportId = 6;
                break;
            case "Volleyball":
                sportId = 7;
                break;
        }
        return sportId;
    }

    public static int batchCheck(String batch_id) {
        int batchimage = 0;
        switch (batch_id) {
            case "1":
                batchimage = R.drawable.badge1;
                break;
            case "2":
                batchimage = R.drawable.badge2;
                break;
            case "3":
                batchimage = R.drawable.badge3;
                break;
            case "4":
                batchimage = R.drawable.badge4;
                break;
            case "5":
                batchimage = R.drawable.badge5;
                break;
        }
        return batchimage;
    }

    public static String MatchType(String matchtypeid) {
        String matchtype = "";
        switch (matchtypeid) {
            case "1":
                matchtype = "Street";
                break;
            case "2":
                matchtype = "League";
                break;
            case "3":
                matchtype = "Tournament";
                break;
        }
        return matchtype;
    }

    public static String timeconverter(String createdate) {
        String timeago = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date past = format.parse(createdate);
            Date now = new Date();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if (seconds < 60) {
                String subadd = seconds > 1 ? "seconds ago" : "second ago";
                timeago = seconds + " " + subadd;
            } else if (minutes < 60) {
                String subadd = minutes > 1 ? "minutes ago" : "minute ago";
                timeago = minutes + " " + subadd;
            } else if (hours < 24) {
                String subadd = hours > 1 ? "hours ago" : "hour ago";
                timeago = hours + " " + subadd;
            } else {
                String subadd = days > 1 ? "days ago" : "day ago";
                timeago = days + " " + subadd;
            }

        } catch (Exception j) {
            j.printStackTrace();
        }
        return timeago;
    }

    public static void locationdialog(final Context mcontext, final TextView text, final int status) {

        typeface_regular = Typeface.createFromAsset(mcontext.getAssets(), "fonts/roboto_regular.ttf");
        preferenceHelper = new PreferenceHelper(mcontext, Constants.APP_NAME, 0);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) mcontext).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final Dialog dialog = new Dialog(mcontext, android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        assert window != null;
        window.setGravity(Gravity.CENTER);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.location_layout_dialogue);
        LinearLayout location_container = (LinearLayout) dialog.findViewById(R.id.location_container);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) (metrics.widthPixels * 0.9);
        params.gravity = Gravity.CENTER;
        location_container.setLayoutParams(params);

        locationdata = (RecyclerView) dialog.findViewById(R.id.locationdata);
        location_edit = (EditText) dialog.findViewById(R.id.activity_explore_player_list_et_location_search);
        location_progress = (CircularProgressView) dialog.findViewById(R.id.location_progress);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mcontext);
        locationdata.setLayoutManager(mLayoutManager);

        locationdata.addOnItemTouchListener(new RecyclerItemClickListener(mcontext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                InputMethodManager imm = (InputMethodManager) mcontext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                LocationModel locationModel = (LocationModel) locationarrayList.get(position);
                text.setText(locationModel.getLocationname());
                text.setFocusable(false);
                text.setError(null);
                if (status == 2) {
                    locationid = locationModel.getLocation_id();
                    preferenceHelper.save("user_location", locationid);
                } else if (status == 1) {
                    cityid = locationModel.getLocation_id();
                    preferenceHelper.save("user_city", locationid);
                } else if (status == 3) {
                    locationid = locationModel.getLocation_id();
                    preferenceHelper.save("user_location", locationid);
                }
//                preferenceHelper.save("user_location_name", locationModel.getLocationname());
            }
        }));

        location_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = location_edit.getText().toString().toLowerCase(Locale.getDefault());
                if (null != locationadapter) locationadapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });
        if (status == 2) {
            location_servercall(mcontext, status);
        } else if (status == 1) {
            city_servercall(mcontext);
        } else if (status == 3) {
            location_servercall(mcontext, status);
        }
        dialog.show();
    }

    private static void city_servercall(final Context mcontext) {
        location_progress.setVisibility(View.VISIBLE);
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call = apiInterface.getCity(auth_token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        if (locationarrayList.size() != 0) {
                            locationarrayList.clear();
                        }
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        if (locationarrayList.size() != 0) {
                            locationarrayList.clear();
                        }
                        JSONArray array = new JSONArray(json.getString("data"));
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {
                                LocationModel m = new LocationModel();
                                m.setLocationname(array.getJSONObject(i).getString("city_name"));
                                m.setLocation_id(array.getJSONObject(i).getString("city_id"));
                                locationarrayList.add(m);
                            }
                        } else {
                            String message = json.getString("error");
                            Toast.makeText(mcontext, message, Toast.LENGTH_SHORT).show();
                        }

                        locationadapter = new LocationAdapter(mcontext, locationarrayList);
                        locationdata.setAdapter(locationadapter);
                    } else {
                        if (response.body() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(mcontext, msg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(mcontext, R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(mcontext, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                location_progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                location_progress.setVisibility(View.GONE);
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(mcontext, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mcontext, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void location_servercall(final Context mcontext, int status) {
        location_progress.setVisibility(View.VISIBLE);
        String auth_token = "Bearer " + preferenceHelper.getString("user_token", "");
        Call<ResponseBody> call;
        if (status == 3) {
            call = apiInterface.getLocation(auth_token);
        } else {
            call = apiInterface.getLocationByCity(auth_token, cityid);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null && response.code() == 200) {
                        if (locationarrayList.size() != 0) {
                            locationarrayList.clear();
                        }
                        String data = response.body().string();
                        Log.d("RESPONSE", data);
                        JSONObject json = new JSONObject(data);
                        if (locationarrayList.size() != 0) {
                            locationarrayList.clear();
                        }
                        JSONArray array = new JSONArray(json.getString("data"));
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {
                                LocationModel m = new LocationModel();
                                m.setLocationname(array.getJSONObject(i).getString("location_name"));
                                m.setLocation_id(array.getJSONObject(i).getString("location_id"));
                                locationarrayList.add(m);
                            }
                        } else {
                            String message = json.getString("error");
                            Toast.makeText(mcontext, message, Toast.LENGTH_SHORT).show();
                        }

                        locationadapter = new LocationAdapter(mcontext, locationarrayList);
                        locationdata.setAdapter(locationadapter);
                    } else {
                        if (response.body() != null) {
                            try {
                                String error = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(error);
                                String msg = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (!code.equals("500")) {
                                    Toast.makeText(mcontext, msg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String error = response.message();
                                Toast.makeText(mcontext, R.string.error500, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String error = response.message();
                            Toast.makeText(mcontext, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                location_progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  Log error here since request failed
                location_progress.setVisibility(View.GONE);
                try {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(mcontext, R.string.timeout, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mcontext, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean validate(String playerHeight) {
        if (playerHeight.length() > 0) {
            if (!playerHeight.equals("0") && Integer.valueOf(playerHeight) < 120 || Integer.valueOf(playerHeight) > 245) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    public static void ErrorHandleMethod(FragmentActivity activity, Response<ResponseBody> response) {
        if (response.code() != 200) {
            if (response.errorBody() != null) {
                try {
                    String error = response.errorBody().string();
                    JSONObject jsonObject = new JSONObject(error);
                    String msg = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (!code.equals("500")) {
                        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, R.string.error500, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String error = response.message();
                    Toast.makeText(activity, R.string.error500, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                String error = response.message();
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void ServerFailureHandleMethod(FragmentActivity activity, Throwable t) {
        try {
            if (t instanceof SocketTimeoutException) {
                Toast.makeText(activity, R.string.timeout, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
