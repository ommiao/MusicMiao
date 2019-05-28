package cn.ommiao.musicmiao.ui.music;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.databinding.FragmentCustomDialogBinding;
import cn.ommiao.musicmiao.databinding.FragmentEditFolderBinding;
import cn.ommiao.musicmiao.ui.base.BaseActivity;
import cn.ommiao.musicmiao.utils.StringUtil;
import cn.ommiao.musicmiao.utils.ToastUtil;

public class FolderEditFragment extends DialogFragment {

    private FragmentEditFolderBinding mBinding;
    private BaseActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_folder, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        SharedPreferences preferences = mActivity.getPreferences(Context.MODE_PRIVATE);
        String content = preferences.getString("DownLoadPath", "MiaoLe/Music");
        mBinding.etContent.setText(content);
        mBinding.etContent.setSelection(content.length());
        mBinding.tvLeft.setOnClickListener(v -> {
            String path = mBinding.etContent.getText().toString().trim();
            if(StringUtil.isEmpty(path)){
                ToastUtil.show("请输入");
                return;
            }
            dismiss();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("DownLoadPath", path);
            editor.apply();
            if(onDoneActionListener != null){
                onDoneActionListener.onConfirmClick(path);
            }
        });
        mBinding.tvRight.setOnClickListener(v -> {
            dismiss();
            if(onDoneActionListener != null){
                onDoneActionListener.onCancelClick();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setCancelable(false);
        Dialog dialog = getDialog();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        attributes.width = screenWidth - getResources().getDimensionPixelSize(R.dimen.dialog_margin) * 2;
        window.setAttributes(attributes);
    }


    private OnDoneActionListener onDoneActionListener;

    public void setOnDoneActionListener(OnDoneActionListener onDoneActionListener) {
        this.onDoneActionListener = onDoneActionListener;
    }

    public interface OnDoneActionListener{
        void onConfirmClick(String path);
        void onCancelClick();
    }
}
