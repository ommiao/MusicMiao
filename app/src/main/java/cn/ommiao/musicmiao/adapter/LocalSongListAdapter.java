package cn.ommiao.musicmiao.adapter;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import org.litepal.LitePal;

import java.util.List;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.bean.LocalSong;
import cn.ommiao.musicmiao.bean.Song;
import cn.ommiao.musicmiao.httpcall.musicsearch.MusicSearchCall;
import cn.ommiao.musicmiao.httpcall.musicsearch.model.MusicSearchIn;
import cn.ommiao.musicmiao.httpcall.musicsearch.model.MusicSearchOut;
import cn.ommiao.musicmiao.utils.StringUtil;
import cn.ommiao.musicmiao.widget.SquareImageView;
import cn.ommiao.musicmiao.widget.playpause.PlayingView;
import cn.ommiao.network.HttpCall;
import cn.ommiao.network.SimpleRequestCallback;

public class LocalSongListAdapter extends BaseQuickAdapter<LocalSong, BaseViewHolder> {

    private HttpCall httpCall;

    public LocalSongListAdapter(int layoutResId, @Nullable List<LocalSong> data, @NonNull HttpCall httpCall) {
        super(layoutResId, data);
        this.httpCall = httpCall;
    }

    @Override
    protected void convert(BaseViewHolder helper, LocalSong item) {
        boolean isPlaying = item.isPlaying();
        SquareImageView albumView = helper.getView(R.id.siv_music_album);
        List<LocalSong> songsInDb = LitePal.where("path = ?", item.getPath()).find(LocalSong.class);
        if(songsInDb != null && songsInDb.size() > 0){
            item = songsInDb.get(0);
            item.setPlaying(isPlaying);
        } else {
            MusicSearchIn in = new MusicSearchIn(item.getTitle(), 1, 1);
            LocalSong finalItem = item;
            httpCall.newCall(new MusicSearchCall(), in, new SimpleRequestCallback<MusicSearchOut>() {
                @Override
                public void onSuccess(MusicSearchOut out) {
                    Song song = out.getSongs().get(0);
                    if(song != null){
                        loadAlumImage(song.getAlbumImageUrl(), albumView);
                        finalItem.setAlbumUrl(song.getAlbumImageUrl());
                        finalItem.save();
                    }
                }

                @Override
                public void onError(int code, String error) {
                    //do nothing
                }
            });
        }
        loadAlumImage(item.getAlbumUrl(), albumView);
        helper.setText(R.id.tv_music_title, item.getTitle());
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
        PlayingView playingView = helper.getView(R.id.playing);
        if(item.isPlaying()){
            playingView.setVisibility(View.VISIBLE);
            playingView.start();
        } else {
            playingView.stop();
            playingView.setVisibility(View.GONE);
        }
    }

    private void loadAlumImage(String imgUrl, ImageView target){
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter grayColorFilter = new ColorMatrixColorFilter(cm);
        target.setColorFilter(grayColorFilter);
        Picasso.with(mContext)
                .load(imgUrl)
                .placeholder(R.drawable.ic_music_s)
                .error(R.drawable.ic_music_s)
                .into(target);
    }
}
