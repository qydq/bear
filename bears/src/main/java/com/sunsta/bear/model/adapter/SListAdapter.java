package com.sunsta.bear.model.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import com.sunsta.bear.AnApplication;

import java.util.List;

/**
 * 简单通用的ListView 的Adapter。使用很简单。
 */

public class SListAdapter extends BaseAdapter {
    private List<String> listData;

    private int choosePosition;

    public SListAdapter(@NonNull List<String> listData) {
        this.listData = listData;
    }

    public int getChoosePosition() {
        return choosePosition;
    }

    public void setChoosePosition(int choosePosition) {
        this.choosePosition = choosePosition;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public String getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SItemView itemView;
        if (convertView == null) {
            convertView = new SItemView(AnApplication.getApplication());
        }
        itemView = (SItemView) convertView;
        String item = listData.get(position);
        if (item != null) {
            itemView.setItemName(item);
        }
        return convertView;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        if (convertView == null) {
//            holder = new ViewHolder();
//            convertView = mInflater.inflate(R.layout.list_item, null);
//            holder.img = (ImageView) item.findViewById(R.id.img)
//            holder.title = (TextView) item.findViewById(R.id.title);
//            holder.info = (TextView) item.findViewById(R.id.info);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//            holder.img.setImageResource(R.drawable.ic_launcher);
//            holder.title.setText("Hello");
//            holder.info.setText("World");
//        }
//
//        return convertView;
//    }

    /*ListAdapter优化2。*/

//    static class ViewHolder {
//        public ImageView img;
//        public TextView title;
//        public TextView info;
//    }
}
