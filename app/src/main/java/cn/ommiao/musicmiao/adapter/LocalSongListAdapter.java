package cn.ommiao.musicmiao.adapter;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.bean.LocalSong;
import cn.ommiao.musicmiao.utils.MusicUtil;
import cn.ommiao.musicmiao.utils.StringUtil;
import cn.ommiao.musicmiao.widget.SquareImageView;

public class LocalSongListAdapter extends BaseQuickAdapter<LocalSong, BaseViewHolder> {

    public LocalSongListAdapter(int layoutResId, @Nullable List<LocalSong> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LocalSong item) {
        SquareImageView albumView = helper.getView(R.id.siv_music_album);
        String albumStr = MusicUtil.getAlbumResource(albumView.getContext(), item.getAlbumId());
        Picasso.with(mContext)
                .load(new File(albumStr))
                .placeholder(R.drawable.ic_music_s)
                .error(R.drawable.ic_music_s)
                .into(albumView);
        TextView titleView = helper.getView(R.id.tv_music_title);
        titleView.setTextColor(ContextCompat.getColor(titleView.getContext(), R.color.colorPrimaryLocal));
        titleView.setText(item.getName());
        String singer = StringUtil.isEmpty(item.getSinger()) ? mContext.getString(R.string.music_no_singer) : item.getSinger();
        int singerLength = singer.length();
        String sep = " / ";
        int sepLength = sep.length();
        String album = StringUtil.isEmpty(item.getAlbum()) ? mContext.getString(R.string.music_no_album) : item.getAlbum();
        int albumLength = album.length();
        String singerAndAlbum = singer + sep + album;
        int singerAndAlbumLength = singerAndAlbum.length();
        SpannableString singerAndAlbumSpan = new SpannableString(singerAndAlbum);
        singerAndAlbumSpan.setSpan(new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelSize(R.dimen.music_search_album), false), singerLength + sepLength, singerAndAlbumLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        helper.setText(R.id.tv_music_singer_album, singerAndAlbumSpan);
    }
}
