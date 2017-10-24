package com.aipxperts.ecountdown.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.aipxperts.ecountdown.Interface.OnImageClick;
import com.aipxperts.ecountdown.Model.MediaModel;
import com.aipxperts.ecountdown.R;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * Created by aipxperts-ubuntu-01 on 1/6/17.
 */

public class SearchImageCustomAdapter extends BaseAdapter {


    ArrayList<MediaModel> mediaModelList;
    Context context;
    ArrayList<String> imageId;
    private static LayoutInflater inflater = null;
    boolean isclick = false;
    OnImageClick onImageClick;
    ProgressDialog mProgressDialog;
    String cat_name;
    File storeDirectory ;

    public void onImageClick(OnImageClick onImageClick) {
        this.onImageClick = onImageClick;
    }

    int selected_position = 0;

    public SearchImageCustomAdapter(Context context, ArrayList<MediaModel> mediaModelList) {
        // TODO Auto-generated constructor stub
        this.mediaModelList = mediaModelList;
        this.context = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        storeDirectory = new File("/sdcard/Android/data/" + context.getPackageName() + "/");
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mediaModelList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.search_image_list, null);
        holder.img = (ImageView) rowView.findViewById(R.id.img1);
        if(mediaModelList.size()%3==0) {
            Glide.with(context).load(mediaModelList.get(position).media).skipMemoryCache(true).into(holder.img);
        }            holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadImage(position).execute(mediaModelList.get(position).media);
            }
        });

        return rowView;
    }
    private class DownloadImage extends AsyncTask<String, Void, File> {

        int position;
        public DownloadImage(int position) {
            this.position=position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(context);
            // Set progressdialog title
            mProgressDialog.setTitle("Please wait");
            // Set progressdialog message
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected File doInBackground(String... URL) {

            String imageURL = URL[0];
            Bitmap bitmap = null;
            Uri uri=null;
            File file = null;
            try {
                String timeStamp = "/" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + ".jpg";

                bitmap = Glide. with(context). load(mediaModelList.get(position).media). asBitmap(). into(500, 500).get();
                // uri = getImageUri(context,bitmap);
                 file = new File(storeDirectory, timeStamp);
                if (!file.exists()) {
                    file.createNewFile();
                }

//Convert bitmap to byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return file;
        }

        @Override
        protected void onPostExecute(File result) {
            // Set the bitmap into ImageView
            if(result!=null) {
               /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Bitmap b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                String encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);
*/
                onImageClick.OnImageClick(position, result.getPath());
            }else
            {
                onImageClick.OnImageClick(position, "");
                Toast.makeText(context,"Please check internet connection.",Toast.LENGTH_SHORT).show();
            }
            // Close progressdialog
            mProgressDialog.dismiss();
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, UUID.randomUUID().toString() + ".png", "drawing");
        return Uri.parse(path);
    }
}

