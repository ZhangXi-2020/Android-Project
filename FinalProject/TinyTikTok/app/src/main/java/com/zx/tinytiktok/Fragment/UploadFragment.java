package com.zx.tinytiktok.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.zx.tinytiktok.Network.VideoService;
import com.zx.tinytiktok.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

// TODO: 该页面为上传视频页面，需要在xml中画出可输入文本框用于获得上传信息，并从内存中选择视频和封面图进行上传
public class UploadFragment extends Fragment {

    // 这里的为默认值，后面根据xml中的输入修改
    String studentId = "123";
    String userName = "hhh";
    String coverImageName = "pic.png";
    String videoName = "video.mp4";
    Uri coverUri;
    Uri videoUri;
    String extraValue = "";
    EditText text_id;
    EditText text_name;
    EditText text_explain;
    ImageView coverView;
    VideoView videoView;
    public static final int chose_photo = 2;
    public static final int chose_video = 3;

    // Retrofit
    final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api-sjtu-camp.bytedance.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    final VideoService videoService = retrofit.create(VideoService.class);

    private MultipartBody.Part getMultipartFromAsset(String name, String fileName, Uri fileUri) {
//        final AssetManager assetManager = getActivity().getAssets();
        ContentResolver resolver = getActivity().getContentResolver();
        try {
            InputStream inStream = resolver.openInputStream(fileUri);
            RequestBody requestFile = RequestBody
                    .create(MediaType.parse("multipart/form-data"), toByteArray(inStream));
            return MultipartBody.Part.createFormData(name, fileName, requestFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, chose_photo);//打开相册
    }

    private void openVideoDir(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("video/*");
        startActivityForResult(intent, chose_video);//打开相册
    }

    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(getActivity(), uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);

            }
            else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        coverUri = uri;
        coverImageName = imagePath;
        Log.e("TAG", "coverUri: " + uri);
        Log.e("TAG", "coverImageName: " + imagePath);
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKate(Intent data){
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }

    private void handleVideoOnKitKat(Intent data){
        String videoPath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(getActivity(),uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection=MediaStore.Video.Media._ID+"="+id;
                videoPath = getVideoPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,selection);

            }
            else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                videoPath=getVideoPath(contentUri,null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            videoPath = getVideoPath(uri,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            videoPath = uri.getPath();
        }
        videoName = videoPath;
        videoUri = uri;
        Log.e("TAG", "videoUri: " + uri);
        Log.e("TAG", "videoName: " + videoPath);
        displayVideo(videoPath);
    }

    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path = cursor.getString( cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private String getVideoPath(Uri uri,String selection){
        String path=null;
        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString( cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            coverView.setImageBitmap(bitmap);
        }
        else{
            Toast.makeText(getContext(),"failed to get Image", Toast.LENGTH_LONG).show();
        }
    }

    private void displayVideo(String videoPath){
        if(videoPath!=null){
            videoView.setVideoPath(videoPath);
            videoView.start();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mp.setLooping(true);
                }
            });
        }
        else{
            Toast.makeText(getContext(),"failed to get Video", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_upload, container, false);

        text_id = root.findViewById(R.id.text_id);
        text_name = root.findViewById(R.id.text_name);
        text_explain = root.findViewById(R.id.text_content);
        coverView = root.findViewById(R.id.img_cover);
        videoView = root.findViewById(R.id.video_show);

        root.findViewById(R.id.btn_select_image).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else {
                    openAlbum();
                    coverView.setBackgroundColor(R.color.white);
                }
            }
        });

        root.findViewById(R.id.btn_select_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 访问本地资源选择需要上传的视频
                if(ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else {
                    openVideoDir();
                }
            }
        });

        root.findViewById(R.id.icon_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(text_id.getText())) {
                    studentId = text_id.getText().toString().trim();
                    Log.e("TAG", "studentId: " + studentId);
                }
                if (!TextUtils.isEmpty(text_name.getText())) {
                    userName = text_name.getText().toString().trim();
                    Log.e("TAG", "studentName: " + userName);
                }
                if (!TextUtils.isEmpty(text_explain.getText())) {
                    extraValue = text_explain.getText().toString().trim();
                    Log.e("TAG", "extra: " + extraValue);
                }

                videoService.post(studentId, userName, extraValue,
                        getMultipartFromAsset("cover_image", coverImageName, coverUri),
                        getMultipartFromAsset("video", videoName, videoUri))
                    .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!response.isSuccessful()) {
                        Log.e("TAG", "fail!");
                        return;
                    }
                    final ResponseBody body = response.body();
                    if (body == null) {
                        Log.e("TAG", "body is null");
                    } else {
                        try {
                            final String string = body.string();
                            Log.e("TAG", string);
                            // TODO：此处上传成功显示方式需改
                            ((TextView) root.findViewById(R.id.tmp_upload_info)).setText(string);
                            Toast.makeText(getActivity(),
                                    "上传成功！", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }});
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
//            case TAKE_PHOTP:
//                if(resultCode==RESULT_OK){
//                    try {
//                        Bitmap bitmap= BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
//                        coverView.setImageBitmap(bitmap);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
            case chose_photo:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }
                    else{
                        handleImageBeforeKitKate(data);
                    }
                }
                break;
            case chose_video:
                if(resultCode==RESULT_OK){
                    handleVideoOnKitKat(data);
                }
                break;
            default:
                break;
        }
    }
}