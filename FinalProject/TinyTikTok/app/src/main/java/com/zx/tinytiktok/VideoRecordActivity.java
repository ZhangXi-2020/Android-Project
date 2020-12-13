package com.zx.tinytiktok;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoRecordActivity extends AppCompatActivity {

    private Button mTakePhoto;
    private Button mRecordVideo;


    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private Camera mCamera;
    private MediaRecorder mMediaRecorder;

    private boolean mIsRecording = false;

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);
        mSurfaceView = findViewById(R.id.surface_view);
        mTakePhoto = findViewById(R.id.take_photo);
        mRecordVideo = findViewById(R.id.record_video);

        startCamera();
        Camera.Parameters mParameter =mCamera.getParameters();

        mParameter.setRotation(90);//前置旋转270°

        mCamera.setParameters(mParameter);//将参数设置进去

        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture(VideoRecordActivity.this);
            }
        });

        mRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordVideo();
            }
        });
    }

    private void startCamera() {
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            setCameraDisplayOrientation();
        } catch (Exception e) {
            // error
        }

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int i, int i1, int i2) {
                try {
                    mCamera.stopPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) { }
        });
    }

    private void setCameraDisplayOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);

        int result = (info.orientation - degrees + 360) % 360;
        mCamera.setDisplayOrientation(result);
    }

    private void takePicture(Context context) {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                String fileName = System.currentTimeMillis() + ".jpg";
                if (pictureFile == null) {
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(bytes);
                    fos.close();
                } catch (FileNotFoundException e) {
                    //error
                } catch (IOException e) {
                    //error
                }
                // 其次把文件插入到系统图库
                try {
                    MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            pictureFile.getAbsolutePath(), fileName, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // 最后通知图库更新
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://storage/emulated/0/Pictures/MyCollections/TinyTikTok")));

                mCamera.startPreview();
            }
        });
    }

    private File getOutputMediaFile(int type) {

        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!mediaStorageDir.exists()) {
            Log.e("LOG_TAG", "Directory not exists");
            if (!mediaStorageDir.mkdirs()) {
                Log.e("LOG_TAG", "Directory not created");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            //mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            mediaFile = new File("/storage/emulated/0/Pictures/MyCollections/TinyTikTok/IMG_"+timeStamp+".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
            mediaFile = new File("/storage/emulated/0/Pictures/MyCollections/TinyTikTok/VID_"+timeStamp+".mp4");
            videoToAlbum(mediaFile);
        } else {
            Log.e("LOG_TAG", "File to Store");
            return null;
        }
        Log.e("LOG_TAG", mediaFile.toString());
        return mediaFile;
    }

    private boolean prepareVideoRecorder() {
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        mMediaRecorder.setOrientationHint(90);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            Log.e("prepareVideoRecorder", "IllegalStateException");
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();

            return false;
        }
        return true;
    }


    private void recordVideo() {
        if (mIsRecording) {
            mMediaRecorder.stop();
            releaseMediaRecorder();
            mCamera.lock();
            mIsRecording = false;
            mRecordVideo.setText("Start Recording");
        } else {
            if (prepareVideoRecorder()) {
                mMediaRecorder.start();
                mIsRecording = true;
                mRecordVideo.setText("Stop Recording");
            } else {
                releaseMediaRecorder();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaRecorder();
        releaseCamera();
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void videoToAlbum(File file) {
        //是否添加到相册
        ContentResolver localContentResolver = this.getContentResolver();
        ContentValues localContentValues = getVideoContentValues(file, System.currentTimeMillis());
        Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri));
    }

    public static ContentValues getVideoContentValues(File paramFile, long paramLong)
    {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "video/mp4");
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        return localContentValues;
    }
}
