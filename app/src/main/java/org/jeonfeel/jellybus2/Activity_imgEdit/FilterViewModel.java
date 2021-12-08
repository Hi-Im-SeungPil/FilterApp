package org.jeonfeel.jellybus2.Activity_imgEdit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jeonfeel.jellybus2.Activity_CustomFilter.CustomFilter;
import org.jeonfeel.jellybus2.Activity_CustomFilter.CustomFilterDatabase;
import org.jeonfeel.jellybus2.databinding.ActivityImgEditBinding;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FilterViewModel extends AndroidViewModel {

    private final String TAG = "FilterViewModel";
    private Context context;
    private MutableLiveData<float[]> currentFilterLiveData;
    private final CustomFilterDatabase db;

    public FilterViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        db = CustomFilterDatabase.getInstance(context);
    }

    public MutableLiveData<float[]> getCurrentFilterLiveData() {

        if(currentFilterLiveData == null){
            currentFilterLiveData = new MutableLiveData<>();
        }

        return currentFilterLiveData;
    }

    // 필터 선택하면 이미지뷰 업데이트
    public void updateImageViewMatrix(ActivityImgEditBinding binding){

        ColorMatrixColorFilter matrixColorFilter = new ColorMatrixColorFilter(currentFilterLiveData.getValue());
        binding.ivImageView.setColorFilter(matrixColorFilter);

    }

    // 필터 리사이클러뷰 초기화
    public void rvFilterInit(ActivityImgEditBinding binding){

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL,false);
        binding.rvFilter.setLayoutManager(mLinearLayoutManager);
        binding.rvFilter.setHasFixedSize(true);

    }

    // 편집한 파일 저장
    @SuppressLint("SimpleDateFormat")
    public int saveFile(Bitmap original) {

        Bitmap bitmap = Bitmap.createBitmap(original.getWidth(),
                original.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        float[] currentMatrix = currentFilterLiveData.getValue();
        paint.setColorFilter(new ColorMatrixColorFilter(currentMatrix));

        canvas.drawBitmap(original, 0, 0, paint);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
        String pictureName = simpleDateFormat.format(new Date()) + ".jpg";

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DISPLAY_NAME, pictureName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.IS_PENDING, 1);
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/JeonSeungPil");

        ContentResolver contentResolver = context.getContentResolver();
        Uri item = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item,"w",null);

            if (pdf == null) {
                return -1;
            } else {

                FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                fos.close();

                values.clear();
                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                contentResolver.update(item, values, null, null);

                return 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;

    }

    //선택한 uri 비트맵으로 변환
    public Bitmap ivImageViewInit(String uri,ActivityImgEditBinding binding){

        Bitmap bitmap = null;

        ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), Uri.parse(uri));

        ImageDecoder.OnHeaderDecodedListener onHeaderDecodedListener = new ImageDecoder.OnHeaderDecodedListener() {
            @Override
            public void onHeaderDecoded(@NonNull ImageDecoder imageDecoder, @NonNull ImageDecoder.ImageInfo imageInfo, @NonNull ImageDecoder.Source source) {
                imageDecoder.setMutableRequired(true);
                imageDecoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
            }
        };

        try {
            bitmap = ImageDecoder.decodeBitmap(source,onHeaderDecodedListener);
            binding.ivImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    // 커스텀 필터 내장 DB에서 불러오기
    public void getCustomFilterList(Adapter_rvFilter adapter_rvFilter){

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<FilterInfoDTO> filterInfoDTOS = new ArrayList<>();
                List<CustomFilter> customFilters = db.customFilterDao().getAll();

                if (customFilters != null) {

                    for (int i = 0; i < customFilters.size(); i++) {

                        String name = customFilters.get(i).getName();

                        String[] matrix = customFilters.get(i).getMatrix().split(",");

                        float[] parseFloatMatrix = new float[matrix.length];

                        for (int j = 0; j < parseFloatMatrix.length; j++) {

                            parseFloatMatrix[j] = Float.parseFloat(matrix[j]);

                        }
                        FilterInfoDTO filterInfoDTO = new FilterInfoDTO(parseFloatMatrix, name);
                        filterInfoDTOS.add(filterInfoDTO);
                    }
                }
                adapter_rvFilter.setCustomFilterInfoDTOS(filterInfoDTOS);
            }

        }).start();

    }

}
