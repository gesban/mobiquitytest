package com.example.mobiquitytest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobiquitytest.utils.GlobalVariables;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Jonathan Gama on 6/13/2016.
 */
public class PictureFragment extends Fragment implements View.OnClickListener {

    Context context;
    TextView txtPlace;
    TextView txtTemperature;
    TextView txtMessage;


    private static final int ACTION_TAKE_PHOTO = 2;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    FrameLayout containerView;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.picture_fragment, container, false);

        context = getActivity().getApplicationContext();

        txtPlace = (TextView) view.findViewById(R.id.txt_place);
        txtTemperature = (TextView) view.findViewById(R.id.txt_temperature);
        txtMessage = (TextView) view.findViewById(R.id.txt_message);

        mImageView = (ImageView) view.findViewById(R.id.mImageView);

        txtMessage.setOnClickListener(this);

        updateViews();

        return view;

    }

    public void updateViews()
    {
        String title = GlobalVariables.getInstance().getPlaceName();
        String temperature = GlobalVariables.getInstance().getPlaceTemperature();

        if (title.trim().length()>0) {
            txtPlace.setText(getCity(title));
        }

        if (temperature.trim().length()>0) {
            txtTemperature.setText(getDegrees(temperature));
        }
    }

    private String getDegrees(String temperature)
    {
        String[] data = temperature.split("Hi");
        if (data!=null)
        {
            if (data.length>0)
            {
              return data[0];
            }
            else
            {
                return "0";
            }
        }
        else
        {
            return "0";
        }
    }


    private String getCity(String placeName)
    {
        String[] data = placeName.split(",");
        if (data!=null)
        {
            if (data.length>0)
            {
                return data[0];
            }
            else
            {
                return placeName;
            }
        }
        else
        {
            return placeName;
        }
    }


    @Override
    public void onClick(View v) {
        dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
    }

    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }


    private File getAlbumDir() {

        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"");
        if(!file.mkdirs())
        {
            Log.e("IO Problem", "Directory not created!");
        }
        return file;

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(bitmap);
        mImageView.setVisibility(View.VISIBLE);

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {

            case ACTION_TAKE_PHOTO://jonathan
                File f2 = null;

                try {
                    f2 = setUpPhotoFile();
                    mCurrentPhotoPath = f2.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f2));
                } catch (IOException e) {
                    e.printStackTrace();
                    f2 = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }


    private void handleSmallCameraPhoto(Intent intent) {

        if (mCurrentPhotoPath != null) {
            setPic();

            containerView = (FrameLayout) view.findViewById(R.id.containerView);
            Bitmap bitmap = getScreenshot(containerView);

            // This is for Facebook
//            SharePhoto sharePhoto = new SharePhoto.BUilder();
//            sharePhoto.setBitmap(bitmap);
//            ShareDialog shareDialog = new ShareDialog();
//            shareDialog.show(shareContent,Mode.AUTOMATIC);
            // This is for Facebook

            galleryAddPic();
            mCurrentPhotoPath = null;
        }



    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    private void handleCameraVideo(Intent intent) {
        mImageBitmap = null;
        mImageView.setVisibility(View.INVISIBLE);
    }


    Button.OnClickListener mTakePicSOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
                }
            };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case ACTION_TAKE_PHOTO: {
                if (resultCode == getActivity().RESULT_OK) {
                    handleSmallCameraPhoto(data);
                }
                break;
            }

        }
    }

//    // Some lifecycle callbacks so that the image can survive orientation change
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
//        //outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
//        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
//        //outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null));
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
//        //mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
//        mImageView.setImageBitmap(mImageBitmap);
//        mImageView.setVisibility(
//                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
//                        ImageView.VISIBLE : ImageView.INVISIBLE
//        );
//    }

    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(context, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }
    }

    public Bitmap getScreenshot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}
