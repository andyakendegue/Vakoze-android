/*
 * Copyright 2014-2016 Media for Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vakoze.video.core_process;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.vakoze.video.core_process.controls.TimelineItem;

public class ActivityWithTimeline extends Activity implements TimelineItem.TimelineItemEvents {
    protected static final int IMPORT_FROM_GALLERY_REQUEST = 1;

    TimelineItem mItemToPick;

    @Override
    public void onOpen(TimelineItem item) {
        mItemToPick = item;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);

        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        startActivityForResult(intent, IMPORT_FROM_GALLERY_REQUEST);
    }

    @Override
    public void onDelete(TimelineItem item) {
        item.setMediaUri(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {

            case IMPORT_FROM_GALLERY_REQUEST: {
                if (resultCode == RESULT_OK) {
                    Uri selectedVideo = intent.getData();

                    if (selectedVideo == null) {
                        showToast("Invalid URI.");
                        return;
                    }

                    Cursor cursor = getContentResolver().query(selectedVideo, null, null, null, null);

                    if (cursor != null) {
                        cursor.moveToFirst();

                        int idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME);

                        if (idx != -1) {
                            String displayName = cursor.getString(idx);

                            mItemToPick.setMediaFileName(getPath(selectedVideo));

                            org.m4m.Uri uri = new org.m4m.Uri(selectedVideo.toString());

                            try {
                                mItemToPick.setMediaUri(uri);
                            } catch (IllegalArgumentException ex) {
                                showToast(ex.getMessage());
                            }
                        } else {
                            showToast("Error while importing video from gallery.");
                        }

                        cursor.close();
                    }
                }
            }
            break;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    public void showToast(String title) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
    }

    public void showMessageBox(String message, DialogInterface.OnClickListener listener) {

        if (message == null) {
            message = "";
        }

        if (listener == null) {
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            };
        }

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage(message);
        b.setPositiveButton("OK", listener);
        AlertDialog d = b.show();

        ((TextView) d.findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
    }
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

}
