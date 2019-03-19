package cn.ommiao.musicmiao.adapter;

import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.bean.Song;
import cn.ommiao.musicmiao.utils.StringUtil;
import cn.ommiao.musicmiao.widget.SquareImageView;

public class SongListAdapter extends BaseQuickAdapter<Song, BaseViewHolder> {

    public SongListAdapter(int layoutResId, @Nullable List<Song> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Song item) {
        SquareImageView albumView = helper.getView(R.id.siv_music_album);
        Glide.with(mContext).load(item.getAlbumImageUrl())
                .placeholder(R.drawable.pic_music_album)
                .error(R.drawable.pic_music_album)
                .into(albumView);
        helper.setText(R.id.tv_music_title, item.getTitle());
        String singer = StringUtil.isEmpty(item.getOneSinger()) ? mContext.getString(R.string.music_no_singer) : item.getOneSinger();
        int singerLength = singer.length();
        String sep = " / ";
        int sepLength = sep.length();
        String album = StringUtil.isEmpty(item.getAlbum().getTitle()) ? mContext.getString(R.string.music_no_album) : item.getAlbum().getTitle();
        int albumLength = album.length();
        String singerAndAlbum = singer + sep + album;
        int singerAndAlbumLength = singerAndAlbum.length();
        SpannableString singerAndAlbumSpan = new SpannableString(singerAndAlbum);
        singerAndAlbumSpan.setSpan(new AbsoluteSizeSpan(13, true), singerLength + sepLength, singerAndAlbumLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        helper.setText(R.id.tv_music_singer_album, singerAndAlbumSpan);
    }
}
