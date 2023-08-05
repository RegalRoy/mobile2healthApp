package com.example.healthapp.ui.item;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.example.healthapp.R;

import java.util.List;

public class RecyclerViewAdapterCustom extends RecyclerView.Adapter<RecyclerViewAdapterCustom.ViewHolderCustom>{
    Context context;
    //3 add data
    List<ExerciseClass> myList;
    _OnClickListener onClickListener;
    //4 add constructor
    RecyclerViewAdapterCustom(List<ExerciseClass> myList, _OnClickListener onClickListener, Context context){
        this.myList=myList;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    //6
    @NonNull
    @Override
    public ViewHolderCustom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        ViewHolderCustom holder = new ViewHolderCustom(itemView);
        return holder;
    }
    //7
    @Override
    public void onBindViewHolder(@NonNull ViewHolderCustom holder, int position) {
//        holder.imageView.setImageResource(myList.get(position).getPoster());
//        Picasso.get().load(myList.get(position).getPoster()).into(holder.imageView);
//        holder.title.setText(myList.get(position).getTitle());
//        holder.rating.setText(myList.get(position).getRating().toString());
//        holder.desc.setText(myList.get(position).getOverView());

       holder.textView1.setText(myList.get(position).getName());
       holder.textView2.setText(myList.get(position).getType());
       holder.textView3.setText(myList.get(position).getMuscle());
       holder.textView4.setText(myList.get(position).getEquipment());
       holder.textView5.setText(myList.get(position).getDifficulty());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener._OnClick(holder.getAdapterPosition());
            }
        });
    }

    //5
    @Override
    public int getItemCount() {
        return myList.size();
    }

    //1 create inner class
    public class ViewHolderCustom extends RecyclerView.ViewHolder{

        TextView textView1, textView2, textView3, textView4, textView5;
        View view;
        public ViewHolderCustom(@NonNull View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.textView3);
            textView2 = itemView.findViewById(R.id.textView4);
            textView3 = itemView.findViewById(R.id.textView5);
            textView4 = itemView.findViewById(R.id.textView6);
            textView5 = itemView.findViewById(R.id.textView7);
            view = itemView;
        }


    }
    //8 add listener interface
    public interface _OnClickListener{
        public void _OnClick(int i);
    }
}
