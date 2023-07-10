package com.example.municipalcars.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.municipalcars.databinding.ItemCarBinding;
import com.example.municipalcars.model.UserData;

import java.util.List;

public class ItemCarsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mActivity;
    private List<UserData> list;

    private OnClickListener listener;


    public ItemCarsAdapter(Activity mActivity, List<UserData> list, OnClickListener listener) {
        this.mActivity = mActivity;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCarBinding binding = ItemCarBinding.inflate(mActivity.getLayoutInflater());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(getPx(15), getPx(7), getPx(15), getPx(7));
        binding.getRoot().setLayoutParams(lp);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) (holder).itemView.getLayoutParams();
        ((ViewHolder) holder).bind(getItem(position), position);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemCarBinding binding;

        public ViewHolder(View rootView, ItemCarBinding binding) {
            super(rootView);
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(final UserData item, int position) {



            if (item!=null && item.getCar()!=null){
                binding.btnShow.setOnClickListener(view -> listener.onItemClick(item,position));
                if (item.getCar().getName()!=null)binding.txtNameCar.setText( item.getCar().getName() );
                if (item.getCar().getDetails()!=null)binding.txtDesCar.setText(item.getCar().getDetails() );
                if (item.getName()!=null)binding.txtNameUser.setText(item.getName());
                if (item.getAddress()!=null)binding.txtLocationUser.setText(item.getAddress());
                if (item.getCar().getPhoto()!=null)  Glide.with(mActivity).load(item.getCar().getPhoto()).into(binding.img);
            }
        }

    }


    public UserData getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onItemClick(UserData item, int position);
    }



    public OnClickListener getListener() {
        return listener;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public int getPx(int dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mActivity.getResources().getDisplayMetrics());
        return (int) px;
    }


}
