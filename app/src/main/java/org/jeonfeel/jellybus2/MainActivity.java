package org.jeonfeel.jellybus2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jeonfeel.jellybus2.Activity_CustomFilter.Activity_customFilter;
import org.jeonfeel.jellybus2.Activity_imgEdit.Activity_imgEdit;
import org.jeonfeel.jellybus2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private ActivityResultLauncher<Intent> resultLauncher;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference reference = database.getReference();
    private final int CAMERA_PERMISSION_CODE = 111;
    private final int EXTERNAL_STORAGE_PERMISSION_CODE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUIInit();
        getPermission();
        resultLauncher();
        binding.btnImgEdit.setOnClickListener(this);
        binding.btnCustomFilter.setOnClickListener(this);
        binding.btnAD.setOnClickListener(this);
        binding.btnCamera.setOnClickListener(this);

    }

    private void setUIInit(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Window window = MainActivity.this.getWindow();
        window.setStatusBarColor(Color.parseColor("#22202E"));
        window.getDecorView().setSystemUiVisibility(0);

    }

    private void getPermission(){

        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int externalStoragePermission = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(cameraPermission == PackageManager.PERMISSION_DENIED){ // 권한 없어서 요청

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_PERMISSION_CODE);

        }

        if(externalStoragePermission == PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_STORAGE_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 111){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "앱 사용을 위한 권한 설정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "앱 사용을 위해 카메라 권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == 112){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "앱 사용을 위한 권한 설정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "앱 사용을 위해 외부 저장소 권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resultLauncher(){

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){

                            Intent intent = result.getData();

                            if(intent != null){

                                Intent intent_ = new Intent(MainActivity.this, Activity_imgEdit.class);
                                Uri selectedImageUri = intent.getData();
                                intent_.putExtra("imgUri",selectedImageUri.toString());
                                startActivity(intent_);

                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {

        if (view == binding.btnImgEdit){

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            resultLauncher.launch(intent);

        }else if(view == binding.btnCamera){
            Intent intent = new Intent(this, Activity_camera.class);
            startActivity(intent);
        }else if(view == binding.btnCustomFilter){

            Intent intent = new Intent(this, Activity_customFilter.class);
            startActivity(intent);

        }else if(view == binding.btnAD){

            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.jellybus.Moldiv"));
            startActivity(intent);

        }

    }
}