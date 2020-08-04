package com.example.taskforandroid.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.taskforandroid.Activity.ItemActivity;
import com.example.taskforandroid.Bean.TaskAndUser;
import com.example.taskforandroid.R;
import com.example.taskforandroid.Tool.GetSystemUtils;

import java.text.ParseException;
import java.util.List;

public class task_item_Adapter extends RecyclerView.Adapter<task_item_Adapter.ViewHolder> {

    public List<TaskAndUser> taskAndUserList;
    public Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        public TextView name;
        public TextView state;
        public TextView start_name;
        public TextView start_time;
        public TextView preset_time;
        public TextView agree_name;
        public TextView agree_time;

        public ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            name = itemView.findViewById(R.id.name_tv);
            state = itemView.findViewById(R.id.state_tv);
            start_name = itemView.findViewById(R.id.start_name_tv);
            start_time = itemView.findViewById(R.id.start_time_tv);
            preset_time = itemView.findViewById(R.id.preset_time_tv);
            agree_name = itemView.findViewById(R.id.agree_name_tv);
            agree_time = itemView.findViewById(R.id.agree_time_tv);
            progressBar = itemView.findViewById(R.id.progressBar);

        }
    }

    public task_item_Adapter(List<TaskAndUser> taskAndUserList , Context context){
        this.taskAndUserList=taskAndUserList;
        this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,null);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.name.setText(taskAndUserList.get(position).getTask().getName());
        holder.state.setText(taskAndUserList.get(position).getTask().getState());
        holder.start_name.setText(taskAndUserList.get(position).getUser().getName());
        holder.start_time.setText(taskAndUserList.get(position).getTask().getStart_time().substring(0,10));
        holder.preset_time.setText(taskAndUserList.get(position).getTask().getPreset_time().substring(0,10));


        if (taskAndUserList.get(position).getUser1().getId()==0){
            holder.agree_name.setText("等待批准");
            holder.agree_time.setText("等待批准");
        }else {

            holder.agree_name.setText(taskAndUserList.get(position).getUser1().getName());
            holder.agree_time.setText(taskAndUserList.get(position).getTask().getAgree_time().substring(0,10));
        }

        if("进行中".equals(taskAndUserList.get(position).getTask().getState())){
            try {
                int max= GetSystemUtils.maxdays(taskAndUserList.get(position).getTask().getStart_time(),taskAndUserList.get(position).getTask().getPreset_time());
                int progress= GetSystemUtils.newdays(taskAndUserList.get(position).getTask().getAgree_time());
                if (progress>=max){
                    progress=max;
                }
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.progressBar.setMax(max);
                holder.progressBar.setProgress(progress);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //绑定要启动的Activity对象
                Intent intent =new Intent(context, ItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("task_id", taskAndUserList.get(position).getTask().getId());
                intent.putExtras(bundle);
                //启动Activity
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return taskAndUserList.size();
    }


}