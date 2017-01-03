package com.han.dlnaplayercontroller.adapter;

import java.util.List;

import com.example.administrator.testdmc.R;
import com.han.dlnaplayercontroller.model.MediaItem;
import com.han.dlnaplayercontroller.utils.UpnpUtil;

import android.content.Context;

public class DirectoryAdapter extends MBaseAdapter {

    private List<MediaItem> items;

    public DirectoryAdapter(Context context, List<MediaItem> datas) {
        super(context);
        this.items = datas;
    }

    public List<MediaItem> getDatas() {
        return items;
    }

    public void refreshData(List<MediaItem> contentItem) {
        this.items = contentItem;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MediaItem item = (MediaItem) getItem(position);
        holder.img.setTag(item);
        holder.txt.setText(item.getTitle());
        if (UpnpUtil.isAudioItem(item)) {
            holder.img.setImageResource(R.drawable.tab_icon_music);
        } else if (UpnpUtil.isVideoItem(item)) {
            holder.img.setImageResource(R.drawable.tab_icon_video);
        } else if (UpnpUtil.isPictureItem(item)) {
            holder.img.setImageResource(R.drawable.tab_icon_pic);
        } else if (UpnpUtil.isStorageFolder(item)) {
            holder.img.setImageResource(R.drawable.ic_menu_archive);
        }
    }

    @Override
    public Object getItemData(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
