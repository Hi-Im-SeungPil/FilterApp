package org.jeonfeel.jellybus2.Activity_CustomFilter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.jeonfeel.jellybus2.databinding.ActivityCustomFilterBinding;

import java.io.IOException;

public class Activity_customFilter extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "Activity_CustomFilter";
    private ActivityCustomFilterBinding binding;
    private int redNum = 0,greenNum = 0,blueNum = 0;
    private ActivityResultLauncher<Intent> resultLauncher;
    private CustomFilterDatabase db;

    private float[] customFilter = {
            1F,0f,0f,0f,0f,
            0f,1f,0f,0f,0f,
            0f,0f,1f,0f,0f,
            0f,0f,0f,1f,0f
    };
    private String customFilter_;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomFilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db =  CustomFilterDatabase.getInstance(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Window window = Activity_customFilter.this.getWindow();
        window.setStatusBarColor(Color.parseColor("#FFFFFFFF"));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        resultLauncher();

        binding.btnRedPlus.setOnClickListener(this);
        binding.btnRedMinus.setOnClickListener(this);
        binding.btnGreenPlus.setOnClickListener(this);
        binding.btnGreenMinus.setOnClickListener(this);
        binding.btnBluePlus.setOnClickListener(this);
        binding.btnBlueMinus.setOnClickListener(this);

        binding.btnSaveCustomFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBtnSaveCustomFilter();
            }
        });

        binding.btnGetSamplePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                resultLauncher.launch(intent);
            }
        });

        // 밝기 조절 seekBar
        binding.skbBrightnessAdjustment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int seekBarProgress = seekBar.getProgress();

                customFilter[4] = (float) seekBarProgress - 255f;
                customFilter[9] = (float) seekBarProgress - 255f;
                customFilter[14] = (float) seekBarProgress - 255f;
                binding.tvBrightnessAdjustmentProgress.setText(String.valueOf(seekBarProgress - 255));
                binding.ivSample.setColorFilter(new ColorMatrixColorFilter(customFilter));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // 커스텀한 필터 저장
    private void setBtnSaveCustomFilter() {
        EditText editText = new EditText(Activity_customFilter.this);
        editText.setHint("공백 불가!");

        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_customFilter.this)
                .setTitle("제목을 입력해 주세요")
                .setView(editText)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(editText.length() != 0) {
                            String name = editText.getText().toString();
                            StringBuilder stringBuilder = new StringBuilder();

                            for (int j = 0; j < customFilter.length; j++){
                                stringBuilder.append(customFilter[j]).append(",");
                            }
                            stringBuilder.deleteCharAt(stringBuilder.length()-1);
                            customFilter_ = stringBuilder.toString();
                            db.customFilterDao().insert(null,name,customFilter_);
                            Toast.makeText(Activity_customFilter.this, "저장이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(Activity_customFilter.this, "공백 안되요ㅠㅠ..", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
        AlertDialog alertDialog =  builder.create();
        alertDialog.show();
    }

    // 사진 불러온 결과 resultLauncher
    private void resultLauncher() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            Intent intent = result.getData();
                            if(intent != null){
                                Uri selectedImageUri = intent.getData();
                                Bitmap bitmap = null;
                                ImageDecoder.Source source = ImageDecoder.createSource(Activity_customFilter.this.getContentResolver(), selectedImageUri);
                                ImageDecoder.OnHeaderDecodedListener onHeaderDecodedListener = new ImageDecoder.OnHeaderDecodedListener() {
                                    @Override
                                    public void onHeaderDecoded(@NonNull ImageDecoder imageDecoder, @NonNull ImageDecoder.ImageInfo imageInfo, @NonNull ImageDecoder.Source source) {
                                        imageDecoder.setMutableRequired(true);
                                        imageDecoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
                                    }
                                };
                                try {
                                    bitmap = ImageDecoder.decodeBitmap(source,onHeaderDecodedListener);
                                    binding.ivSample.setImageBitmap(bitmap);
                                    binding.ivSample.setColorFilter(new ColorMatrixColorFilter(customFilter));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(Activity_customFilter.this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }
                    }
                });
    }

    //필터 커스텀할 버튼들 set
    @Override
    public void onClick(View view) {
        if (view == binding.btnRedMinus && redNum > -10){
            redNum -= 1;
            binding.tvRedNum.setText(String.valueOf(redNum));
            customFilter[0] -= 0.1f;
        }else if(view == binding.btnRedPlus && redNum < 10){
            redNum += 1;
            binding.tvRedNum.setText(String.valueOf(redNum));
            customFilter[0] += 0.1f;
        }else if(view == binding.btnGreenMinus && greenNum > -10){
            greenNum -= 1;
            binding.tvGreenNum.setText(String.valueOf(greenNum));
            customFilter[6] -= 0.1f;
        }else if(view == binding.btnGreenPlus && greenNum < 10){
            greenNum += 1;
            binding.tvGreenNum.setText(String.valueOf(greenNum));
            customFilter[6] += 0.1f;
        }else if(view == binding.btnBlueMinus && blueNum > -10){
            blueNum -= 1;
            binding.tvBlueNum.setText(String.valueOf(blueNum));
            customFilter[12] -= 0.1f;
        }else if(view == binding.btnBluePlus && blueNum < 10){
            blueNum += 1;
            binding.tvBlueNum.setText(String.valueOf(blueNum));
            customFilter[12] += 0.1f;
        }
        binding.ivSample.setColorFilter(new ColorMatrixColorFilter(customFilter));
    }
}
