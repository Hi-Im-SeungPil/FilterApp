package org.jeonfeel.jellybus2.Activity_imgEdit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import org.jeonfeel.jellybus2.R;
import org.jeonfeel.jellybus2.databinding.ActivityImgEditBinding;

import java.util.List;

public class Adapter_rvFilter extends RecyclerView.Adapter<Adapter_rvFilter.CustomViewHolder> {
    private final String TAG = "Adapter_rvFilter";
    private final Context context;
    private final ActivityImgEditBinding binding;
    private List<FilterInfoDTO> selectedFilterInfoDTOS;
    private List<FilterInfoDTO> basicFilterInfoDTOS;
    private List<FilterInfoDTO> customFilterInfoDTOS;
    private Bitmap selectedImageBitmap;
    private float[] currentFilterMatrix;
    private FilterViewModel model;
    private String selectedFilter = "";

    public Adapter_rvFilter(Context context,
                            List<FilterInfoDTO> BasicFilterInfoDTOS,
                            ActivityImgEditBinding binding,
                            FilterViewModel model) {
        this.context = context;
        this.selectedFilterInfoDTOS = BasicFilterInfoDTOS;
        this.basicFilterInfoDTOS = BasicFilterInfoDTOS;
        this.binding = binding;
        this.model = model;

        model.getCurrentFilterLiveData().observe((LifecycleOwner) context, new Observer<float[]>() {
            @Override
            public void onChanged(float[] floats) {
                model.updateImageViewMatrix(binding);
            }
        });
    }

    public void setSelectedImageBitmap(Bitmap bitmap){
        this.selectedImageBitmap = bitmap;
    }

    @NonNull
    @Override
    public Adapter_rvFilter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_filter_item,parent,false);

        Adapter_rvFilter.CustomViewHolder customViewHolder = new Adapter_rvFilter.CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_rvFilter.CustomViewHolder holder,
                                 int position) {
        String name = selectedFilterInfoDTOS.get(position).getName();
        float[] matrix = selectedFilterInfoDTOS.get(position).getMatrix();

        holder.iv_filterItem.setImageBitmap(selectedImageBitmap);
        holder.tv_filterItem.setText(name);

        if(selectedFilterInfoDTOS.get(holder.getAdapterPosition()).getName().equals(selectedFilter)){
            holder.iv_filterItem.setColorFilter(Color.parseColor("#4D000000"));
            holder.iv_selectedBar.setVisibility(View.VISIBLE);
        }else{
            holder.iv_filterItem.setColorFilter(new ColorMatrixColorFilter(matrix));
            holder.iv_selectedBar.setVisibility(View.GONE);
        }

        holder.rvFilterParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedFilter.equals(name)) {
                    selectedFilter = name;
                    currentFilterMatrix = matrix;
                    model.getCurrentFilterLiveData().setValue(currentFilterMatrix);
                    notifyItemRangeChanged(0,selectedFilterInfoDTOS.size());
                }
            }
        });
    }

    public void setFilterDtos(String groupName){
        if(groupName.equals("BASIC")){
            this.selectedFilterInfoDTOS = basicFilterInfoDTOS;
        }else if(groupName.equals("CUSTOM")){
            this.selectedFilterInfoDTOS = customFilterInfoDTOS;
        }
        notifyDataSetChanged();
    }

    public void setCustomFilterInfoDTOS(List<FilterInfoDTO> filterInfoDTOS){
        this.customFilterInfoDTOS = filterInfoDTOS;
    }

    @Override
    public int getItemCount() {
        return selectedFilterInfoDTOS.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ConstraintLayout rvFilterParent;
        protected ImageView iv_filterItem;
        protected TextView tv_filterItem;
        protected ImageView iv_selectedBar;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            rvFilterParent = itemView.findViewById(R.id.rvFilterParent);
            iv_filterItem = itemView.findViewById(R.id.iv_filterItem);
            tv_filterItem = itemView.findViewById(R.id.tv_filterItem);
            iv_selectedBar = itemView.findViewById(R.id.iv_selectedBar);
        }
    }
}
