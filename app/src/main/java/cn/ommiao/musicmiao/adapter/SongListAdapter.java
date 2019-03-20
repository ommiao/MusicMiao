package cn.ommiao.musicmiao.adapter;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

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
        Picasso.with(mContext)
                .load(item.getAlbumImageUrl())
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
        singerAndAlbumSpan.setSpan(new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelSize(R.dimen.music_search_album), false), singerLength + sepLength, singerAndAlbumLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        helper.setText(R.id.tv_music_singer_album, singerAndAlbumSpan);
        ViewCompat.setTransitionName(albumView, item.getMid());
    }
}
