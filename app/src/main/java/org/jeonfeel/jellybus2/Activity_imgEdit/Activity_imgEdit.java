package org.jeonfeel.jellybus2.Activity_imgEdit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jeonfeel.jellybus2.CustomLoadingDialog;
import org.jeonfeel.jellybus2.databinding.ActivityImgEditBinding;

import java.util.ArrayList;
import java.util.List;

public class Activity_imgEdit extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener{
    private final String TAG = "Activity_imgEdit";
    private final FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    private final DatabaseReference REFERENCE = DATABASE.getReference();
    private ActivityImgEditBinding binding;
    private Adapter_rvFilter adapter_rvFilter;
    private List<FilterInfoDTO> filterDTOS;
    private Bitmap original;
    private FilterViewModel model;
    private String selectedFilterGroup = "BASIC";
    private CustomLoadingDialog customLoadingDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImgEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UIinit();
        customLoadingDialog = new CustomLoadingDialog(Activity_imgEdit.this);
        customLoadingDialog.show();

        Intent intent = getIntent();
        String imgUri = intent.getStringExtra("imgUri");

        model = new ViewModelProvider(this).get(FilterViewModel.class);
        model.rvFilterInit(binding);
        original = model.ivImageViewInit(imgUri,binding);

        getFilterListFromFirebase();

        binding.btnReset.setOnTouchListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.btnAdmit.setOnClickListener(this);
        binding.tvBasic.setOnClickListener(this);
        binding.tvCustom.setOnClickListener(this);
    }

    // ActionBar 숨기기, status Bar 색 변경
    private void UIinit(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Window window = Activity_imgEdit.this.getWindow();
        window.setStatusBarColor(Color.parseColor("#1C1C1C"));
        window.getDecorView().setSystemUiVisibility(0);
    }

    // 필터화면 시작 시 Firebase에서 필터 불러오기.
    private void getFilterListFromFirebase(){
        filterDTOS = new ArrayList<>();

        REFERENCE.child("FILTER")
                .child("BASIC")
                .orderByChild("sequence")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    FilterDTOForFirebase filterDTOForFirebase = dataSnapshot.getValue(FilterDTOForFirebase.class);

                    String name = filterDTOForFirebase.getName();
                    String[] matrix = filterDTOForFirebase.getMatrix().split(",");

                    float[] parseFloatMatrix = new float[matrix.length];

                    for(int i =0; i < parseFloatMatrix.length; i++){
                        parseFloatMatrix[i] = Float.parseFloat(matrix[i]);
                    }
                    FilterInfoDTO filterInfoDTO = new FilterInfoDTO(parseFloatMatrix,name);
                    filterDTOS.add(filterInfoDTO);
                }

                adapter_rvFilter = new Adapter_rvFilter(Activity_imgEdit.this,filterDTOS,binding,model);
                adapter_rvFilter.setSelectedImageBitmap(original);
                model.getCustomFilterList(adapter_rvFilter);
                binding.rvFilter.setAdapter(adapter_rvFilter);
                if(customLoadingDialog != null)
                customLoadingDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // onClickListener
    @Override
    public void onClick(View view) {
        if(view == binding.btnCancel){
            finish();
        }else if(view == binding.btnAdmit){
            if(model.saveFile(original) == -1){
                Toast.makeText(Activity_imgEdit.this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(Activity_imgEdit.this, "저장이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }else if(view == binding.tvBasic && !selectedFilterGroup.equals("BASIC")){
            selectedFilterGroup = "BASIC";
            adapter_rvFilter.setFilterDtos("BASIC");
            binding.tvBasic.setBackgroundColor(Color.parseColor("#3E3E3E"));
            binding.tvBasic.setTextColor(Color.parseColor("#FFFFFFFF"));
            binding.tvCustom.setBackgroundColor(Color.parseColor("#FFFFFF"));
            binding.tvCustom.setTextColor(Color.parseColor("#D5D5D5"));
        }else if(view == binding.tvCustom && !selectedFilterGroup.equals("CUSTOM")){
            selectedFilterGroup = "CUSTOM";
            adapter_rvFilter.setFilterDtos("CUSTOM");
            binding.tvCustom.setBackgroundColor(Color.parseColor("#3E3E3E"));
            binding.tvCustom.setTextColor(Color.parseColor("#FFFFFFFF"));
            binding.tvBasic.setBackgroundColor(Color.parseColor("#FFFFFF"));
            binding.tvBasic.setTextColor(Color.parseColor("#D5D5D5"));
        }
    }

    // 원본으로 되돌리기
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if(model.getCurrentFilterLiveData().getValue() != null) {
            if(action == MotionEvent.ACTION_DOWN){
                binding.ivImageView.setColorFilter(null);
                binding.tvOriginal.setVisibility(View.VISIBLE);
            }else if(action == MotionEvent.ACTION_UP){
                binding.ivImageView.setColorFilter(new ColorMatrixColorFilter(model.getCurrentFilterLiveData().getValue()));
                binding.tvOriginal.setVisibility(View.GONE);
            }
        }
        return true;
    }
}
