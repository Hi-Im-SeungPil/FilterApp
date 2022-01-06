package org.jeonfeel.jellybus2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import org.jeonfeel.jellybus2.databinding.ActivityCameraBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Activity_camera extends AppCompatActivity {
    private final String TAG = "Activity_Camera";
    private ActivityCameraBinding binding;
    private ImageCapture imageCapture;
    private final Executor cameraExecutor = Executors.newSingleThreadExecutor();
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Camera camera;
    private int LensFacing = CameraSelector.LENS_FACING_BACK;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        cameraSet();

        binding.btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        binding.btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBtnSwitchCamera();
            }
        });

    }
    // 카메라 초기 세팅
    private void cameraSet() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();
                startCamera(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }
    // 사진 캡쳐 후 저장
    private void takePicture(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
        String pictureName = simpleDateFormat.format(new Date()) + ".jpg";

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DISPLAY_NAME, pictureName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.IS_PENDING, 1);
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/JeonSeungPil");

        ContentResolver contentResolver = this.getContentResolver();

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(contentResolver,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values).build();

        imageCapture.takePicture(outputFileOptions, cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Activity_camera.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Activity_camera.this, pictureName + " 저장이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onError(ImageCaptureException error) {
                        // insert your code here.
                        Toast.makeText(Activity_camera.this,  "오류가 발생 했습니다.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        );
    }
    // 카메라 전면 / 후면 변경 버튼
    private void setBtnSwitchCamera(){
        if(LensFacing == CameraSelector.LENS_FACING_BACK){
            LensFacing = CameraSelector.LENS_FACING_FRONT;
        }else{
            LensFacing = CameraSelector.LENS_FACING_BACK;
        }
        cameraSet();
    }
    //카메라 시작
    private void startCamera(ProcessCameraProvider cameraProvider){
        Preview preview = new Preview.Builder()
                .build();

        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(binding.viewFinder.getDisplay().getRotation())
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(LensFacing)
                .build();

        preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
        camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview,imageCapture);
        CameraControl cameraControl = camera.getCameraControl();
        // 화면 터치하면 초점 잡기
        binding.viewFinder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d(TAG,"Focus On");
                    MeteringPointFactory meteringPointFactory = new SurfaceOrientedMeteringPointFactory(
                            Float.parseFloat(String.valueOf(binding.viewFinder.getWidth())),
                            Float.parseFloat(String.valueOf(binding.viewFinder.getHeight()))
                    );
                    MeteringPoint meteringPoint = meteringPointFactory.createPoint(motionEvent.getX(),motionEvent.getY());
                    try {
                        Log.d(TAG,"try");
                        FocusMeteringAction.Builder builder = new FocusMeteringAction.Builder(meteringPoint,FocusMeteringAction.FLAG_AF);
                        cameraControl.startFocusAndMetering(builder.disableAutoCancel().build());

                    }catch (Exception e){
                        Log.d(TAG,"cat");
                        e.printStackTrace();
                    }
                    return true;
                }else{
                    return false;
                }
            }
        });
    }
}
