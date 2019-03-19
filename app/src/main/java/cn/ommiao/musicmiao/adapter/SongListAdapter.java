package cn.ommiao.musicmiao.adapter;

import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.bean.Song;
import cn.ommiao.musicmiao.widget.SquareImageView;

public class SongListAdapter extends BaseQuickAdapter<Song, BaseViewHolder> {

    public SongListAdapter(int layoutResId, @Nullable List<Song> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Song item) {
        SquareImageView album = helper.getView(R.id.siv_music_album);
        Glide.with(mContext).load(item.getAlbumImageUrl())
                .placeholder(R.drawable.pic_music_album)
                .error(R.drawable.pic_music_album)
                .into(album);
        helper.setText(R.id.tv_music_title, item.getTitle());
        helper.setText(R.id.tv_music_singer, item.getOneSinger());
        helper.setText(R.id.tv_music_album, item.getAlbum().getTitle());
    }
}
