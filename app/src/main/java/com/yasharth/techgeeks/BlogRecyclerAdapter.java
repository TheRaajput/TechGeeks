package com.yasharth.techgeeks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;


public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder>{

    public List<BlogPost> blog_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;

    public BlogRecyclerAdapter(List<BlogPost>blog_list){

        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String user_id = blog_list.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    String userName = task.getResult().getString("Name");
                    String userImage = task.getResult().getString("Image");

                    holder.setUserdata(userName,userImage);


                }else{
                }
            }
        });

        String image_url = blog_list.get(position).getImage_url();
        holder.setBlogImage(image_url);


        long milliseconds = blog_list.get(position).getTimestamp().getTime();
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(milliseconds);
        holder.setTime(date);

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView descView;
        private TextView blogUsername;
        private ImageView blogImageView;
        private TextView blogDate;
        private CircleImageView blogUserImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setDescText(String descText){

            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);

        }
        public void setUserdata(String name, String image){
            blogUsername = mView.findViewById(R.id.blog_user_name);
            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUsername.setText(name);


            RequestOptions placeholderOptions = new RequestOptions();
            placeholderOptions.placeholder(R.drawable.profile_placeholder);
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderOptions)
                    .load(image)
                    .into(blogUserImage);

        }
        public void setBlogImage(String downloadUri){
            blogImageView = mView.findViewById(R.id.blog_image);

            RequestOptions placeholderOptions = new RequestOptions();
            placeholderOptions.placeholder(R.drawable.image_placeholder);
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderOptions)
                    .load(downloadUri)
                    .into(blogImageView);
        }
        public void setTime(String date){
            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);
        }

    }
}
