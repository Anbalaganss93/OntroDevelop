package com.ontro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ontro.fragments.PersonalDetailsFragment;
import com.ontro.fragments.SportsFragment;
import com.ontro.utils.CommonUtils;
import com.ontro.utils.PreferenceHelper;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileCompletionActivity extends AppCompatActivity implements View.OnClickListener {
    public static String img_str = "";
    public ImageView image1;
    public ImageView image2;
    public RoundRectCornerImageView profile_upload;
    public TextView mid_line;
    Bitmap bm;
    private PreferenceHelper preferenceHelper;
    private DisplayMetrics dm;
    private FrameLayout profileFrameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        overridePendingTransition(R.anim.slide_in_right, R.anim.pause);
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        preferenceHelper = new PreferenceHelper(ProfileCompletionActivity.this, Constants.APP_NAME, 0);
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        profile_upload = (RoundRectCornerImageView) findViewById(R.id.profile_upload);
        mid_line = (TextView) findViewById(R.id.mid_line);
        FrameLayout profiledata = (FrameLayout) findViewById(R.id.profiledata);
        profileFrameLayout = (FrameLayout) findViewById(R.id.profile_container);
        profileFrameLayout.setVisibility(View.VISIBLE);

        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params2.width = (int) (dm.widthPixels * 0.2);
        params2.height = ((int) (dm.widthPixels * 0.2));
        profile_upload.setLayoutParams(params2);

        img_str = "";
        profile_upload.setOnClickListener(this);

        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment personalDetailsFragment = PersonalDetailsFragment.newInstance(getIntent().getStringExtra(Constants.BundleKeys.PROFILE_COMPLETION));
            fragmentTransaction.add(R.id.profiledata, personalDetailsFragment, "personaldetails");
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        profiledata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        SportsFragment sport_fragment = (SportsFragment) getSupportFragmentManager().findFragmentByTag("sports");
        PersonalDetailsFragment personalDetails_fragment = (PersonalDetailsFragment) getSupportFragmentManager().findFragmentByTag("personaldetails");

        if (sport_fragment != null && sport_fragment.isVisible()) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                
                getSupportFragmentManager().popBackStack();
                image1.setImageResource(R.drawable.s1);
                image2.setImageResource(R.drawable.s4);

                FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                params2.width = (int) (dm.widthPixels * 0.2);
                params2.height = ((int) (dm.heightPixels * 0.1));
                params2.gravity = Gravity.CENTER_VERTICAL;
                image1.setLayoutParams(params2);
                profileFrameLayout.setVisibility(View.VISIBLE);
                
                mid_line.setBackgroundColor(Color.parseColor("#283138"));
                FrameLayout.LayoutParams mid_line_param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                mid_line_param.width = (int) (dm.widthPixels * 0.18);
                mid_line_param.height = 3;
                mid_line_param.gravity = Gravity.CENTER_VERTICAL;
                mid_line_param.leftMargin = (int) (dm.widthPixels * 0.18);
                mid_line.setLayoutParams(mid_line_param);


                FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                params3.width = (int) (dm.widthPixels * 0.1);
                params3.height = ((int) (dm.heightPixels * 0.05));
                params3.gravity = Gravity.CENTER_VERTICAL;
                params3.leftMargin = (int) (dm.widthPixels * 0.35);
                image2.setLayoutParams(params3);
            }
        }
        preferenceHelper.save("user_location_name", "");
        if (personalDetails_fragment != null && personalDetails_fragment.isVisible()) {
            String profileCompletion = getIntent().getStringExtra(Constants.BundleKeys.PROFILE_COMPLETION);
            if (profileCompletion == null) {
                super.onBackPressed();
            } else if (profileCompletion != null && profileCompletion.equals(Constants.Messages.SIGN_UP)) {
                super.onBackPressed();
            } else {
                CommonUtils.locationid = "";
                Intent intent = new Intent(this, PlayerProfileActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onClick(View view) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {
            // Do something for lollipop and above versions
            if (ContextCompat.checkSelfPermission(ProfileCompletionActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ProfileCompletionActivity.this, new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.CommonKeys.MY_PERMISSIONS_REQUEST);
            } else {
                selectImage();
            }
        } else {
            selectImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(ProfileCompletionActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    // Permission granted
                    selectImage();
                }else{
                    Toast.makeText(ProfileCompletionActivity.this, "Permission was was was denied", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProfileCompletionActivity.this, "Permission was denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode ==  Constants.CommonKeys.SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode ==  Constants.CommonKeys.REQUEST_CAMERA)
                onCaptureImageResult(data);
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                Glide.with(this).load(resultUri).placeholder(R.drawable.profiledefaultimg).dontAnimate().into(profile_upload);
                profile_upload.setImageBitmap(bm);
                base64(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(getApplicationContext(), String.valueOf(cropError), Toast.LENGTH_SHORT).show();
        }
    }

    private void onCaptureImageResult(Intent data) {
        bm = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File f = new File(String.valueOf(destination));
        Uri yourUri = Uri.fromFile(f);
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setMaxScaleMultiplier(5);
        options.setCropGridColor(ContextCompat.getColor(this, R.color.colorAccent));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.toolbar_color));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.white));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        UCrop.of(yourUri, Uri.fromFile(destination))
                .withOptions(options)
                .start(this);
        base64(bm);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setMaxScaleMultiplier(5);
        options.setCropGridColor(ContextCompat.getColor(this, R.color.colorAccent));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.toolbar_color));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.white));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        UCrop.of(selectedImageUri, Uri.fromFile(destination)).withOptions(options).start(this);
    }

    public String base64(Bitmap bm) {
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.images);
        if(preferenceHelper.contains("user_profilepic")) {
            preferenceHelper.remove("user_profilepic");
        }
        if (bm != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byte[] image = stream.toByteArray();
            System.out.println("byte array:" + image);
            img_str = Base64.encodeToString(image, 0);
            Log.d("IMG", img_str);
            return img_str;
        } else {
            Toast.makeText(getApplicationContext(), "Unable to get convert image", Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    private void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileCompletionActivity.this);
        builder.setTitle("Choose Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,  Constants.CommonKeys.REQUEST_CAMERA);
                } else if (items[item].equals("Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select File"),  Constants.CommonKeys.SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}
