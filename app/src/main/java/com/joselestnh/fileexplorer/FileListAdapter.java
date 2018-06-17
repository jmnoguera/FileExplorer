package com.joselestnh.fileexplorer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Date;
import java.util.List;

public class FileListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<File> fileList;


    public FileListAdapter(Context context, List<File> fileList){
        this.context = context;
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listView = convertView;

        if(listView == null){
            this.inflater = (LayoutInflater) this.context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            listView = this.inflater.inflate(R.layout.file_layout, null);
        }

        File currentFile = fileList.get(position);
        ImageView imageView = listView.findViewById(R.id.icon);
        if(currentFile.isDirectory()){
            imageView.setImageResource(R.drawable.folder);
        }else if(currentFile.isFile()){
            imageView.setImageResource(R.drawable.file);
        }

        ((TextView)listView.findViewById(R.id.fileName)).setText(currentFile.getName());
        ((TextView)listView.findViewById(R.id.infoText)).setText((new Date(currentFile.lastModified()).toString()));


        return listView;
    }

    public  void inputData(List<File> fileList){
        this.fileList = fileList;
        notifyDataSetChanged();
    }
}
