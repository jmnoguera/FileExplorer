package com.joselestnh.fileexplorer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static String ROOT_PATH = Environment.getExternalStorageDirectory().toString();
    final static int EXT_PERMISSION = 56;


    private List<File> fileList;
    private File currentDir;

    private FileListAdapter adapter;
    private ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentDir = new File(ROOT_PATH);
        fileList = new ArrayList<>();

        listView = findViewById(R.id.file_list);
        adapter = new FileListAdapter(this,fileList);
        listView.setAdapter(adapter);

        refreshFileList(currentDir);
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File f = fileList.get(position);
                if(f.isDirectory()){
                    currentDir = f.getAbsoluteFile();
                    refreshFileList(currentDir);
                }else if (f.isFile()){
                    //do smth (?)
                }

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentDir.getPath().equals(ROOT_PATH)){
                    currentDir = currentDir.getParentFile();
                refreshFileList(currentDir);
                }
            }
        });

        findViewById(R.id.newFileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MainActivity.this);
                editText.setSingleLine();
                FrameLayout layout = new FrameLayout(MainActivity.this);
                layout.setPadding(50,0,50,0);
                layout.addView(editText);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Create a File")
                        .setMessage("Input the file name")
                        .setView(layout)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File f = new File(currentDir.getPath()+"/"+editText.getText().toString());
                                try {
                                    f.createNewFile();
                                    refreshFileList(currentDir);
                                }catch (Exception e){
                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

            }
        });

        findViewById(R.id.newFolderButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MainActivity.this);
                editText.setSingleLine();
                FrameLayout layout = new FrameLayout(MainActivity.this);
                layout.setPadding(50,0,50,0);
                layout.addView(editText);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Create a Folder")
                        .setMessage("Input the folder name")
                        .setView(layout)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File f = new File(currentDir.getPath()+"/"+editText.getText().toString());
                                if (!f.exists()){
                                    f.mkdir();
                                    refreshFileList(currentDir);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

            }
        });
    }



    private void refreshFileList(@NonNull File path){


        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXT_PERMISSION);
        }else {
            // Do stuff with granted permissions
            fileList = new ArrayList<>();
            List<File> files = new ArrayList<>();
            List<File> directories = new ArrayList<>();

            for (File f : path.listFiles()) {
                if (f.isFile()) {
                    files.add(f);
                } else if (f.isDirectory()) {
                    directories.add(f);
                }
            }
            Collections.sort(files);
            Collections.sort(directories);
            fileList.addAll(directories);
            fileList.addAll(files);
            ((TextView)findViewById(R.id.dirName)).setText(currentDir.getAbsolutePath());


            adapter.inputData(fileList);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.file_rename:
                final File f_rename = fileList.get(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
                final EditText editText = new EditText(MainActivity.this);
                editText.setSingleLine();
                FrameLayout layout = new FrameLayout(MainActivity.this);
                layout.setPadding(50,0,50,0);
                layout.addView(editText);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Rename")
                        .setMessage("Input the new name")
                        .setView(layout)
                        .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File f = new File(currentDir.getPath()+"/"+editText.getText().toString());
                                f_rename.renameTo(f);
                                refreshFileList(currentDir);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                return  true;
            case R.id.file_delete:
                File f_delete = fileList.get(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
                if(f_delete.isDirectory()){
                    deleteRecursive(f_delete);
                    refreshFileList(currentDir);
                }else if(f_delete.isFile()){
                    f_delete.delete();
                    refreshFileList(currentDir);
                }
                return true;
            default:
                return super.onContextItemSelected(item);

        }
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case EXT_PERMISSION:
                refreshFileList(currentDir);
        }
    }

}
