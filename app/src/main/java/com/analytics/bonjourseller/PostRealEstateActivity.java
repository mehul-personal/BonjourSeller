package com.analytics.bonjourseller;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
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
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostRealEstateActivity extends AppCompatActivity {

    private static final int COM_REQUEST_CAMERA = 0;
    private static final int COM_REQUEST_GALLERY = 1;
    private static final int RES_REQUEST_CAMERA = 2;
    private static final int RES_REQUEST_GALLERY = 3;
    private static final String TAG = "PostFoodRestaurantActiv";
    static ArrayList<String> residentUploadImageList, commercailUploadImageList, residentUploadImageCallList, commercailUploadImageCallList;
    static String strTypeOfProperty = "Resident", strPurpose = "Sale", strProperty = "", strResFurnishStatus = "", strComFurnishStatus = "";
    static String name, typeofproperty, purpose, complex_name, Houseno, address, furnish_status, bhk, yearoldproperty,
            sqft, property_type, latitude, longitude, offer_status, description, offer_id;
    static ArrayList<String> ImageList;
    static String CALLTYPE = "";
    Button btnResident, btnCommercial, btnSale, btnRent;
    LinearLayout llResident, llCommercial;
    RadioButton apartment, bunglows, resiFurnished, resiSemifurnished, resiUnfurnished, office, shop,
            comFurnished, comSemiFurnished, comUnfurnished;
    EditText edtResidentNameOfSociety, edtResidentHouseNumber, edtResidentAddress, edtResidentBHK,
            edtResidentHowYearOldProperty, edtResidentAreaOfSQFT, edtResidentLatitude, edtResidentLongitude,
            edtCommercialComplexName, edtCommercialAddress, edtCommercialHowYearOldProperty, edtCommercialAreaOfSQFT,
            edtCommercialLatitude, edtCommercialLongitude;
    TextView txvResidentFurnished, txvResidentSemiFurnished, txvResidentUnFurnished, txvResidentTermsAndCondition,
            txvApartment, txvBunglows, txvOr, txvOffice, txvShop, txvCommercialFurnished, txvCommercialSemiFurnished,
            txvCommercialUnFurnished, txvCommercialTermsAndCondition;
    ExpandableHeightGridView ehgResidentUploadImage, ehgCommercialUploadImage;
    Button btnResidentUploadImage, btnResidentTapLocation, btnResidentSubmit, btnCommercialUploadImage,
            btnCommercialTapLocation, btnCommercialSubmit;
    CheckBox chbResidentTermsAndCondition, chbCommercialTermsAndCondition;
    UploadImageListAdapter ResidentUploadImageAdapter, CommercialUploadImageAdapter;
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
        setContentView(R.layout.activity_post_real_estate);
        btnResident = (Button) findViewById(R.id.btnResident);
        btnCommercial = (Button) findViewById(R.id.btnCommercial);
        btnSale = (Button) findViewById(R.id.btnSale);
        btnRent = (Button) findViewById(R.id.btnRent);
        llResident = (LinearLayout) findViewById(R.id.llResident);
        llCommercial = (LinearLayout) findViewById(R.id.llCommercial);

        apartment = (RadioButton) findViewById(R.id.rbApartment);
        bunglows = (RadioButton) findViewById(R.id.rbBunglows);

        resiFurnished = (RadioButton) findViewById(R.id.rbResidentFurnished);
        resiSemifurnished = (RadioButton) findViewById(R.id.rbResidentSemiFurnished);
        resiUnfurnished = (RadioButton) findViewById(R.id.rbResidentUnFurnished);

        office = (RadioButton) findViewById(R.id.rbOffice);
        shop = (RadioButton) findViewById(R.id.rbShop);

        comFurnished = (RadioButton) findViewById(R.id.rbCommercialFurnished);
        comSemiFurnished = (RadioButton) findViewById(R.id.rbCommercialSemiFurnished);
        comUnfurnished = (RadioButton) findViewById(R.id.rbCommercialUnFurnished);

        edtResidentNameOfSociety = (EditText) findViewById(R.id.edtResidentNameOfSociety);
        edtResidentHouseNumber = (EditText) findViewById(R.id.edtResidentHouseNumber);
        edtResidentAddress = (EditText) findViewById(R.id.edtResidentAddress);
        edtResidentBHK = (EditText) findViewById(R.id.edtResidentBHK);
        edtResidentHowYearOldProperty = (EditText) findViewById(R.id.edtResidentHowYearOldProperty);
        edtResidentAreaOfSQFT = (EditText) findViewById(R.id.edtResidentAreaOfSQFT);
        edtResidentLatitude = (EditText) findViewById(R.id.edtResidentLatitude);
        edtResidentLongitude = (EditText) findViewById(R.id.edtResidentLongitude);
        edtCommercialComplexName = (EditText) findViewById(R.id.edtCommercialComplexName);
        edtCommercialAddress = (EditText) findViewById(R.id.edtCommercialAddress);
        edtCommercialHowYearOldProperty = (EditText) findViewById(R.id.edtCommercialHowYearOldProperty);
        edtCommercialAreaOfSQFT = (EditText) findViewById(R.id.edtCommercialAreaOfSQFT);
        edtCommercialLatitude = (EditText) findViewById(R.id.edtCommercialLatitude);
        edtCommercialLongitude = (EditText) findViewById(R.id.edtCommercialLongitude);

        txvResidentFurnished = (TextView) findViewById(R.id.txvResidentFurnished);
        txvResidentSemiFurnished = (TextView) findViewById(R.id.txvResidentFurnished);
        txvResidentUnFurnished = (TextView) findViewById(R.id.txvResidentFurnished);
        txvResidentTermsAndCondition = (TextView) findViewById(R.id.txvResidentFurnished);
        txvApartment = (TextView) findViewById(R.id.txvResidentFurnished);
        txvBunglows = (TextView) findViewById(R.id.txvResidentFurnished);
        txvOr = (TextView) findViewById(R.id.txvResidentFurnished);
        txvOffice = (TextView) findViewById(R.id.txvResidentFurnished);
        txvShop = (TextView) findViewById(R.id.txvResidentFurnished);
        txvCommercialFurnished = (TextView) findViewById(R.id.txvResidentFurnished);
        txvCommercialSemiFurnished = (TextView) findViewById(R.id.txvResidentFurnished);
        txvCommercialUnFurnished = (TextView) findViewById(R.id.txvResidentFurnished);
        txvCommercialTermsAndCondition = (TextView) findViewById(R.id.txvResidentFurnished);

        ehgResidentUploadImage = (ExpandableHeightGridView) findViewById(R.id.ehgResidentUploadImage);
        ehgCommercialUploadImage = (ExpandableHeightGridView) findViewById(R.id.ehgCommercialUploadImage);

        btnResidentUploadImage = (Button) findViewById(R.id.btnResidentUploadImage);
        btnResidentTapLocation = (Button) findViewById(R.id.btnResidentTapLocation);
        btnResidentSubmit = (Button) findViewById(R.id.btnResidentSubmit);

        btnCommercialUploadImage = (Button) findViewById(R.id.btnCommercialUploadImage);
        btnCommercialTapLocation = (Button) findViewById(R.id.btnCommercialTapLocation);
        btnCommercialSubmit = (Button) findViewById(R.id.btnCommercialSubmit);

        chbResidentTermsAndCondition = (CheckBox) findViewById(R.id.chbResidentTermsAndCondition);
        chbCommercialTermsAndCondition = (CheckBox) findViewById(R.id.chbCommercialTermsAndCondition);

        llResident.setVisibility(View.VISIBLE);
        llCommercial.setVisibility(View.GONE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Real Estate");
        residentUploadImageList = new ArrayList<String>();
        commercailUploadImageList = new ArrayList<String>();
        ImageList = new ArrayList<String>();
        Intent i = getIntent();
        CALLTYPE = i.getStringExtra("TYPE");
        if (i.getStringExtra("TYPE").equalsIgnoreCase("EDIT")) {
            name = i.getStringExtra("NAME");
            typeofproperty = i.getStringExtra("TYPEOFPROPERTY");
            purpose = i.getStringExtra("PURPOSE");
            complex_name = i.getStringExtra("COMPLEX_NAME");
            Houseno = i.getStringExtra("HOUSE_NO");
            address = i.getStringExtra("ADDRESS");
            furnish_status = i.getStringExtra("FURNISH_STATUS");
            bhk = i.getStringExtra("BHK");
            yearoldproperty = i.getStringExtra("YEAR_OLD_PROPERTY");
            sqft = i.getStringExtra("SQFT");
            property_type = i.getStringExtra("PROPERTY_TYPE");
            latitude = i.getStringExtra("LATITUDE");
            longitude = i.getStringExtra("LONGITUDE");
            offer_status = i.getStringExtra("OFFER_STATUS");
            ImageList = i.getStringArrayListExtra("IMAGE");
            description = i.getStringExtra("DESCRIPTION");
            offer_id = i.getStringExtra("OFFER_ID");

            strTypeOfProperty = typeofproperty;
            if (purpose.equalsIgnoreCase("Sale")) {
                btnSale.setBackgroundResource(R.drawable.set_add_offer_location_background);
                btnRent.setBackgroundResource(R.drawable.set_add_offer_location_border);
                strPurpose = "Sale";
            }
            if (purpose.equalsIgnoreCase("Rent")) {
                btnRent.setBackgroundResource(R.drawable.set_add_offer_location_background);
                btnSale.setBackgroundResource(R.drawable.set_add_offer_location_border);
                strPurpose = "Rent";
            }
            if (property_type.equalsIgnoreCase("apartment")) {
                apartment.setChecked(true);
                bunglows.setChecked(false);
                strProperty = "apartment";
            }
            if (property_type.equalsIgnoreCase("bunglows")) {
                apartment.setChecked(false);
                bunglows.setChecked(true);
                strProperty = "bunglows";
            }
            if (property_type.equalsIgnoreCase("office")) {
                office.setChecked(true);
                shop.setChecked(false);
                strProperty = "office";
            }
            if (property_type.equalsIgnoreCase("shop")) {
                office.setChecked(false);
                shop.setChecked(true);
                strProperty = "shop";
            }


            if (typeofproperty.equalsIgnoreCase("Resident")) {
                btnCommercial.setBackgroundColor(0);
                btnResident.setBackgroundColor(Color.parseColor("#50ffffff"));
                llResident.setVisibility(View.VISIBLE);
                llCommercial.setVisibility(View.GONE);
                strTypeOfProperty = "Resident";

                edtResidentNameOfSociety.setText(name);
                edtResidentHouseNumber.setText(Houseno);
                edtResidentAddress.setText(address);
                edtResidentBHK.setText(bhk);
                edtResidentHowYearOldProperty.setText(yearoldproperty);
                edtResidentAreaOfSQFT.setText(sqft);
                edtResidentLatitude.setText(latitude);
                edtResidentLongitude.setText(longitude);


                if (furnish_status.equalsIgnoreCase("Furnished")) {
                    resiFurnished.setChecked(true);
                    resiSemifurnished.setChecked(false);
                    resiUnfurnished.setChecked(false);
                    strResFurnishStatus = "Furnished";
                }
                if (furnish_status.equalsIgnoreCase("Semifurnished")) {
                    resiFurnished.setChecked(false);
                    resiSemifurnished.setChecked(true);
                    resiUnfurnished.setChecked(false);
                    strResFurnishStatus = "Semifurnished";
                }
                if (furnish_status.equalsIgnoreCase("Unfurnished")) {
                    resiFurnished.setChecked(false);
                    resiSemifurnished.setChecked(false);
                    resiUnfurnished.setChecked(true);
                    strResFurnishStatus = "Unfurnished";
                }


            } else if (typeofproperty.equalsIgnoreCase("Commercial")) {
                btnResident.setBackgroundColor(0);
                btnCommercial.setBackgroundColor(Color.parseColor("#50ffffff"));
                llCommercial.setVisibility(View.VISIBLE);
                llResident.setVisibility(View.GONE);
                strTypeOfProperty = "Commercial";


                edtCommercialComplexName.setText(complex_name);
                edtCommercialAddress.setText(address);
                edtCommercialHowYearOldProperty.setText(yearoldproperty);
                edtCommercialAreaOfSQFT.setText(sqft);
                edtCommercialLatitude.setText(latitude);
                edtCommercialLongitude.setText(longitude);

                if (furnish_status.equalsIgnoreCase("Furnished")) {
                    comFurnished.setChecked(true);
                    comSemiFurnished.setChecked(false);
                    comUnfurnished.setChecked(false);
                    strComFurnishStatus = "Furnished";
                }

                if (furnish_status.equalsIgnoreCase("Semifurnished")) {
                    comFurnished.setChecked(false);
                    comSemiFurnished.setChecked(true);
                    comUnfurnished.setChecked(false);
                    strComFurnishStatus = "Semifurnished";
                }

                if (furnish_status.equalsIgnoreCase("Unfurnished")) {
                    comFurnished.setChecked(false);
                    comSemiFurnished.setChecked(false);
                    comUnfurnished.setChecked(true);
                    strComFurnishStatus = "Unfurnished";
                }
            }

        }

        residentUploadImageList = new ArrayList<String>();
        residentUploadImageCallList = new ArrayList<String>();
        residentUploadImageList = ImageList;
        for (int j = 0; j < residentUploadImageList.size(); j++) {
            residentUploadImageCallList.add("SERVER");
        }
        ResidentUploadImageAdapter = new UploadImageListAdapter(residentUploadImageList, residentUploadImageCallList);
        ehgResidentUploadImage.setAdapter(ResidentUploadImageAdapter);

        commercailUploadImageList = new ArrayList<String>();
        commercailUploadImageCallList = new ArrayList<String>();
        commercailUploadImageList = ImageList;
        for (int j = 0; j < residentUploadImageList.size(); j++) {
            commercailUploadImageCallList.add("SERVER");
        }
        CommercialUploadImageAdapter = new UploadImageListAdapter(commercailUploadImageList, commercailUploadImageCallList);
        ehgCommercialUploadImage.setAdapter(CommercialUploadImageAdapter);


        btnResidentSubmit.setOnClickListener(new View.OnClickListener()

                                             {
                                                 @Override
                                                 public void onClick(View v) {
                                                     addRealEstateOffer();
                                                 }
                                             }

        );
        btnCommercialSubmit.setOnClickListener(new View.OnClickListener()

                                               {
                                                   @Override
                                                   public void onClick(View v) {
                                                       addRealEstateOffer();
                                                   }
                                               }

        );
        btnCommercialUploadImage.setOnClickListener(new View.OnClickListener()

                                                    {
                                                        @Override
                                                        public void onClick(View v) {
                                                            final CharSequence[] options = {"Take Photo",
                                                                    "Choose from Gallery", "Cancel"};

                                                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                                                    PostRealEstateActivity.this);
                                                            builder.setTitle("Add Photo!");
                                                            builder.setItems(options,
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int item) {
                                                                            if (options[item].equals("Take Photo")) {
                                                                                startCameraCommercial();
                                                                            } else if (options[item].equals("Choose from Gallery")) {
                                                                                startGalleryCommercial();
                                                                            } else if (options[item].equals("Cancel")) {
                                                                                dialog.dismiss();
                                                                            }
                                                                        }
                                                                    });
                                                            builder.show();
                                                        }
                                                    }

        );

        btnResidentUploadImage.setOnClickListener(new View.OnClickListener()

                                                  {
                                                      @Override
                                                      public void onClick(View v) {
                                                          final CharSequence[] options = {"Take Photo",
                                                                  "Choose from Gallery", "Cancel"};

                                                          AlertDialog.Builder builder = new AlertDialog.Builder(
                                                                  PostRealEstateActivity.this);
                                                          builder.setTitle("Add Photo!");
                                                          builder.setItems(options,
                                                                  new DialogInterface.OnClickListener() {
                                                                      @Override
                                                                      public void onClick(DialogInterface dialog, int item) {
                                                                          if (options[item].equals("Take Photo")) {
                                                                              startCameraResident();
                                                                          } else if (options[item]
                                                                                  .equals("Choose from Gallery")) {
                                                                              startGalleryResident();
                                                                          } else if (options[item].equals("Cancel")) {
                                                                              dialog.dismiss();
                                                                          }
                                                                      }
                                                                  });
                                                          builder.show();
                                                      }
                                                  }

        );

        btnCommercialTapLocation.setOnClickListener(new View.OnClickListener()

                                                    {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent i = new Intent(PostRealEstateActivity.this, NavigationDrawerActivity.class);
                                                            startActivityForResult(i, 15);
                                                        }
                                                    }

        );
        btnResidentTapLocation.setOnClickListener(new View.OnClickListener()

                                                  {
                                                      @Override
                                                      public void onClick(View v) {
                                                          Intent i = new Intent(PostRealEstateActivity.this, NavigationDrawerActivity.class);
                                                          startActivityForResult(i, 15);
                                                      }
                                                  }

        );
        apartment.setOnClickListener(new View.OnClickListener()

                                     {
                                         @Override
                                         public void onClick(View v) {
                                             apartment.setChecked(true);
                                             bunglows.setChecked(false);
                                             strProperty = "apartment";
                                         }
                                     }

        );
        bunglows.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {
                                            apartment.setChecked(false);
                                            bunglows.setChecked(true);
                                            strProperty = "bunglows";
                                        }
                                    }

        );

        office.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(View v) {
                                          office.setChecked(true);
                                          shop.setChecked(false);
                                          strProperty = "office";
                                      }
                                  }

        );
        shop.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View v) {
                                        office.setChecked(false);
                                        shop.setChecked(true);
                                        strProperty = "shop";
                                    }
                                }

        );
        resiFurnished.setOnClickListener(new View.OnClickListener()

                                         {
                                             @Override
                                             public void onClick(View v) {
                                                 resiFurnished.setChecked(true);
                                                 resiSemifurnished.setChecked(false);
                                                 resiUnfurnished.setChecked(false);
                                                 strResFurnishStatus = "Furnished";
                                             }
                                         }

        );
        resiSemifurnished.setOnClickListener(new View.OnClickListener()

                                             {
                                                 @Override
                                                 public void onClick(View v) {
                                                     resiFurnished.setChecked(false);
                                                     resiSemifurnished.setChecked(true);
                                                     resiUnfurnished.setChecked(false);
                                                     strResFurnishStatus = "Semifurnished";
                                                 }
                                             }

        );
        resiUnfurnished.setOnClickListener(new View.OnClickListener()

                                           {
                                               @Override
                                               public void onClick(View v) {
                                                   resiFurnished.setChecked(false);
                                                   resiSemifurnished.setChecked(false);
                                                   resiUnfurnished.setChecked(true);
                                                   strResFurnishStatus = "Unfurnished";
                                               }
                                           }

        );


        comFurnished.setOnClickListener(new View.OnClickListener()

                                        {
                                            @Override
                                            public void onClick(View v) {
                                                comFurnished.setChecked(true);
                                                comSemiFurnished.setChecked(false);
                                                comUnfurnished.setChecked(false);
                                                strComFurnishStatus = "Furnished";
                                            }
                                        }

        );
        comSemiFurnished.setOnClickListener(new View.OnClickListener()

                                            {
                                                @Override
                                                public void onClick(View v) {
                                                    comFurnished.setChecked(false);
                                                    comSemiFurnished.setChecked(true);
                                                    comUnfurnished.setChecked(false);
                                                    strComFurnishStatus = "Semifurnished";
                                                }
                                            }

        );
        comUnfurnished.setOnClickListener(new View.OnClickListener()

                                          {
                                              @Override
                                              public void onClick(View v) {
                                                  comFurnished.setChecked(false);
                                                  comSemiFurnished.setChecked(false);
                                                  comUnfurnished.setChecked(true);
                                                  strComFurnishStatus = "Unfurnished";
                                              }
                                          }

        );
        btnSale.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View v) {
                                           btnSale.setBackgroundResource(R.drawable.set_add_offer_location_background);
                                           btnRent.setBackgroundResource(R.drawable.set_add_offer_location_border);
                                           strPurpose = "Sale";
                                       }
                                   }

        );
        btnRent.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View v) {
                                           btnRent.setBackgroundResource(R.drawable.set_add_offer_location_background);
                                           btnSale.setBackgroundResource(R.drawable.set_add_offer_location_border);
                                           strPurpose = "Rent";
                                       }
                                   }

        );
        btnResident.setOnClickListener(new View.OnClickListener()

                                       {
                                           @Override
                                           public void onClick(View v) {
                                               btnCommercial.setBackgroundColor(0);
                                               btnResident.setBackgroundColor(Color.parseColor("#50ffffff"));
                                               llResident.setVisibility(View.VISIBLE);
                                               llCommercial.setVisibility(View.GONE);
                                               strTypeOfProperty = "Resident";
                                           }
                                       }

        );
        btnCommercial.setOnClickListener(new View.OnClickListener()

                                         {
                                             @Override
                                             public void onClick(View v) {
                                                 btnResident.setBackgroundColor(0);
                                                 btnCommercial.setBackgroundColor(Color.parseColor("#50ffffff"));
                                                 llCommercial.setVisibility(View.VISIBLE);
                                                 llResident.setVisibility(View.GONE);
                                                 strTypeOfProperty = "Commercial";
                                             }
                                         }

        );
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
                if (strTypeOfProperty.equalsIgnoreCase("Resident")) {
                    edtResidentLatitude.setText(data.getStringExtra("LATITUDE"));
                    edtResidentLongitude.setText(data.getStringExtra("LONGITUDE"));
                } else if (strTypeOfProperty.equalsIgnoreCase("Commercial")) {
                    edtCommercialLatitude.setText(data.getStringExtra("LATITUDE"));
                    edtCommercialLongitude.setText(data.getStringExtra("LONGITUDE"));
                }
            }
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case COM_REQUEST_CAMERA:
                    // Uri selectedImage = imageReturnedIntent.getData();
                    if (mFileUri != null) {
                        Log.d(TAG, "file: " + mFileUri);
                        commercailUploadImageList.add(getRealPathFromURI(mFileUri));
                        commercailUploadImageCallList.add("FILE");
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
                            commercailUploadImageList.add(path);
                            commercailUploadImageCallList.add("FILE");
                        }
                    }

                    CommercialUploadImageAdapter.notifyDataSetChanged();
                    break;

                case COM_REQUEST_GALLERY:
                    if (data != null && data.getData() != null) {
                        String path = "";
                        try {
                            path = getPath(data.getData(), true);
                        } catch (Exception e) {
                            path = getRealPathFromURI(data.getData());
                        }
                        Log.d(TAG, "path:" + path);
                        if (path != null) {
                            commercailUploadImageList.add(path);
                            commercailUploadImageCallList.add("FILE");
                            CommercialUploadImageAdapter.notifyDataSetChanged();
                        }
                    }

                    break;
                case RES_REQUEST_CAMERA:
                    if (mFileUri != null) {
                        Log.d(TAG, "file: " + mFileUri);
                        residentUploadImageList.add(getRealPathFromURI(mFileUri));
                        residentUploadImageCallList.add("FILE");
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
                            residentUploadImageList.add(path);
                            residentUploadImageCallList.add("FILE");
                        }
                    }
                    ResidentUploadImageAdapter.notifyDataSetChanged();
                    break;

                case RES_REQUEST_GALLERY:
                    if (data != null && data.getData() != null) {
                        String path = "";
                        try {
                            path = getPath(data.getData(), true);
                        } catch (Exception e) {
                            path = getRealPathFromURI(data.getData());
                        }
                        Log.d(TAG, "path:" + path);
                        if (path != null) {
                            residentUploadImageList.add(path);
                            residentUploadImageCallList.add("FILE");
                            ResidentUploadImageAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
            }
        }
    }

    private void startCameraCommercial() {

        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFileUri = getOutputMediaFile(1);
        if (mFileUri != null) {
            intent1.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            startActivityForResult(intent1, COM_REQUEST_CAMERA);
        } else {
            Log.e("image camera", "file not available");
        }

    }

    private void startCameraResident() {

        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFileUri = getOutputMediaFile(1);
        if (mFileUri != null) {
            intent1.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            startActivityForResult(intent1, RES_REQUEST_CAMERA);
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

    private void startGalleryCommercial() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, COM_REQUEST_GALLERY);

    }

    private void startGalleryResident() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, RES_REQUEST_GALLERY);

    }


    public void addRealEstateOffer() {

        final String residentNameOfSociety = edtResidentNameOfSociety.getText().toString();
        final String residentHouseNumber = edtResidentHouseNumber.getText().toString();
        final String residentAddress = edtResidentAddress.getText().toString();
        final String residentBHK = edtResidentBHK.getText().toString();
        final String residentHowYearOldProperty = edtResidentHowYearOldProperty.getText().toString();
        final String residentAreaOfSQFT = edtResidentAreaOfSQFT.getText().toString();
        final String residentLatitude = edtResidentLatitude.getText().toString();
        final String residentLongitude = edtResidentLongitude.getText().toString();
        final String commercialComplexName = edtCommercialComplexName.getText().toString();
        final String commercialAddress = edtCommercialAddress.getText().toString();
        final String commercialHowYearOldProperty = edtCommercialHowYearOldProperty.getText().toString();
        final String commercialAreaOfSQFT = edtCommercialAreaOfSQFT.getText().toString();
        final String commercialLatitude = edtCommercialLatitude.getText().toString();
        final String commercialLongitude = edtCommercialLongitude.getText().toString();
        if (strTypeOfProperty.isEmpty()) {
            Toast.makeText(PostRealEstateActivity.this, "Please select Property like Resident or Commercial!", Toast.LENGTH_LONG).show();
        } else if (strPurpose.isEmpty()) {
            Toast.makeText(PostRealEstateActivity.this, "Please select Sale or Rent", Toast.LENGTH_LONG).show();
        } else if (strProperty.isEmpty()) {
            if (strTypeOfProperty.equalsIgnoreCase("Resident")) {
                Toast.makeText(PostRealEstateActivity.this, "Please select Apartment or Bungalows", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PostRealEstateActivity.this, "Please select Office or Shope", Toast.LENGTH_LONG).show();
            }
        } else if (residentAddress.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Resident")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter Resident Address", Toast.LENGTH_LONG).show();
        } else if (residentAreaOfSQFT.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Resident")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter Resident Area Sqft", Toast.LENGTH_LONG).show();
        } else if (residentHowYearOldProperty.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Resident")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter How much Year Old Resident Property", Toast.LENGTH_LONG).show();
        } else if (residentNameOfSociety.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Resident")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter Resident Society Name", Toast.LENGTH_LONG).show();
        } else if (residentHouseNumber.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Resident")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter Resident House No", Toast.LENGTH_LONG).show();
        } else if (residentBHK.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Resident")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter Resident BHK", Toast.LENGTH_LONG).show();
        } else if (residentLatitude.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Resident")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter Resident Latitude", Toast.LENGTH_LONG).show();
        } else if (residentLongitude.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Resident")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter Resident Longitude", Toast.LENGTH_LONG).show();
        } else if (strResFurnishStatus.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Resident")) {
            Toast.makeText(PostRealEstateActivity.this, "Please select Resident Furnish Status", Toast.LENGTH_LONG).show();
        } else if (!chbResidentTermsAndCondition.isChecked() && strTypeOfProperty.equalsIgnoreCase("Resident")) {
            Toast.makeText(PostRealEstateActivity.this, "You must be agree with terms and condition before posting Resident Offer", Toast.LENGTH_LONG).show();
        } else if (commercialAddress.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Commercial")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter Commercial Address", Toast.LENGTH_LONG).show();
        } else if (commercialAreaOfSQFT.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Commercial")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter Commercial Area Sqft", Toast.LENGTH_LONG).show();
        } else if (commercialHowYearOldProperty.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Commercial")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter How much Year Old Commercial Property", Toast.LENGTH_LONG).show();
        } else if (commercialComplexName.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Commercial")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter Commercial Complex Name", Toast.LENGTH_LONG).show();
        } else if (commercialLatitude.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Commercial")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter Commercial Latitude", Toast.LENGTH_LONG).show();
        } else if (commercialLongitude.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Commercial")) {
            Toast.makeText(PostRealEstateActivity.this, "Please enter Commercial Longitude", Toast.LENGTH_LONG).show();
        } else if (strComFurnishStatus.isEmpty() && strTypeOfProperty.equalsIgnoreCase("Commercial")) {
            Toast.makeText(PostRealEstateActivity.this, "Please select Commercial Furnish Status", Toast.LENGTH_LONG).show();
        } else if (!chbCommercialTermsAndCondition.isChecked() && strTypeOfProperty.equalsIgnoreCase("Commercial")) {
            Toast.makeText(PostRealEstateActivity.this, "You must be agree with terms and condition before posting Commercial Offer", Toast.LENGTH_LONG).show();
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
                    if (CALLTYPE.equalsIgnoreCase("EDIT"))
                        httppost = new HttpPost(ApplicationData.serviceURL + "edit_real_estate.php");
                    else
                        httppost = new HttpPost(ApplicationData.serviceURL + "real_estate.php");
                    try {

                        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                        entity.addPart("seller_id", new StringBody(userid,
                                Charset.forName("UTF-8")));
                        if (CALLTYPE.equalsIgnoreCase("EDIT")) {
                            entity.addPart("offer_id", new StringBody(offer_id));
                        }
                        if (strTypeOfProperty.equalsIgnoreCase("Resident")) {
                            entity.addPart("type_of_property", new StringBody(strTypeOfProperty));
                            entity.addPart("purpose", new StringBody(strPurpose));
                            entity.addPart("property", new StringBody(strProperty));
                            entity.addPart("address", new StringBody(residentAddress));
                            entity.addPart("sqft", new StringBody(residentAreaOfSQFT));
                            entity.addPart("oldp", new StringBody(residentHowYearOldProperty));
                            entity.addPart("name_of_society", new StringBody(residentNameOfSociety));
                            entity.addPart("house_no", new StringBody(residentHouseNumber));
                            entity.addPart("bhk", new StringBody(residentBHK));

                            entity.addPart("latitude", new StringBody(residentLatitude));
                            entity.addPart("longitude", new StringBody(residentLongitude));
                            entity.addPart("furnish_status", new StringBody(strResFurnishStatus));
                            int newImg = 1;
                            for (int i = 0; i < residentUploadImageList.size(); i++) {
                                if (residentUploadImageCallList.get(i).equalsIgnoreCase("FILE")) {
                                    entity.addPart("image" + newImg, new FileBody(new File(residentUploadImageList.get(i))));
                                    newImg++;
                                }
                            }
                            entity.addPart("image_count", new StringBody(newImg + ""));
                        } else if (strTypeOfProperty.equalsIgnoreCase("Commercial")) {
                            entity.addPart("type_of_property", new StringBody(strTypeOfProperty));
                            entity.addPart("purpose", new StringBody(strPurpose));
                            entity.addPart("property", new StringBody(strProperty));
                            entity.addPart("address", new StringBody(commercialAddress));
                            entity.addPart("sqft", new StringBody(commercialAreaOfSQFT));
                            entity.addPart("oldp", new StringBody(commercialHowYearOldProperty));
                            entity.addPart("complex_name", new StringBody(commercialComplexName));

                            entity.addPart("latitude", new StringBody(commercialLatitude));
                            entity.addPart("longitude", new StringBody(commercialLongitude));
                            entity.addPart("furnish_status", new StringBody(strComFurnishStatus));
                            int newImg = 1;
                            for (int i = 0; i < commercailUploadImageList.size(); i++) {
                                if (commercailUploadImageCallList.get(i).equalsIgnoreCase("FILE")) {
                                    entity.addPart("image" + newImg, new FileBody(new File(commercailUploadImageList.get(i))));
                                    newImg++;
                                }
                            }
                            entity.addPart("image_count", new StringBody((newImg-1) + ""));
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
                        Log.e("real_estate data", sb.toString());
                        return sb.toString();

                    } catch (Exception e) {
                        Log.e("real_estate problem", "" + e);
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
                                Toast.makeText(PostRealEstateActivity.this,
                                        "Your Real Estate offer updated successfully!",
                                        Toast.LENGTH_LONG).show();
                                Intent i = new Intent();
                                i.putExtra("msg", "success");
                                setResult(39, i);
                                finish();
                            } else {
                                Toast.makeText(PostRealEstateActivity.this,
                                        "Your Real Estate offer added successfully!",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(PostRealEstateActivity.this,
                                    "Oopss! We are troubling to send data",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception error) {
                        error.printStackTrace();
                        mProgressDialog.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(PostRealEstateActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(PostRealEstateActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                protected void onPreExecute() {
                    // TODO Auto-generated method stub
                    super.onPreExecute();
                    mProgressDialog = new ProgressDialog(PostRealEstateActivity.this);
                    mProgressDialog.setTitle("");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setMessage("Please Wait...");
                    mProgressDialog.show();

                }
            }.execute();
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
                    Picasso.with(PostRealEstateActivity.this)
                            .load(ImageList.get(position))
                            .into(holder.imageGridview);
                } else if (CallList.get(position).equalsIgnoreCase("FILE")) {
                    Picasso.with(PostRealEstateActivity.this)
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
