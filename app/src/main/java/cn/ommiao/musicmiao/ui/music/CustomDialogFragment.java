package cn.ommiao.musicmiao.ui.music;

import android.app.Dialog;
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
import cn.ommiao.musicmiao.ui.base.BaseActivity;

public class CustomDialogFragment extends DialogFragment {

    private FragmentCustomDialogBinding mBinding;
    private BaseActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_custom_dialog, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        Bundle bundle = getArguments();
        assert bundle != null;
        String title = bundle.getString("title", "提示");
        String content = bundle.getString("content", "No Content!");
        mBinding.tvTitle.setText(title);
        mBinding.tipsContent.setText(content);
        boolean justConfirm = bundle.getBoolean("justConfirm", false);
        String leftBtnStr, rightBtnStr;
        if(justConfirm){
            mBinding.btnLeft.setVisibility(View.GONE);
            rightBtnStr = bundle.getString("rightBtnStr", "确定");
            mBinding.btnRight.setText(rightBtnStr);
            mBinding.btnRight.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        } else {
            leftBtnStr = bundle.getString("leftBtnStr", "确定");
            rightBtnStr = bundle.getString("rightBtnStr", "取消");
            mBinding.btnLeft.setText(leftBtnStr);
            mBinding.btnRight.setText(rightBtnStr);
        }
        mBinding.btnLeft.setOnClickListener(v -> {
            dismiss();
            if(onClickActionListener != null){
                onClickActionListener.onLeftClick();
            }
        });
        mBinding.btnRight.setOnClickListener(v -> {
            dismiss();
            if(onClickActionListener != null){
                onClickActionListener.onRightClick();
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


    private OnClickActionListener onClickActionListener;

    public void setOnClickActionListener(OnClickActionListener onClickActionListener) {
        this.onClickActionListener = onClickActionListener;
    }

    public interface OnClickActionListener{
        void onLeftClick();
        void onRightClick();
    }
}
