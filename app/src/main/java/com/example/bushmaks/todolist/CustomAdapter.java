package com.example.bushmaks.todolist;

import java.util.ArrayList;
import java.util.TreeSet;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

class CustomAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    final long id_blank = -1;

    private ArrayList<String> mData = new ArrayList<String>();
    private ArrayList<Boolean> mDataBool = new ArrayList<Boolean>();
    private ArrayList<Long> mDataId = new ArrayList<Long>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    public CustomAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final String item, final Boolean isCompleted, final Long id) {
        mData.add(item);
        mDataBool.add(isCompleted);
        mDataId.add(id);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        mData.add(item);
        mDataBool.add(false);
        mDataId.add(id_blank);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }
    public void clear() {
        mDataBool.clear();
        mData.clear();
        mDataId.clear();
        notifyDataSetChanged();
    }
    public void changeBool(boolean bool, long id) {
        mDataBool.set(mDataId.indexOf(id), bool);
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataId.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.cell, null);
                    holder.checkedTextView = (CheckedTextView) convertView.findViewById(R.id.text);
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.layout_header, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (rowType == TYPE_ITEM) {
            holder.checkedTextView.setText(mData.get(position));
            holder.checkedTextView.setChecked(mDataBool.get(position));
            if (mDataBool.get(position)) {
                holder.checkedTextView.setPaintFlags(holder.checkedTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else { // else нужен из-за того, что listview использует recycleview
                holder.checkedTextView.setPaintFlags(holder.checkedTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        } else {
            holder.textView.setText(mData.get(position));
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
        public CheckedTextView checkedTextView;
    }

}