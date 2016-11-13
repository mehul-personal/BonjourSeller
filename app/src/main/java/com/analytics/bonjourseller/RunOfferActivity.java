package com.analytics.bonjourseller;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RunOfferActivity extends AppCompatActivity {

    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final String TAG = "RunOfferActivity";
    public static String Day = "", Month = "", Year = "", Minit = "", Hours = "", getDtae = "", getTime = "";
    ExpandableHeightGridView ehgDisplayUploadedImage;
    TextView txvDisplayRunOfferHeader, txvVisibleItems, txvAreaOfRadius, txvKM;
    EditText edtNoOfPeople, edtAddttionalDescription;
    Button btnTextOnly, btnImageOnly, btnTextImageOnly, btnUploadedMoreImage, btnRunOffer;
    Spinner spnArea;
    String selectedItem = "";
    String offer_id = "", category = "";
    ArrayList<String> UploadImageList;
    UploagImageListAdapter ImageAdapter;
    String selected_category_id = "";
    ImageView imvTimePicker;
    Dialog DateTimeDialog;
    String selecteddate = "", selectedtime = "";
    TextView txvSetDate;
    Button btnDialogCancle, btnDialogOk;
    DatePicker dpDatePicker;
    TimePicker tpTimePicker;
    EditText offerExpire;
    String visibleitem = "";
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
        setContentView(R.layout.activity_run_offer);
        ehgDisplayUploadedImage = (ExpandableHeightGridView) findViewById(R.id.ehgDisplayUploadedImage);
        ehgDisplayUploadedImage.setExpanded(true);
        txvDisplayRunOfferHeader = (TextView) findViewById(R.id.txvDisplayRunOfferHeader);
        txvVisibleItems = (TextView) findViewById(R.id.txvVisibleItems);
        txvAreaOfRadius = (TextView) findViewById(R.id.txvAreaOfRadius);
        txvKM = (TextView) findViewById(R.id.txvKM);
        edtNoOfPeople = (EditText) findViewById(R.id.edtNoOfPeople);
        edtAddttionalDescription = (EditText) findViewById(R.id.edtAddttionalDescription);
        btnTextOnly = (Button) findViewById(R.id.btnTextOnly);
        btnImageOnly = (Button) findViewById(R.id.btnImageOnly);
        btnTextImageOnly = (Button) findViewById(R.id.btnTextImageOnly);
        btnUploadedMoreImage = (Button) findViewById(R.id.btnUploadedMoreImage);
        btnRunOffer = (Button) findViewById(R.id.btnRunOffer);
        spnArea = (Spinner) findViewById(R.id.spnArea);
        imvTimePicker = (ImageView) findViewById(R.id.imvTimePicker);
        offerExpire = (EditText) findViewById(R.id.edtOfferExpire);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Run Offer");

        Intent i = getIntent();
        offer_id = i.getStringExtra("OFFER_ID");
        category = i.getStringExtra("CATEGORY");
        if (i.getStringExtra("CATEGORY").equalsIgnoreCase("FOOD")) {
            selected_category_id = "1";
        } else if (i.getStringExtra("CATEGORY").equalsIgnoreCase("REAL_ESATE")) {
            selected_category_id = "5";
        } else if (i.getStringExtra("CATEGORY").equalsIgnoreCase("MERCHANT")) {
            selected_category_id = "3";
        }


        UploadImageList = new ArrayList<String>();

        ImageAdapter = new UploagImageListAdapter(UploadImageList);
        ehgDisplayUploadedImage.setAdapter(ImageAdapter);
        DateTimeDialog = new Dialog(RunOfferActivity.this);
        DateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DateTimeDialog.setContentView(R.layout.dialog_date_details);
        DateTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        txvSetDate = (TextView) DateTimeDialog.findViewById(R.id.txvSetDate);
        dpDatePicker = (DatePicker) DateTimeDialog.findViewById(R.id.dpDatePicker);
        tpTimePicker = (TimePicker) DateTimeDialog.findViewById(R.id.tpTimePicker);
        btnDialogCancle = (Button) DateTimeDialog.findViewById(R.id.btnDialogCancle);
        btnDialogOk = (Button) DateTimeDialog.findViewById(R.id.btnDialogOk);
        btnRunOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunOffer();
            }
        });
        btnTextOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTextOnly.setBackgroundResource(R.drawable.set_add_offer_location_background);
                btnImageOnly.setBackgroundResource(R.drawable.set_add_offer_location_border);
                btnTextImageOnly.setBackgroundResource(R.drawable.set_add_offer_location_border);
                visibleitem = "text";
            }
        });
        btnImageOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnImageOnly.setBackgroundResource(R.drawable.set_add_offer_location_background);
                btnTextOnly.setBackgroundResource(R.drawable.set_add_offer_location_border);
                btnTextImageOnly.setBackgroundResource(R.drawable.set_add_offer_location_border);
                visibleitem = "image";
            }
        });
        btnTextImageOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTextImageOnly.setBackgroundResource(R.drawable.set_add_offer_location_background);
                btnImageOnly.setBackgroundResource(R.drawable.set_add_offer_location_border);
                btnTextOnly.setBackgroundResource(R.drawable.set_add_offer_location_border);
                visibleitem = "textImage";
            }
        });
        imvTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeDialog.show();
                btnDialogCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateTimeDialog.dismiss();
                    }
                });

                btnDialogOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Year = String.valueOf(dpDatePicker.getYear());
                        Month = String.valueOf(dpDatePicker.getMonth() + 1);
                        Day = String.valueOf(dpDatePicker.getDayOfMonth());

                        Hours = String.valueOf(tpTimePicker.getCurrentHour());
                        Minit = String.valueOf(tpTimePicker.getCurrentMinute());

                        getDtae = String.valueOf(Month + "-" + Day + "-" + Year);

                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        Log.e("currentDateTimeString", currentDateTimeString);
                        SimpleDateFormat df;
                        Date CurrentDD, EndDD;

                        Calendar c = Calendar.getInstance();
                        df = new SimpleDateFormat("MM-dd-yyyy");
                        String CurrentDate = df.format(c.getTime());

                        Date dt = new Date();
                        int hours = dt.getHours();
                        int minutes = dt.getMinutes();
                        int seconds = dt.getSeconds();

                        String curTime = hours + ":" + minutes;
                        getTime = showTime(Integer.parseInt(Hours), Integer.parseInt(Minit));

                        try {
                            CurrentDD = df.parse(CurrentDate);
                            EndDD = df.parse(getDtae);

                            if (CurrentDD.compareTo(EndDD) < 0) {

                                Log.e("CurrentDD......", CurrentDD.toString());
                                Log.e("EndDD......", EndDD.toString());

                                offerExpire.setText(getDtae + " " + getTime);
                                DateTimeDialog.dismiss();

                            } else if (CurrentDD.equals(EndDD)) {

                                Log.e("CurrentDD......else if ", CurrentDD.toString());
                                Log.e("EndDD......else if ", EndDD.toString());

                                if (curTime.compareTo(Hours + ":" + Minit) <= 0) {

                                    Log.e("curTime......", curTime.toString());
                                    Log.e("getTime......", Hours + ":" + Minit);

                                    offerExpire.setText(getDtae + " " + getTime);
                                    DateTimeDialog.dismiss();

                                } else {

                                    Log.e("curTime......else : ", curTime.toString());
                                    Log.e("getTime......else : ", Hours + ":" + Minit);
                                    getTime = "";
                                    Toast.makeText(RunOfferActivity.this, "Selected time should be greater than current time", Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                Log.e("CurrentDD else", CurrentDD.toString());
                                Log.e("EndDD......else", EndDD.toString());

                                getDtae = "";

                                Toast.makeText(RunOfferActivity.this, "Selected date should be greater than current date", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        selecteddate = getDtae;
                        selectedtime = getTime;
                        Log.e("Get Date ", " : " + Year + Month + Day);
                        Log.e("Get Time ", " : " + Minit + Hours);

                    }
                });

            }
        });


        spnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("selected item", parent.getSelectedItem() + "");
                selectedItem = parent.getSelectedItem() + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnUploadedMoreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo",
                        "Choose from Gallery", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        RunOfferActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals("Take Photo")) {
                                    //
//                                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                    startActivityForResult(takePicture, 0);
                                    startCamera();
                                } else if (options[item]
                                        .equals("Choose from Gallery")) {
                                    //
//                                    Intent pickPhoto = new Intent(
//                                            Intent.ACTION_PICK,
//                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                    startActivityForResult(pickPhoto, 1);
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

    public String showTime(int hour, int min) {
        String format;
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        return (new StringBuilder().append(hour).append(":").append(min)
                .append(" ").append(format)).toString();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult requestCode:" + requestCode + ", resultCode:" + resultCode + ", data:" + (data == null ? "null" : data.toString()));

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {
                if (mFileUri != null) {
                    Log.d(TAG, "file: " + mFileUri);
                    //sendImage(mFileUri);
//                    mClick.onClick(mFileUri);
//                    dismiss();
                    UploadImageList.add(getRealPathFromURI(mFileUri));
                } else {
                    Log.d(TAG, "file is null");
                    if (data != null) {
                        Log.d(TAG, "data: " + data.toString());
                        //sendImage(data.getData());
//                        mClick.onClick(data.getData());
//                        dismiss();
                        String path = "";
                        try {
                            path = getPath(data.getData(), true);
                        } catch (Exception e) {
                            path = getRealPathFromURI(data.getData());
                        }
                        UploadImageList.add(path);
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
                        //mCometChat.sendImage(new File(path), String.valueOf(mUser.getCometId()), new BaseCallbacks(this));
//                        mClick.onClick(path);
//                        dismiss();
                        UploadImageList.add(path);
                    }
                }

            }
            ImageAdapter.notifyDataSetChanged();
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

    public void RunOffer() {
        final String strNoOfPeople = edtNoOfPeople.getText().toString();
        final String strAdditionalDescription = edtAddttionalDescription.getText().toString();
        final String area_of_km = spnArea.getSelectedItem().toString();
        final String offer_expire = offerExpire.getText().toString();

        if (visibleitem.isEmpty()) {
            Toast.makeText(RunOfferActivity.this, "Please select visibility item", Toast.LENGTH_LONG).show();
        } else if (strNoOfPeople.isEmpty()) {
            Toast.makeText(RunOfferActivity.this, "Please enter No of people to take offer", Toast.LENGTH_LONG).show();
        } else if (selectedItem.isEmpty()) {
            Toast.makeText(RunOfferActivity.this, "Please select Area of radius", Toast.LENGTH_LONG).show();
        } else if (offerExpire.getText().toString().isEmpty()) {
            Toast.makeText(RunOfferActivity.this, "Please select offer Expire time", Toast.LENGTH_LONG).show();
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
                    HttpPost httppost = new HttpPost(ApplicationData.serviceURL + "add_run_offer.php");

                    try {

                        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                        for (int i = 0; i < UploadImageList.size(); i++) {
                            entity.addPart("image" + (i + 1), new FileBody(new File(UploadImageList.get(i))));
                        }
                        entity.addPart("image_count", new StringBody("" + UploadImageList.size()));
                        //offer_id, seller_id,category_id,no_of_people,area_of_km,validity_of_date,validity_of_time,more_description,offer_type,image_count,image1,image2...
                        entity.addPart("offer_id", new StringBody("" + offer_id));
                        entity.addPart("seller_id", new StringBody("" + userid));
                        entity.addPart("area_of_km", new StringBody("" + area_of_km));
                        entity.addPart("more_description", new StringBody("" + strAdditionalDescription));
                        entity.addPart("offer_type", new StringBody("Run"));
                        entity.addPart("category_id", new StringBody("" + selected_category_id));
                        entity.addPart("no_of_people", new StringBody("" + strNoOfPeople));
                        entity.addPart("validity_of_time", new StringBody("" + selectedtime));
                        entity.addPart("validity_of_date", new StringBody("" + selecteddate));
                        entity.addPart("visible_item", new StringBody("" + visibleitem));
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
                        Log.e("add_run_offer data", sb.toString());
                        return sb.toString();

                    } catch (Exception e) {
                        Log.e("add_run_offer problem", "" + e);
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
                            Toast.makeText(RunOfferActivity.this,
                                    "Your offer run successfully!",
                                    Toast.LENGTH_LONG).show();
                            if (category.equalsIgnoreCase("FOOD")) {
                                Intent i = new Intent();
                                i.putExtra("msg", "success");
                                setResult(31, i);
                                finish();
                            } else if (category.equalsIgnoreCase("MERCHANT")) {
                                Intent i = new Intent();
                                i.putExtra("msg", "success");
                                setResult(32, i);
                                finish();
                            } else if (category.equalsIgnoreCase("REAL_ESATE")) {
                                Intent i = new Intent();
                                i.putExtra("msg", "success");
                                setResult(33, i);
                                finish();
                            }
                        } else {
                            Toast.makeText(RunOfferActivity.this,
                                    "Oopss! We are troubling to send data",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception error) {
                        error.printStackTrace();
                        mProgressDialog.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(RunOfferActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RunOfferActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                protected void onPreExecute() {
                    // TODO Auto-generated method stub
                    super.onPreExecute();
                    mProgressDialog = new ProgressDialog(RunOfferActivity.this);
                    mProgressDialog.setTitle("");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setMessage("Please Wait...");
                    mProgressDialog.show();

                }
            }.execute();
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

    public class UploagImageListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<String> ImageList;

        public UploagImageListAdapter(ArrayList<String> imagelist) {
            ImageList = imagelist;
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
                Picasso.with(RunOfferActivity.this)
                        .load(new File(ImageList.get(position)))
                        .into(holder.imageGridview);
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
