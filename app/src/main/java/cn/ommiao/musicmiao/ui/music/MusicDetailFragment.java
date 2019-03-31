package cn.ommiao.musicmiao.ui.music;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.gyf.barlibrary.ImmersionBar;
import com.lauzy.freedom.library.Lrc;
import com.lauzy.freedom.library.LrcHelper;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.SweetSheet;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.bean.Song;
import cn.ommiao.musicmiao.databinding.FragmentMusicDetailBinding;
import cn.ommiao.musicmiao.databinding.LayoutMusicDownloadBinding;
import cn.ommiao.musicmiao.httpcall.lyricsquery.LyricsQueryCall;
import cn.ommiao.musicmiao.httpcall.lyricsquery.model.LyricsQueryIn;
import cn.ommiao.musicmiao.httpcall.lyricsquery.model.LyricsQueryOut;
import cn.ommiao.musicmiao.httpcall.vkey.VkeyCall;
import cn.ommiao.musicmiao.httpcall.vkey.model.VkeyIn;
import cn.ommiao.musicmiao.httpcall.vkey.model.VkeyOut;
import cn.ommiao.musicmiao.ui.base.BaseActivity;
import cn.ommiao.musicmiao.ui.base.BaseFragment;
import cn.ommiao.musicmiao.utils.StringUtil;
import cn.ommiao.musicmiao.utils.ToastUtil;
import cn.ommiao.network.SimpleRequestCallback;

public class MusicDetailFragment extends BaseFragment<FragmentMusicDetailBinding> {

    private static final String SUFFIX_MP3_NQ = "-NQ.mp3";
    private static final String SUFFIX_MP3_HQ = "-HQ.mp3";
    private static final String SUFFIX_FLAC = ".flac";
    private static final String SUFFIX_APE = ".ape";

    private String downloadDir;

    private String tran_name;
    private Song song;
    private SweetSheet mSweetSheet;
    private boolean downloadLinkPrepared = false;
    private String fileName;
    private LayoutMusicDownloadBinding downloadBinding;

    private String downloadResult;

