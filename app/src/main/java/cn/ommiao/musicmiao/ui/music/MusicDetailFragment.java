package cn.ommiao.musicmiao.ui.music;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;

import com.arialyy.aria.core.Aria;
import com.gyf.barlibrary.ImmersionBar;
import com.lauzy.freedom.library.Lrc;
import com.lauzy.freedom.library.LrcHelper;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.SweetSheet;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.bean.SongTask;
import cn.ommiao.musicmiao.bean.Song;
import cn.ommiao.musicmiao.bean.moresound.Api;
import cn.ommiao.musicmiao.databinding.FragmentMusicDetailBinding;
import cn.ommiao.musicmiao.databinding.LayoutMusicDownloadBinding;
import cn.ommiao.musicmiao.httpcall.lyricsquery.LyricsQueryCall;
import cn.ommiao.musicmiao.httpcall.lyricsquery.model.LyricsQueryIn;
import cn.ommiao.musicmiao.httpcall.lyricsquery.model.LyricsQueryOut;
import cn.ommiao.musicmiao.httpcall.moresound.songinfo.SongInfoCall;
import cn.ommiao.musicmiao.httpcall.moresound.songinfo.model.SongInfoIn;
import cn.ommiao.musicmiao.httpcall.moresound.songinfo.model.SongInfoOut;
import cn.ommiao.musicmiao.httpcall.moresound.url.UrlCall;
import cn.ommiao.musicmiao.httpcall.moresound.url.model.UrlIn;
import cn.ommiao.musicmiao.httpcall.moresound.url.model.UrlOut;
import cn.ommiao.musicmiao.httpcall.vkey.VkeyCall;
import cn.ommiao.musicmiao.httpcall.vkey.model.VkeyIn;
import cn.ommiao.musicmiao.httpcall.vkey.model.VkeyOut;
import cn.ommiao.musicmiao.interfaces.DownloadActionListener;
import cn.ommiao.musicmiao.ui.base.BaseFragment;
import cn.ommiao.musicmiao.utils.StringUtil;
import cn.ommiao.musicmiao.utils.ToastUtil;
import cn.ommiao.network.SimpleRequestCallback;

public class MusicDetailFragment extends BaseFragment<FragmentMusicDetailBinding> {

    private static String MUSIC_DOWNLOAD_PATH = "MiaoLe/Music";

    private static final String SUFFIX_MP3_NQ = "-NQ.mp3";
    private static final String SUFFIX_MP3_HQ = "-HQ.mp3";
    private static final String SUFFIX_FLAC = ".flac";
    private static final String SUFFIX_APE = ".ape";

    private String tran_name;
    private Song song;
    private SweetSheet mSweetSheet;
    private boolean downloadLinkPrepared = false;
    private boolean listenLinkPrepared = false;
    private String fileName;
    private LayoutMusicDownloadBinding downloadBinding;

    private String downloadResult;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private WifiManager.WifiLock wifiLock;
    private Runnable progressRunnable;

    private DownloadActionListener downloadActionListener;
    private Api downloadApi;

