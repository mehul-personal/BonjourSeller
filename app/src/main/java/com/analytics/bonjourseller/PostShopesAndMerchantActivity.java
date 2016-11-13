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

public class PostShopesAndMerchantActivity extends AppCompatActivity {

    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final String TAG = "PostShopesAndMerchant";
    static ArrayList<String> CategoryId, CategoryName, CHECKLISTADAPTER, CATEGORYIDADAPTER;
    static String strSelectedCategoryID = "";
    static String name = "", address = "", description = "", latitude = "", longitude = "",
            category_name = "", offer_status = "", offer_id = "", CALL_TYPE = "";
    static ArrayList<String> UploadImageList, UploadImageCallList;
    static ArrayList<String> ImageList;
    ExpandableHeightGridView gvCategory, ehgUploadImage;
    TextView txvSelectOneOrMoreCategory, txvShopesTermsAndCondition;
    EditText edtOtherCategory, edtShopesBrandName, edtShopesAddress, edtShopesDescription,
            edtShopesLatitude, edtShopesLongitude;
    Button btnOtherCategory, btnShopesUploadImage, btnShopesTapLocation, btnShopesSubmit;
    CheckBox chbShopesTermsAndCondition;
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
        setContentView(R.layout.activity_post_shopes_and_merchant);
        gvCategory = (ExpandableHeightGridView) findViewById(R.id.gvCategoryView);
        gvCategory.setExpanded(true);
        ehgUploadImage = (ExpandableHeightGridView) findViewById(R.id.ehgUploadImage);
        ehgUploadImage.setExpanded(true);
        txvSelectOneOrMoreCategory = (TextView) findViewById(R.id.txvSelectOneOrMoreCategory);
        txvShopesTermsAndCondition = (TextView) findViewById(R.id.txvShopesTermsAndCondition);
        edtOtherCategory = (EditText) findViewById(R.id.edtOtherCategory);
        edtShopesBrandName = (EditText) findViewById(R.id.edtShopesBrandName);
        edtShopesAddress = (EditText) findViewById(R.id.edtShopesAddress);
        edtShopesDescription = (EditText) findViewById(R.id.edtShopesDescription);
        edtShopesLatitude = (EditText) findViewById(R.id.edtShopesLatitude);
        edtShopesLongitude = (EditText) findViewById(R.id.edtShopesLongitude);
        btnOtherCategory = (Button) findViewById(R.id.btnOtherCategory);
        btnShopesUploadImage = (Button) findViewById(R.id.btnShopesUploadImage);
        btnShopesTapLocation = (Button) findViewById(R.id.btnShopesTapLocation);
        btnShopesSubmit = (Button) findViewById(R.id.btnShopesSubmit);
        chbShopesTermsAndCondition = (CheckBox) findViewById(R.id.chbShopesTermsAndCondition);
        UploadImageList = new ArrayList<String>();
        ImageList = new ArrayList<String>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Shopes & Merchant");
        Intent i = getIntent();
        CALL_TYPE = i.getStringExtra("TYPE");
        if (i.getStringExtra("TYPE").equalsIgnoreCase("EDIT")) {
            name = i.getStringExtra("NAME");
            address = i.getStringExtra("ADDRESS");
            description = i.getStringExtra("DESCRIPTION");
            latitude = i.getStringExtra("LATITUDE");
            longitude = i.getStringExtra("LONGITUDE");
            category_name = i.getStringExtra("CATEGORY_NAME");
            offer_status = i.getStringExtra("OFFER_STATUS");
            ImageList = i.getStringArrayListExtra("IMAGE");
            offer_id = i.getStringExtra("OFFER_ID");

            edtShopesBrandName.setText(name);
            edtShopesAddress.setText(address);
            edtShopesDescription.setText(description);
            edtShopesLatitude.setText(latitude);
            edtShopesLongitude.setText(longitude);


        }

        UploadImageList = new ArrayList<String>();
        UploadImageCallList = new ArrayList<String>();
        UploadImageList = ImageList;
        for (int j = 0; j < UploadImageList.size(); j++) {
            UploadImageCallList.add("SERVER");
        }
        ImageAdapter = new UploadImageListAdapter(UploadImageList, UploadImageCallList);
        ehgUploadImage.setAdapter(ImageAdapter);