    @Override
    protected void immersionBar() {
        ImmersionBar.with(this).titleBar(mBinding.toolbar).init();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    protected void initViews() {
        Bundle bundle = getArguments();
        assert bundle != null;
        song = (Song) bundle.getSerializable("song");
        tran_name = bundle.getString("tran_name");
        mBinding.toolbatLayout.setTitle(song.getTitle());
        mBinding.playPause.pause();
        mBinding.playPause.setOnClickListener(v -> onPlayPauseClick());
        initDownloadView();
        mBinding.fabDownload.setOnClickListener(v -> onDownloadClick());
        mBinding.toolbar.setNavigationOnClickListener(v -> mActivity.onBackPressed());
    }

    private void initDownloadView() {
        mSweetSheet = new SweetSheet(mBinding.flContainer);
        CustomDelegate customDelegate = new CustomDelegate(false, CustomDelegate.AnimationType.DuangLayoutAnimation, mActivity.getResources().getDimensionPixelSize(R.dimen.music_download_height));
        customDelegate.setCustomView(generateDownloadView());
        customDelegate.setSweetSheetColor(Color.WHITE);
        mSweetSheet.setDelegate(customDelegate);
        mSweetSheet.setBackgroundEffect(new DimEffect(1));
    }

    private View generateDownloadView() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_music_download, null);
        downloadBinding = DataBindingUtil.bind(view);
        assert downloadBinding != null;
        downloadBinding.setSongFile(song.getFile());
        downloadBinding.ivClose.setOnClickListener(v -> closeDownloadView());
        downloadBinding.flMp3Normal.setOnClickListener(v -> onMp3NormalClick());
        downloadBinding.flMp3High.setOnClickListener(v -> onMp3HighClick());
        downloadBinding.flFlac.setOnClickListener(v -> onFlacClick());
        downloadBinding.flApe.setOnClickListener(v -> onApeClick());
        return view;
    }

    @Override
    protected void initData() {
        requestLyrics();
        requestVkey();
    }

    private void initLocalFileStatus() {
        downloadDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music";
        fileName = song.getTitle() + "-" + song.getOneSinger();
        String commonPath = downloadDir + "/" + fileName;
        song.getFile().setHasLocalNqMp3(new File( commonPath + "-NQ" + ".mp3").exists());
        song.getFile().setHasLocalHqMp3(new File( commonPath + "-HQ" + ".mp3").exists());
        song.getFile().setHasLocalFlac(new File( commonPath + ".flac").exists());
        song.getFile().setHasLocalApe(new File( commonPath + ".ape").exists());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_music_detail;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.ivAlbum.setTransitionName(tran_name);
        Picasso.with(getContext())
                .load(song.getAlbumImageUrl())
                .noFade()
                .placeholder(R.drawable.ic_music_s)
                .error(R.drawable.ic_music_s)
                .into(mBinding.ivAlbum, new Callback() {
                    @Override
                    public void onSuccess() {
                        startPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        startPostponedEnterTransition();
                    }
                });
    }

    private void onDownloadClick() {
        initLocalFileStatus();
        if(ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            showNoPermissionTip();
            return;
        }
        if(!mSweetSheet.isShow()){
            mSweetSheet.show();
        }
    }

    private void showNoPermissionTip(){
        CustomDialogFragment customDialogFragment = new CustomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("content", getString(R.string.music_download_no_permission_tips));
        bundle.putBoolean("justConfirm", true);
        customDialogFragment.setArguments(bundle);
        customDialogFragment.setOnClickActionListener(null);
        customDialogFragment.show(mActivity.getSupportFragmentManager(), CustomDialogFragment.class.getSimpleName());
    }

    private void onMp3NormalClick() {
        if(!isDownloadLinkPrepared()){
            return;
        }
        if(!song.getFile().hasNqMp3()){
            ToastUtil.show(R.string.music_download_no_quality);
            return;
        }
        //download start
        downloadSong(song.getMp3NqLink(), SUFFIX_MP3_NQ);
        closeDownloadViewDelay();
    }

    private void onMp3HighClick() {
        if(!isDownloadLinkPrepared()){
            return;
        }
        if(!song.getFile().hasHqMp3()){
            ToastUtil.show(R.string.music_download_no_quality);
            return;
        }
        //download start
        downloadSong(song.getMp3HqLink(), SUFFIX_MP3_HQ);
        closeDownloadViewDelay();
    }

    private void onFlacClick() {
        if(!isDownloadLinkPrepared()){
            return;
        }
        if(!song.getFile().hasFlac()){
            ToastUtil.show(R.string.music_download_no_quality);
            return;
        }
        //download start
        downloadSong(song.getFlacLink(), SUFFIX_FLAC);
        closeDownloadViewDelay();
    }

    private void onApeClick() {
        if(!isDownloadLinkPrepared()){
            return;
        }
        if(!song.getFile().hasApe()){
            ToastUtil.show(R.string.music_download_no_quality);
            return;
        }
        //download start
        downloadSong(song.getApeLink(), SUFFIX_APE);
        closeDownloadViewDelay();
    }

    private void downloadSong(String songUrl, String suffix){
        Uri uri = Uri.parse(songUrl);
        DownloadManager manager = (DownloadManager) mActivity.getSystemService(BaseActivity.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setDestinationInExternalPublicDir("Music", fileName + suffix);
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.allowScanningByMediaScanner();
        request.setTitle(fileName);
        request.setDescription(fileName);
        try {
            manager.enqueue(request);
            downloadResult = getString(R.string.music_download_already_add);
            switch (suffix){
                case SUFFIX_MP3_NQ:
                    song.getFile().setHasLocalNqMp3(true);
                    break;
                case SUFFIX_MP3_HQ:
                    song.getFile().setHasLocalHqMp3(true);
                    break;
                case SUFFIX_FLAC:
                    song.getFile().setHasLocalFlac(true);
                    break;
                case SUFFIX_APE:
                    song.getFile().setHasLocalApe(true);
                    break;
            }
            downloadBinding.setSongFile(song.getFile());
        } catch (Exception e){
            downloadResult = getString(R.string.music_download_add_fail);
        }
    }

    private boolean isDownloadLinkPrepared(){
        if(!downloadLinkPrepared){
            ToastUtil.show(R.string.music_link_initing);
            return false;
        }
        return true;
    }

    private void onPlayPauseClick() {
        if(!isDownloadLinkPrepared()){
            return;
        }
        if(mBinding.playPause.isPlay()){
            mBinding.playPause.pause();
        } else {
            mBinding.playPause.play();
        }
    }

    private void requestLyrics(){
        LyricsQueryIn in = new LyricsQueryIn(song.getMid());
        newCall(new LyricsQueryCall(), in, new SimpleRequestCallback<LyricsQueryOut>() {
            @Override
            public void onSuccess(LyricsQueryOut out) {
                boolean success = StringUtil.writeToFile(mActivity.getExternalCacheDir() + "/lyrics.lrc", out.getDecodeLyrics());
                List<Lrc> lrcs = new ArrayList<>();
                if(success){
                    File lyrics = new File(mActivity.getExternalCacheDir() + "/lyrics.lrc");
                    lrcs = LrcHelper.parseLrcFromFile(lyrics);
                }
                mBinding.lrcView.setLrcData(lrcs);
                mBinding.lrcView.postDelayed(() -> {
                    mBinding.appBar.setExpanded(false);
                }, 500);
            }

            @Override
            public void onError(int code, String error) {
                //ToastUtil.show(getString(R.string.music_lyrics_init_error) + error);
            }
        });
    }

    private void requestVkey() {
        VkeyIn in = new VkeyIn();
        newCall(new VkeyCall(), in, new SimpleRequestCallback<VkeyOut>() {
            @Override
            public void onSuccess(VkeyOut out) {
                if (out.isDataValid()){
                    generateMusicLink(out.getVkey());
                    downloadLinkPrepared = true;
                    Logger.d("mp3 nq: " + song.getMp3NqLink());
                    Logger.d("mp3 hq: " + song.getMp3HqLink());
                    Logger.d("flac: " + song.getFlacLink());
                    Logger.d("ape: " + song.getApeLink());
                }
            }

            @Override
            public void onError(int code, String error) {
                ToastUtil.show(getString(R.string.music_download_init_error) + error);
            }
        });
    }

    private void generateMusicLink(String vkey){

        String mp3NqLink = "http://streamoc.music.tc.qq.com/M500" +
                song.getMid() +
                ".mp3?vkey=" +
                vkey +
                "&guid=00000000736cfed1fffffffff9ffbfd7&uin=0&fromtag=8";
        song.setMp3NqLink(mp3NqLink);

        String mp3HqLink = "http://mobileoc.music.tc.qq.com/M800" +
                song.getMid() +
                ".mp3?vkey=" +
                vkey +
                "&guid=00000000736cfed1fffffffff9ffbfd7&uin=0&fromtag=68";
        song.setMp3HqLink(mp3HqLink);

        String mp3FlacLink = "http://mobileoc.music.tc.qq.com/F000" +
                song.getMid() +
                ".flac?vkey=" +
                vkey +
                "&guid=00000000736cfed1fffffffff9ffbfd7&uin=0&fromtag=63";
        song.setFlacLink(mp3FlacLink);

        String mp3ApeLink = "http://mobileoc.music.tc.qq.com/A000" +
                song.getMid() +
                ".ape?vkey=" +
                vkey +
                "&guid=00000000736cfed1fffffffff9ffbfd7&uin=0&fromtag=8";
        song.setApeLink(mp3ApeLink);
    }

    private void virtualProgress() {
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 1);
        progressAnimator.setDuration(30 * 1000L);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.addUpdateListener(animation -> {
            mBinding.playPause.setProgress((Float) animation.getAnimatedValue());
        });
        progressAnimator.start();
    }

    @Override
    public boolean interceptBackAction() {
        return mSweetSheet.isShow();
    }

    @Override
    public void onBackPressed() {
        closeDownloadView();
    }

    private void closeDownloadView(){
        if(mSweetSheet.isShow()){
            mSweetSheet.dismiss();
        }
    }

    private void closeDownloadViewDelay(){
        if(mSweetSheet.isShow()){
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mActivity.runOnUiThread(() -> {
                    closeDownloadView();
                    mBinding.getRoot().postDelayed(() -> {
                        ToastUtil.show(downloadResult);
                    }, 500);
                });
            }).start();
        }
    }
}