    public void setDownloadActionListener(DownloadActionListener downloadActionListener) {
        this.downloadActionListener = downloadActionListener;
    }

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
        mBinding.lrcView.setEmptyContent(getString(R.string.music_lyrics_loading));
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
        downloadBinding.ivClose.setOnClickListener(v -> closeDownloadView());
        downloadBinding.ivFolder.setOnClickListener(v -> showEditFolderView());
        downloadBinding.flMp3Normal.setOnClickListener(v -> onMp3NormalClick());
        downloadBinding.flMp3High.setOnClickListener(v -> onMp3HighClick());
        downloadBinding.flFlac.setOnClickListener(v -> onFlacClick());
        downloadBinding.flApe.setOnClickListener(v -> onApeClick());
        return view;
    }

    @Override
    protected void initData() {
        SharedPreferences preferences = mActivity.getPreferences(Context.MODE_PRIVATE);
        MUSIC_DOWNLOAD_PATH = preferences.getString("DownLoadPath", "MiaoLe/Music");
        requestLyrics();
        requestDownloadApi();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void initLocalFileStatus() {
        if(!Environment.getExternalStoragePublicDirectory(MUSIC_DOWNLOAD_PATH).exists()){
            Environment.getExternalStoragePublicDirectory(MUSIC_DOWNLOAD_PATH).mkdirs();
        }
        String downloadDir = Environment.getExternalStoragePublicDirectory(MUSIC_DOWNLOAD_PATH).getAbsolutePath();
        fileName = song.getTitle() + "-" + song.getOneSinger();
        String commonPath = downloadDir + "/" + fileName;
        song.getFile().setHasLocalNqMp3(new File(commonPath + SUFFIX_MP3_NQ).exists());
        song.getFile().setHasLocalHqMp3(new File(commonPath + SUFFIX_MP3_HQ).exists());
        song.getFile().setHasLocalFlac(new File(commonPath + SUFFIX_FLAC).exists());
        song.getFile().setHasLocalApe(new File(commonPath + SUFFIX_APE).exists());
        downloadBinding.setSongFile(song.getFile());
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
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            showNoPermissionTip();
            return;
        }
        if (!mSweetSheet.isShow()) {
            mSweetSheet.show();
        }
    }

    private void showNoPermissionTip() {
        CustomDialogFragment customDialogFragment = new CustomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("content", getString(R.string.music_download_no_permission_tips));
        bundle.putBoolean("justConfirm", true);
        customDialogFragment.setArguments(bundle);
        customDialogFragment.setOnClickActionListener(null);
        customDialogFragment.show(mActivity.getSupportFragmentManager(), CustomDialogFragment.class.getSimpleName());
    }

    private void onMp3NormalClick() {
        if (!song.getFile().hasNqMp3()) {
            ToastUtil.show(R.string.music_download_no_quality);
            return;
        }
        if(isDownloadingOrDownloaded(SUFFIX_MP3_NQ)){
            return;
        }
        if (!isDownloadLinkPrepared()) {
            return;
        }
        if(!listenLinkPrepared){
            ToastUtil.show(R.string.music_link_initing);
            return;
        }
        //download start
        downloadSong(song.getMp3NqLink(), SUFFIX_MP3_NQ);
    }

    private void onMp3HighClick() {
        if (!song.getFile().hasHqMp3()) {
            ToastUtil.show(R.string.music_download_no_quality);
            return;
        }
        if(isDownloadingOrDownloaded(SUFFIX_MP3_HQ)){
            return;
        }
        if (!isDownloadLinkPrepared()) {
            return;
        }
        if(isRequestingLink()){
            ToastUtil.show("正在添加下载，请稍候");
            return;
        }
        //download start
        requestHqMp3Link();
    }

    private void onFlacClick() {
        if (!song.getFile().hasFlac()) {
            ToastUtil.show(R.string.music_download_no_quality);
            return;
        }
        if(isDownloadingOrDownloaded(SUFFIX_FLAC)){
            return;
        }
        if (!isDownloadLinkPrepared()) {
            return;
        }
        if(isRequestingLink()){
            ToastUtil.show("正在添加下载，请稍候");
            return;
        }
        //download start
        requestFlacLink();
    }

    private void onApeClick() {
        if (!song.getFile().hasApe()) {
            ToastUtil.show(R.string.music_download_no_quality);
            return;
        }
        if(isDownloadingOrDownloaded(SUFFIX_APE)){
            return;
        }
        if (!isDownloadLinkPrepared()) {
            return;
        }
        if(isRequestingLink()){
            ToastUtil.show("正在添加下载，请稍候");
            return;
        }
        //download start
        requestApeLink();
    }

    private boolean isDownloadingOrDownloaded(String suffix){
        boolean hasDownloaded = false;
        switch (suffix) {
            case SUFFIX_MP3_NQ:
                hasDownloaded = song.getFile().isHasLocalNqMp3();
                break;
            case SUFFIX_MP3_HQ:
                hasDownloaded = song.getFile().isHasLocalHqMp3();
                break;
            case SUFFIX_FLAC:
                hasDownloaded = song.getFile().isHasLocalFlac();
                break;
            case SUFFIX_APE:
                hasDownloaded = song.getFile().isHasLocalApe();
                break;
        }
        if(downloadActionListener != null && (downloadActionListener.isTaskExists(fileName + suffix) || hasDownloaded)){
            showAlreadyExistsDialog();
            return true;
        } else {
            return false;
        }
    }

    private void downloadSong(String songUrl, String suffix) {
        List<SongTask> records = LitePal.where("displayName = ?", fileName + suffix).find(SongTask.class);
        for(SongTask record : records){
            Aria.download(mActivity)
                    .load(record.getUrl())
                    .removeRecord();
        }
        String localPath = Environment.getExternalStoragePublicDirectory(MUSIC_DOWNLOAD_PATH).getAbsolutePath() + "/" + fileName + suffix;
        if(downloadActionListener != null){
            SongTask task = new SongTask();
            task.setUrl(songUrl);
            task.setDisplayName(fileName + suffix);
            task.setLocalPath(localPath);
            task.setDownoadingSize(0);
            task.setTotalSize(0);
            task.setDownloadStatus(SongTask.DOWNLOAD_STATUS_WAIT);
            task.save();
            downloadActionListener.onAddDownloadTask(task);
        }
        Aria.download(mActivity)
                .load(songUrl)
                .setFilePath(localPath)
                .start();
        downloadResult = getString(R.string.music_download_already_add);
        switch (suffix) {
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
        closeDownloadViewDelay();
    }

    private void showAlreadyExistsDialog() {
        CustomDialogFragment customDialogFragment = new CustomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("content", getString(R.string.music_download_already_exists));
        bundle.putBoolean("justConfirm", true);
        customDialogFragment.setArguments(bundle);
        customDialogFragment.setOnClickActionListener(null);
        customDialogFragment.show(mActivity.getSupportFragmentManager(), CustomDialogFragment.class.getSimpleName());
    }

    private boolean isDownloadLinkPrepared() {
        if (!downloadLinkPrepared) {
            ToastUtil.show(R.string.music_link_initing);
            return false;
        }
        return true;
    }

    private void onPlayPauseClick() {
        if (!isDownloadLinkPrepared()) {
            return;
        }
        if(!listenLinkPrepared){
            ToastUtil.show("试听链接正在初始化");
            return;
        }
        if (mBinding.playPause.isPlay()) {
            pauseMusic();
        } else {
            playMusic();
        }
    }

    private void playMusic() {
        mBinding.playPause.play();
        if (mediaPlayer == null || mediaPlayer.getCurrentPosition() == 0) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(song.getMp3NqLink());
                mediaPlayer.setLooping(false);
                mediaPlayer.setWakeMode(mActivity, PowerManager.PARTIAL_WAKE_LOCK);
                if(wifiLock != null){
                    wifiLock.release();
                }
                wifiLock = ((WifiManager)mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "wifilock");
                wifiLock.acquire();
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> {
                    mp.start();
                    startProgressRunable();
                });
                mediaPlayer.setOnCompletionListener(mp -> {
                    mBinding.playPause.setProgress(1f);
                    mediaPlayer.seekTo(0);
                    mediaPlayer.pause();
                });
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    playError();
                    return true;
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mediaPlayer.start();
        }
    }

    private void playError(){
        handler.removeCallbacks(progressRunnable);
        ToastUtil.show(R.string.music_play_fail);
        mBinding.playPause.error();
        mBinding.lrcView.updateTime(0);
        mediaPlayer = null;
    }

    private void startProgressRunable() {
        handler.removeCallbacks(progressRunnable);
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    mBinding.lrcView.updateTime(currentPosition);
                    float progress = (float) mediaPlayer.getCurrentPosition() / (float)mediaPlayer.getDuration();
                    mBinding.playPause.setProgress(progress);
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.post(progressRunnable);
    }

    private void pauseMusic() {
        mBinding.playPause.pause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void requestLyrics() {
        LyricsQueryIn in = new LyricsQueryIn(song.getMid());
        newCall(new LyricsQueryCall(), in, new SimpleRequestCallback<LyricsQueryOut>() {
            @Override
            public void onSuccess(LyricsQueryOut out) {
                boolean success = StringUtil.writeToFile(mActivity.getExternalCacheDir() + "/lyrics.lrc", out.getDecodeLyrics());
                List<Lrc> lrcs = new ArrayList<>();
                if (success) {
                    File lyrics = new File(mActivity.getExternalCacheDir() + "/lyrics.lrc");
                    lrcs = LrcHelper.parseLrcFromFile(lyrics);
                }
                mBinding.lrcView.setLrcData(lrcs);
                mBinding.lrcView.postDelayed(() -> {
                    if(isAdded()){
                        mBinding.appBar.setExpanded(false);
                    }
                }, 500);
            }

            @Override
            public void onError(int code, String error) {
                mBinding.lrcView.setEmptyContent(getString(R.string.music_lyrics_none));
            }
        });
    }

    private void requestVkey() {
        VkeyIn in = new VkeyIn();
        newCall(new VkeyCall(), in, new SimpleRequestCallback<VkeyOut>() {
            @Override
            public void onSuccess(VkeyOut out) {
                if (out.isDataValid()) {
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

    private void requestDownloadApi(){
        SongInfoIn in = new SongInfoIn(song.getMid());
        newCall(new SongInfoCall(), in, new SimpleRequestCallback<SongInfoOut>() {
            @Override
            public void onSuccess(SongInfoOut out) {
                downloadApi = out.getApi();
                downloadLinkPrepared = true;
                Logger.d(out.getApi().getMp3Nq());
                Logger.d(out.getApi().getMp3Hq());
                Logger.d(out.getApi().getFlac());
                Logger.d(out.getApi().getApe());
                requestListenLink();
            }

            @Override
            public void onError(int code, String error) {
                ToastUtil.show(getString(R.string.music_download_init_error) + error);
            }
        });
    }

    private void requestListenLink(){
        UrlIn mp3NqIn = new UrlIn(downloadApi.getMp3Nq());
        newCall(new UrlCall(), mp3NqIn, new SimpleRequestCallback<UrlOut>() {
            @Override
            public void onSuccess(UrlOut out) {
                song.setMp3NqLink(out.getUrl());
                listenLinkPrepared = true;
            }

            @Override
            public void onError(int code, String error) {
                ToastUtil.show("普通音质mp3链接初始化失败！");
            }
        });
    }

    private boolean isRequestingLink(){
        return downloadBinding.progressBar.getVisibility() == View.VISIBLE;
    }

    private void requestHqMp3Link(){
        downloadBinding.progressBar.setVisibility(View.VISIBLE);
        UrlIn mp3HqIn = new UrlIn(downloadApi.getMp3Hq());
        newCall(new UrlCall(), mp3HqIn, new SimpleRequestCallback<UrlOut>() {
            @Override
            public void onSuccess(UrlOut out) {
                song.setMp3HqLink(out.getUrl());
                downloadSong(song.getMp3HqLink(), SUFFIX_MP3_HQ);
                downloadBinding.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(int code, String error) {
                ToastUtil.show("高音质mp3链接初始化失败！");
                downloadBinding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void requestFlacLink(){
        downloadBinding.progressBar.setVisibility(View.VISIBLE);
        UrlIn flacIn = new UrlIn(downloadApi.getFlac());
        newCall(new UrlCall(), flacIn, new SimpleRequestCallback<UrlOut>() {
            @Override
            public void onSuccess(UrlOut out) {
                song.setFlacLink(out.getUrl());
                downloadSong(song.getFlacLink(), SUFFIX_FLAC);
                downloadBinding.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(int code, String error) {
                ToastUtil.show("flac链接初始化失败！");
                downloadBinding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void requestApeLink(){
        downloadBinding.progressBar.setVisibility(View.VISIBLE);
        UrlIn apeIn = new UrlIn(downloadApi.getApe());
        newCall(new UrlCall(), apeIn, new SimpleRequestCallback<UrlOut>() {
            @Override
            public void onSuccess(UrlOut out) {
                song.setApeLink(out.getUrl());
                downloadSong(song.getApeLink(), SUFFIX_APE);
                downloadBinding.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(int code, String error) {
                ToastUtil.show("ape链接初始化失败！");
                downloadBinding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void generateMusicLink(String vkey) {

        String mp3NqLink = "http://streamoc.music.tc.qq.com/M500" +
                song.getFile().getStrMediaMid() +
                ".mp3?vkey=" +
                vkey +
                "&guid=00000000736cfed1fffffffff9ffbfd7&uin=0&fromtag=8";
        song.setMp3NqLink(mp3NqLink);

        String mp3HqLink = "http://mobileoc.music.tc.qq.com/M800" +
                song.getFile().getStrMediaMid() +
                ".mp3?vkey=" +
                vkey +
                "&guid=00000000736cfed1fffffffff9ffbfd7&uin=0&fromtag=68";
        song.setMp3HqLink(mp3HqLink);

        String mp3FlacLink = "http://mobileoc.music.tc.qq.com/F000" +
                song.getFile().getStrMediaMid() +
                ".flac?vkey=" +
                vkey +
                "&guid=00000000736cfed1fffffffff9ffbfd7&uin=0&fromtag=63";
        song.setFlacLink(mp3FlacLink);

        String mp3ApeLink = "http://mobileoc.music.tc.qq.com/A000" +
                song.getFile().getStrMediaMid() +
                ".ape?vkey=" +
                vkey +
                "&guid=00000000736cfed1fffffffff9ffbfd7&uin=0&fromtag=8";
        song.setApeLink(mp3ApeLink);
    }

    @Override
    public boolean interceptBackAction() {
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mSweetSheet.isShow()){
            closeDownloadView();
        } else {
            assert getFragmentManager() != null;
            mActivity.setOnBackPressedListener(null);
            getFragmentManager().popBackStack();
        }
    }

    private void closeDownloadView() {
        if (mSweetSheet.isShow()) {
            mSweetSheet.dismiss();
        }
    }

    private void closeDownloadViewDelay() {
        if (mSweetSheet.isShow()) {
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

    @Override
    public void onDestroyView() {
        if(mediaPlayer != null){
            mediaPlayer.release();
        }
        if(wifiLock != null){
            wifiLock.release();
        }
        handler.removeCallbacks(progressRunnable);
        super.onDestroyView();
    }

    private void showEditFolderView(){
        FolderEditFragment folderEditFragment = new FolderEditFragment();
        folderEditFragment.setOnDoneActionListener(new FolderEditFragment.OnDoneActionListener(){

            @Override
            public void onConfirmClick(String path) {
                MUSIC_DOWNLOAD_PATH = path;
                initLocalFileStatus();
            }

            @Override
            public void onCancelClick() {

            }
        });
        folderEditFragment.show(mActivity.getSupportFragmentManager(), FolderEditFragment.class.getSimpleName());
    }
}