        getShopeAndMerchantiseCategory();

        btnShopesSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShopesAndMerchantOffer();
            }
        });
        btnShopesTapLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PostShopesAndMerchantActivity.this, NavigationDrawerActivity.class);
                startActivityForResult(i, 15);
            }
        });

        btnShopesUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo",
                        "Choose from Gallery", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        PostShopesAndMerchantActivity.this);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == 15) {
                edtShopesLatitude.setText(data.getStringExtra("LATITUDE"));
                edtShopesLongitude.setText(data.getStringExtra("LONGITUDE"));
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

    public void addShopesAndMerchantOffer() {
        final String strBrandName = edtShopesBrandName.getText().toString();
        final String strShopeAddress = edtShopesAddress.getText().toString();
        final String strShopeDescription = edtShopesDescription.getText().toString();
        final String strLatitude = edtShopesLatitude.getText().toString();
        final String strLongitude = edtShopesLongitude.getText().toString();
        final String strOtherCategory = edtOtherCategory.getText().toString();
        strSelectedCategoryID="";
        for (int i = 0; i < CHECKLISTADAPTER.size(); i++) {
            if (CHECKLISTADAPTER.get(i).equalsIgnoreCase("true")) {
                if (strSelectedCategoryID.isEmpty()) {
                    strSelectedCategoryID = CATEGORYIDADAPTER.get(i);
                } else {
                    strSelectedCategoryID = strSelectedCategoryID + "," + CATEGORYIDADAPTER.get(i);
                }
            }
        }
        Log.e("strSelectedCategoryID:",""+strSelectedCategoryID);
        if (strSelectedCategoryID.isEmpty() && strOtherCategory.isEmpty()) {
            Toast.makeText(PostShopesAndMerchantActivity.this, "Please select Category!", Toast.LENGTH_LONG).show();
        } else if (strBrandName.isEmpty()) {
            Toast.makeText(PostShopesAndMerchantActivity.this, "Please enter Brand Name", Toast.LENGTH_LONG).show();
        } else if (strShopeAddress.isEmpty()) {
            Toast.makeText(PostShopesAndMerchantActivity.this, "Please enter Shop Address", Toast.LENGTH_LONG).show();
        } else if (strShopeDescription.isEmpty()) {
            Toast.makeText(PostShopesAndMerchantActivity.this, "Please enter Shop Description", Toast.LENGTH_LONG).show();
        } else if (strLatitude.isEmpty()) {
            Toast.makeText(PostShopesAndMerchantActivity.this, "Please enter Shop Latitude", Toast.LENGTH_LONG).show();
        } else if (strLongitude.isEmpty()) {
            Toast.makeText(PostShopesAndMerchantActivity.this, "Please enter Shop Longitude", Toast.LENGTH_LONG).show();
        } else if (!chbShopesTermsAndCondition.isChecked()) {
            Toast.makeText(PostShopesAndMerchantActivity.this, "You must be agree with terms and condition before posting Merchant Offer", Toast.LENGTH_LONG).show();

        } else {
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
                    if (CALL_TYPE.equalsIgnoreCase("EDIT")) {
                        httppost = new HttpPost(ApplicationData.serviceURL + "edit_merchant.php");
                    } else {
                        httppost = new HttpPost(ApplicationData.serviceURL + "add_merchant.php");
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
                        entity.addPart("image_count", new StringBody("" + (newimg-1)));
                        if (CALL_TYPE.equalsIgnoreCase("EDIT")) {
                            entity.addPart("offer_id", new StringBody("" + offer_id));
                        }
                        entity.addPart("seller_id", new StringBody("" + userid));
                        entity.addPart("brand_name", new StringBody("" + strBrandName));
                        entity.addPart("address", new StringBody("" + strShopeAddress));
                        entity.addPart("description", new StringBody("" + strShopeDescription));
                        entity.addPart("latitude", new StringBody("" + strLatitude));
                        entity.addPart("longitude", new StringBody("" + strLongitude));
                        entity.addPart("category_id", new StringBody("" + strSelectedCategoryID));
                        entity.addPart("other_category", new StringBody("" + strOtherCategory));

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
                        Log.e("add_merchant data", sb.toString());
                        return sb.toString();

                    } catch (Exception e) {
                        Log.e("add_merchant problem", "" + e);
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
                            if (CALL_TYPE.equalsIgnoreCase("EDIT")) {
                                Toast.makeText(PostShopesAndMerchantActivity.this,
                                        "Your Shops and Merchant offer updated successfully!",
                                        Toast.LENGTH_LONG).show();
                                Intent i=new Intent();
                                i.putExtra("msg","success");
                                setResult(37,i);
                                finish();
                            } else {
                                Toast.makeText(PostShopesAndMerchantActivity.this,
                                        "Your Shops and Merchant offer added successfully!",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(PostShopesAndMerchantActivity.this,
                                    "Oopss! We are troubling to send data",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception error) {
                        error.printStackTrace();
                        mProgressDialog.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(PostShopesAndMerchantActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(PostShopesAndMerchantActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                protected void onPreExecute() {
                    // TODO Auto-generated method stub
                    super.onPreExecute();
                    mProgressDialog = new ProgressDialog(PostShopesAndMerchantActivity.this);
                    mProgressDialog.setTitle("");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setMessage("Please Wait...");
                    mProgressDialog.show();

                }
            }.execute();
        }
    }

    public void getShopeAndMerchantiseCategory() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "shop_category.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(PostShopesAndMerchantActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("forward_offer response", "" + response);
                        try {
                            mProgressDialog.dismiss();
                            CategoryId = new ArrayList<String>();
                            CategoryName = new ArrayList<String>();
                            CHECKLISTADAPTER = new ArrayList<String>();
                            JSONObject object = new JSONObject(response.toString());

                            String msg = object.getString("msg");

                            if (msg.equalsIgnoreCase("Success")) {
                                JSONArray datarray = object.getJSONArray("data");
                                // Log.e("array length", "" + datarray.length());
                                boolean otherCategory = false;
                                for (int i = 0; i < datarray.length(); i++) {
                                    JSONObject dataOb = datarray.getJSONObject(i);
                                    CategoryId.add(dataOb.getString("id"));
                                    CategoryName.add(dataOb.getString("category_name").replace(",", ""));
                                    if (category_name.contains(dataOb.getString("category_name").replace(",", ""))) {
                                        CHECKLISTADAPTER.add("true");
                                        otherCategory = true;
                                    } else {
                                        CHECKLISTADAPTER.add("false");
                                    }
                                }
                                if (!otherCategory) {
                                    edtOtherCategory.setText(category_name.replace(",", ""));
                                }
                                CategoryAdapter adapter = new CategoryAdapter(CategoryId, CategoryName);
                                gvCategory.setAdapter(adapter);
                            } else {
                                Toast.makeText(PostShopesAndMerchantActivity.this,
                                        "Sorry! we can't get your messages. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(PostShopesAndMerchantActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(PostShopesAndMerchantActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(PostShopesAndMerchantActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PostShopesAndMerchantActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);


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
                    Picasso.with(PostShopesAndMerchantActivity.this)
                            .load(ImageList.get(position))
                            .into(holder.imageGridview);
                } else if (CallList.get(position).equalsIgnoreCase("FILE")) {
                    Picasso.with(PostShopesAndMerchantActivity.this)
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

    public class CategoryAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<String> TitleList;

        public CategoryAdapter(ArrayList<String> categoryId,
                               ArrayList<String> title) {
            CATEGORYIDADAPTER = categoryId;
            TitleList = title;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return TitleList.size();
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
            holder.categoryName.setText(TitleList.get(position));
            holder.categoryCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CHECKLISTADAPTER.get(position).equalsIgnoreCase("false")) {
                        CHECKLISTADAPTER.set(position, "true");
                    } else {
                        CHECKLISTADAPTER.set(position, "false");
                    }
                }
            });
            if (CHECKLISTADAPTER.get(position).equalsIgnoreCase("false")) {
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
}
