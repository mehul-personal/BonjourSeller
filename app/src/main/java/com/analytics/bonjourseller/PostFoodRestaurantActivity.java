package com.analytics.bonjourseller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by MeHuL on 17-03-2016.
 */
public class PostFoodRestaurantActivity extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final String TAG = "PostFoodRestaurantActiv";
    static ArrayList<String> UploadImageList, UploadImageCallList, FoodId, FoodName, FOODTYPECHECKLIST, FOODTITLELIST;
    static String strFoodType = "", str_type_of_food = "", str_establish_type = "", offer_id = "",
            name = "", address = "", type_of_food = "", food_type = "", latitude = "", longitude = "",
            establish_type = "", offer_status = "", CALLTYPE = "";
    static ArrayList<String> ImageList;
    RadioButton rbVeg, rbNonVeg, rbBoth;
    Button esbRestaurant, esbQuickBite, esbBakery, esbCafe, esbDining, esbBeverage, pickLocation, uploadImage, submitFoodOffer;
    boolean chkRestaurant = false, chkQuickBite = false, chkBakery = false, chkCafe = false, chkDining = false, chkBeverage = false;
    EditText edtBrandname, edtLatitude, edtLongitude, edtAddress;
    TextView txvSelectTypeOfFood, txvSelectType, txvTypeOfEstablishment, txvTandC;
    ExpandableHeightGridView gvFoodType, gvUploadImage;
    CheckBox chbTandC;
    UploadImageListAdapter ImageAdapter;
    private Uri mFileUri;

    public static Uri getOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "BonjourSeller");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "could not create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            String imageStoragePath = mediaStorageDir + " Images/";
            createDirectory(imageStoragePath);
            mediaFile = new File(imageStoragePath + "IMG" + timeStamp + ".jpg");

        } else if (type == 2) {
            String videoStoragePath = mediaStorageDir + " Videos/";
            createDirectory(videoStoragePath);
            mediaFile = new File(videoStoragePath + "VID" + timeStamp + ".mp4");

        } else {
            return null;
        }
        return Uri.fromFile(mediaFile);
    }

    public static void createDirectory(String filePath) {
        if (!new File(filePath).exists()) {
            new File(filePath).mkdirs();
        }
    }

    public static String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }

        }// MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_food_offer);
        rbVeg = (RadioButton) findViewById(R.id.rbVeg);
        rbNonVeg = (RadioButton) findViewById(R.id.rbNonVeg);
        rbBoth = (RadioButton) findViewById(R.id.rbBoth);
        esbRestaurant = (Button) findViewById(R.id.btnRestaurant);
        esbQuickBite = (Button) findViewById(R.id.btnQuickBite);
        esbBakery = (Button) findViewById(R.id.btnBakery);
        esbCafe = (Button) findViewById(R.id.btnCafe);
        esbDining = (Button) findViewById(R.id.btnDining);
        esbBeverage = (Button) findViewById(R.id.btnBeverage);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtBrandname = (EditText) findViewById(R.id.edtBrandName);
        gvFoodType = (ExpandableHeightGridView) findViewById(R.id.ehgTypeOfFood);
        gvFoodType.setExpanded(true);
        gvUploadImage = (ExpandableHeightGridView) findViewById(R.id.ehgUploadImage);
        gvUploadImage.setExpanded(true);
        txvSelectTypeOfFood = (TextView) findViewById(R.id.txvSelectTypeOfFood);
        txvSelectType = (TextView) findViewById(R.id.txvSelectType);
        txvTypeOfEstablishment = (TextView) findViewById(R.id.txvTypeOfEstablishment);
        pickLocation = (Button) findViewById(R.id.btnPickLocation);
        uploadImage = (Button) findViewById(R.id.btnUploadImage);
        edtLatitude = (EditText) findViewById(R.id.edtLatitude);
        edtLongitude = (EditText) findViewById(R.id.edtLongitude);
        txvTandC = (TextView) findViewById(R.id.txvTermsAndCondition);
        chbTandC = (CheckBox) findViewById(R.id.chbTermsAndCondition);
        submitFoodOffer = (Button) findViewById(R.id.btnSubmitFoodOffer);
        ImageList = new ArrayList<String>();


        Intent i = getIntent();
        CALLTYPE = i.getStringExtra("TYPE");
        if (i.getStringExtra("TYPE").equalsIgnoreCase("EDIT")) {
            name = i.getStringExtra("NAME");
            address = i.getStringExtra("ADDRESS");
            type_of_food = i.getStringExtra("TYPE_OF_FOOD");
            food_type = i.getStringExtra("FOOD_TYPE");
            latitude = i.getStringExtra("LATITUDE");
            longitude = i.getStringExtra("LONGITUDE");
            establish_type = i.getStringExtra("ESTABLISH_TYPE");
            offer_status = i.getStringExtra("OFFER_STATUS");
            offer_id = i.getStringExtra("OFFER_ID");
            ImageList = i.getStringArrayListExtra("IMAGE");

            edtBrandname.setText(name);
            edtLatitude.setText(latitude);
            edtLongitude.setText(longitude);
            edtAddress.setText(address);

            if (type_of_food.equalsIgnoreCase("Veg.")) {
                rbVeg.setChecked(true);
                rbNonVeg.setChecked(false);
                rbBoth.setChecked(false);
                str_type_of_food = "Veg.";
            }

            if (type_of_food.equalsIgnoreCase("Non Veg.")) {
                rbVeg.setChecked(false);
                rbNonVeg.setChecked(true);
                rbBoth.setChecked(false);
                str_type_of_food = "Non Veg.";
            }
            if (type_of_food.equalsIgnoreCase("Veg. / Non Veg.")) {
                rbVeg.setChecked(false);
                rbNonVeg.setChecked(false);
                rbBoth.setChecked(true);
                str_type_of_food = "Veg. / Non Veg.";
            }
            if (establish_type.equalsIgnoreCase("Restaurant")) {
                esbQuickBite.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBakery.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbCafe.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbDining.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBeverage.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbRestaurant.setBackgroundResource(R.drawable.set_add_offer_location_background);
                str_establish_type = "Restaurant";
            }
            if (establish_type.equalsIgnoreCase("QuickBite")) {
                esbRestaurant.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBakery.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbCafe.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbDining.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBeverage.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbQuickBite.setBackgroundResource(R.drawable.set_add_offer_location_background);
                str_establish_type = "QuickBite";
            }
            if (establish_type.equalsIgnoreCase("Bakery")) {
                esbRestaurant.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbQuickBite.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbCafe.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbDining.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBeverage.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBakery.setBackgroundResource(R.drawable.set_add_offer_location_background);
                str_establish_type = "Bakery";
            }
            if (establish_type.equalsIgnoreCase("Cafe")) {
                esbRestaurant.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbQuickBite.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBakery.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbDining.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBeverage.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbCafe.setBackgroundResource(R.drawable.set_add_offer_location_background);
                str_establish_type = "Cafe";
            }
            if (establish_type.equalsIgnoreCase("Dining")) {
                esbRestaurant.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbQuickBite.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBakery.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbCafe.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBeverage.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbDining.setBackgroundResource(R.drawable.set_add_offer_location_background);
                str_establish_type = "Dining";
            }
            if (establish_type.equalsIgnoreCase("Beverage")) {
                esbRestaurant.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbQuickBite.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBakery.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbCafe.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbDining.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBeverage.setBackgroundResource(R.drawable.set_add_offer_location_background);
                str_establish_type = "Beverage";
            }

        }
        UploadImageList = new ArrayList<String>();
        UploadImageCallList = new ArrayList<String>();
        UploadImageList = ImageList;
        for (int j = 0; j < UploadImageList.size(); j++) {
            UploadImageCallList.add("SERVER");
        }
        ImageAdapter = new UploadImageListAdapter(UploadImageList, UploadImageCallList);
        gvUploadImage.setAdapter(ImageAdapter);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Foods & Restaurant");

        esbRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esbQuickBite.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBakery.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbCafe.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbDining.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBeverage.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbRestaurant.setBackgroundResource(R.drawable.set_add_offer_location_background);
                str_establish_type = "Restaurant";

            }
        });
        esbQuickBite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esbRestaurant.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBakery.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbCafe.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbDining.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBeverage.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbQuickBite.setBackgroundResource(R.drawable.set_add_offer_location_background);
                str_establish_type = "QuickBite";

            }
        });
        esbBakery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esbRestaurant.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbQuickBite.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbCafe.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbDining.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBeverage.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBakery.setBackgroundResource(R.drawable.set_add_offer_location_background);
                str_establish_type = "Bakery";

            }
        });
        esbCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esbRestaurant.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbQuickBite.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBakery.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbDining.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBeverage.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbCafe.setBackgroundResource(R.drawable.set_add_offer_location_background);
                str_establish_type = "Cafe";
            }

        });
        esbDining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esbRestaurant.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbQuickBite.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBakery.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbCafe.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBeverage.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbDining.setBackgroundResource(R.drawable.set_add_offer_location_background);
                str_establish_type = "Dining";

            }
        });
        esbBeverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esbRestaurant.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbQuickBite.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBakery.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbCafe.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbDining.setBackgroundResource(R.drawable.set_add_offer_location_border);
                esbBeverage.setBackgroundResource(R.drawable.set_add_offer_location_background);
                str_establish_type = "Beverage";
            }
        });
        rbVeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbVeg.setChecked(true);
                rbNonVeg.setChecked(false);
                rbBoth.setChecked(false);
                str_type_of_food = "Veg.";
            }
        });
        rbNonVeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbVeg.setChecked(false);
                rbNonVeg.setChecked(true);
                rbBoth.setChecked(false);
                str_type_of_food = "Non Veg.";
            }
        });
        rbBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbVeg.setChecked(false);
                rbNonVeg.setChecked(false);
                rbBoth.setChecked(true);
                str_type_of_food = "Veg. / Non Veg.";
            }
        });
        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PostFoodRestaurantActivity.this, NavigationDrawerActivity.class);
                startActivityForResult(i, 15);
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo",
                        "Choose from Gallery", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        PostFoodRestaurantActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals("Take Photo")) {
                                    startCamera();
                                } else if (options[item]
                                        .equals("Choose from Gallery")) {
                                    startGallery();
                                } else if (options[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                            }
                        });
                builder.show();
            }
        });
        submitFoodOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < FOODTYPECHECKLIST.size(); i++) {
                    if (FOODTYPECHECKLIST.get(i).equalsIgnoreCase("true")) {
                        if (strFoodType.isEmpty()) {
                            strFoodType = FOODTITLELIST.get(i);
                        } else {
                            strFoodType = strFoodType + "," + FOODTITLELIST.get(i);
                        }
                    }
                }

                if (edtBrandname.getText().toString().isEmpty()) {
                    Toast.makeText(PostFoodRestaurantActivity.this, "Please enter Brand Name", Toast.LENGTH_LONG).show();
                } else if (edtAddress.getText().toString().isEmpty()) {
                    Toast.makeText(PostFoodRestaurantActivity.this, "Please enter Email Address", Toast.LENGTH_LONG).show();
                } else if (!rbVeg.isChecked() && !rbNonVeg.isChecked() && !rbBoth.isChecked()) {
                    Toast.makeText(PostFoodRestaurantActivity.this, "Please select Type of Food", Toast.LENGTH_LONG).show();
                } else if (strFoodType.isEmpty()) {
                    Toast.makeText(PostFoodRestaurantActivity.this, "Please select any Type", Toast.LENGTH_LONG).show();
                } else if (str_establish_type.isEmpty()) {
                    Toast.makeText(PostFoodRestaurantActivity.this, "Please select Type of Establishment", Toast.LENGTH_LONG).show();
                } else if (edtLatitude.getText().toString().isEmpty()) {
                    Toast.makeText(PostFoodRestaurantActivity.this, "Please enter Latitude", Toast.LENGTH_LONG).show();
                } else if (edtLongitude.getText().toString().isEmpty()) {
                    Toast.makeText(PostFoodRestaurantActivity.this, "Please enter Longitude", Toast.LENGTH_LONG).show();
                } else if (!chbTandC.isChecked()) {
                    Toast.makeText(PostFoodRestaurantActivity.this, "You must be agree with terms and condition before posting offer", Toast.LENGTH_LONG).show();
                } else {
                    addFoodRestaurant();
                }
            }
        });
        getFoodType();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == 15) {
                edtLatitude.setText(data.getStringExtra("LATITUDE"));
                edtLongitude.setText(data.getStringExtra("LONGITUDE"));
            }
        }
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {
                if (mFileUri != null) {
                    Log.d(TAG, "file: " + mFileUri);
                    UploadImageList.add(getRealPathFromURI(mFileUri));
                    UploadImageCallList.add("FILE");
                } else {
                    Log.d(TAG, "file is null");
                    if (data != null) {
                        Log.d(TAG, "data: " + data.toString());
                        String path = "";
                        try {
                            path = getPath(data.getData(), true);
                        } catch (Exception e) {
                            path = getRealPathFromURI(data.getData());
                        }
                        UploadImageList.add(path);
                        UploadImageCallList.add("FILE");
                    }
                }

            } else if (requestCode == REQUEST_GALLERY) {
                if (data != null && data.getData() != null) {
                    String path = "";
                    try {
                        path = getPath(data.getData(), true);
                    } catch (Exception e) {
                        path = getRealPathFromURI(data.getData());
                    }
                    Log.d(TAG, "path:" + path);
                    if (path != null) {
                        UploadImageList.add(path);
                        UploadImageCallList.add("FILE");
                    }
                }

            }
            ImageAdapter.notifyDataSetChanged();
        }
    }

    private void startCamera() {

        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFileUri = getOutputMediaFile(1);
        if (mFileUri != null) {
            intent1.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            startActivityForResult(intent1, REQUEST_CAMERA);
        } else {
            Log.e("image camera", "file not available");
        }

    }

    public String getPath(Uri uri, boolean isImage) {
        if (uri == null) {
            return null;
        }
        String[] projection;
        String coloumnName, selection;
        if (isImage) {
            selection = MediaStore.Images.Media._ID + "=?";
            coloumnName = MediaStore.Images.Media.DATA;
        } else {
            selection = MediaStore.Video.Media._ID + "=?";
            coloumnName = MediaStore.Video.Media.DATA;
        }
        projection = new String[]{coloumnName};
        Cursor cursor;
        if (Build.VERSION.SDK_INT > 19) {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            // where id is equal to
            if (isImage) {
                cursor = getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection,
                                new String[]{id}, null);
            } else {
                cursor = getContentResolver()
                        .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, new String[]{id},
                                null);
            }
        } else {
            cursor = getContentResolver().query(uri, projection, null, null, null);
        }
        String path = null;
        try {
            int column_index = cursor.getColumnIndex(coloumnName);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void startGallery() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);

    }


    public void getFoodType() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "all_food.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(PostFoodRestaurantActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("response", "" + response);
                        try {
                            FoodId = new ArrayList<String>();
                            FoodName = new ArrayList<String>();
                            FOODTYPECHECKLIST = new ArrayList<String>();

                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
                            Log.e("2", "2");

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                JSONArray dataArray = object.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject dataOb = dataArray.getJSONObject(i);
                                    FoodId.add(dataOb.getString("id"));
                                    FoodName.add(dataOb.getString("food_name"));


                                    if (food_type.contains(dataOb.getString("food_name"))) {
                                        FOODTYPECHECKLIST.add("true");

                                    } else {
                                        FOODTYPECHECKLIST.add("false");
                                    }
                                }
                                FoodTypeAdapter adapter = new FoodTypeAdapter(FoodId, FoodName);
                                gvFoodType.setAdapter(adapter);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Oopss! we are troubling to get food type",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(PostFoodRestaurantActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(PostFoodRestaurantActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();

                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(PostFoodRestaurantActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PostFoodRestaurantActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void addFoodRestaurant() {
        final String brandName = edtBrandname.getText().toString();
        final String str_latitude = edtLatitude.getText().toString();
        final String str_longitude = edtLongitude.getText().toString();
        final String str_address = edtAddress.getText().toString();
        new AsyncTask<Void, Void, String>() {
            ProgressDialog mProgressDialog;

            @Override
            protected String doInBackground(Void... params) {
                // TODO Auto-generated method stub
                SharedPreferences sharedPreferences = getSharedPreferences(
                        "LOGIN_DETAIL", 0);
                String userid = sharedPreferences.getString("USER_ID", "");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost;
                if (CALLTYPE.equalsIgnoreCase("EDIT")) {
                    httppost = new HttpPost(ApplicationData.serviceURL + "edit_restaurant.php");
                } else {
                    httppost = new HttpPost(ApplicationData.serviceURL + "add_restaurant.php");
                }
                try {

                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    int newimg = 1;
                    for (int i = 0; i < UploadImageList.size(); i++) {
                        if (UploadImageCallList.get(i).equals("FILE")) {
                            entity.addPart("image" + newimg, new FileBody(new File(UploadImageList.get(i))));
                            newimg++;
                        }
                    }
                    entity.addPart("image_count", new StringBody("" + (newimg - 1)));
                    entity.addPart("seller_id", new StringBody("" + userid));
                    entity.addPart("brand_name", new StringBody("" + brandName));
                    entity.addPart("type_of_food", new StringBody("" + str_type_of_food));
                    entity.addPart("establish_type", new StringBody("" + str_establish_type));
                    entity.addPart("latitude", new StringBody("" + str_latitude));
                    entity.addPart("longitude", new StringBody("" + str_longitude));
                    entity.addPart("food_type", new StringBody("" + strFoodType));
                    entity.addPart("address", new StringBody("" + str_address));
                    if (CALLTYPE.equalsIgnoreCase("EDIT")) {
                        entity.addPart("offer_id", new StringBody("" + offer_id));
                    }

                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(response.getEntity()
                                    .getContent()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    in.close();
                    Log.e("add_restaurant data", sb.toString());
                    return sb.toString();

                } catch (Exception e) {
                    Log.e("add_restaurant problem", "" + e);
                    return "";
                }

            }

            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                try {
                    mProgressDialog.dismiss();
                    JSONObject object = new JSONObject(result.toString());
                    Log.e("2", "2");

                    String msg = object.getString("msg");
                    if (msg.equalsIgnoreCase("Success")) {
                        if (CALLTYPE.equalsIgnoreCase("EDIT")) {
                            Toast.makeText(PostFoodRestaurantActivity.this,
                                    "Your Food offer updated successfully!",
                                    Toast.LENGTH_LONG).show();
                            Intent i = new Intent();
                            i.putExtra("msg", "success");
                            setResult(38, i);
                            finish();
                        } else {
                            Toast.makeText(PostFoodRestaurantActivity.this,
                                    "Your Food offer added successfully!",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(PostFoodRestaurantActivity.this,
                                "Oopss! We are troubling to send data",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                    mProgressDialog.dismiss();
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(PostFoodRestaurantActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PostFoodRestaurantActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(PostFoodRestaurantActivity.this);
                mProgressDialog.setTitle("");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setMessage("Please Wait...");
                mProgressDialog.show();

            }
        }.execute();
    }

    public class FoodTypeAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<String> FoodTitleId;

        public FoodTypeAdapter(ArrayList<String> foodid,
                               ArrayList<String> title) {
            this.FoodTitleId = foodid;
            FOODTITLELIST = title;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        @Override
        public int getCount() {
            return FOODTITLELIST.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_merchant_category, null);
                holder = new ViewHolder();
                holder.categoryName = (TextView) convertView.findViewById(R.id.txvMerchantCategory);
                holder.categoryCheckbox = (CheckBox) convertView.findViewById(R.id.chbMerchantCategory);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.categoryName.setText(FOODTITLELIST.get(position));
            holder.categoryCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FOODTYPECHECKLIST.get(position).equalsIgnoreCase("false")) {
                        FOODTYPECHECKLIST.set(position, "true");
                    } else {
                        FOODTYPECHECKLIST.set(position, "false");
                    }
                }
            });
            if (FOODTYPECHECKLIST.get(position).equalsIgnoreCase("false")) {
                holder.categoryCheckbox.setChecked(false);
            } else {
                holder.categoryCheckbox.setChecked(true);
            }
            return convertView;
        }

        class ViewHolder {
            TextView categoryName;
            CheckBox categoryCheckbox;
        }

    }

    public class UploadImageListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<String> ImageList, CallList;

        public UploadImageListAdapter(ArrayList<String> imagelist, ArrayList<String> CallList) {
            ImageList = imagelist;
            this.CallList = CallList;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return ImageList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_gridview_image, null);
                holder = new ViewHolder();
                holder.imageGridview = (ImageView) convertView.findViewById(R.id.imvGridImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                if (CallList.get(position).equalsIgnoreCase("SERVER")) {
                    Picasso.with(PostFoodRestaurantActivity.this)
                            .load(ImageList.get(position))
                            .into(holder.imageGridview);
                } else if (CallList.get(position).equalsIgnoreCase("FILE")) {
                    Picasso.with(PostFoodRestaurantActivity.this)
                            .load(new File(ImageList.get(position)))
                            .into(holder.imageGridview);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        class ViewHolder {
            ImageView imageGridview;
        }

    }


}
