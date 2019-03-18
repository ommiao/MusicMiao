package cn.ommiao.musicmiao.ui.tab;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.hanks.htextview.base.AnimationListener;
import com.hanks.htextview.base.HTextView;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.databinding.FragmentSearchBinding;
import cn.ommiao.musicmiao.ui.BaseFragment;
import cn.ommiao.musicmiao.utils.StringUtil;
import cn.ommiao.musicmiao.utils.ToastUtil;

public class SearchFragment extends BaseFragment<FragmentSearchBinding> implements View.OnClickListener, AnimationListener, TextView.OnEditorActionListener {

    private boolean isSearchEditViewShow = false;

    private String keywords;

    @Override
    protected void immersionBar() {
        ImmersionBar.with(this).titleBar(mBinding.llTitleBar).init();
    }

    @Override
    protected void initViews() {
        mBinding.ivCat.setOnClickListener(this);
        mBinding.ivSearch.setOnClickListener(this);
        mBinding.etSearch.setOnEditorActionListener(this);
        mBinding.stvTitle.setAnimationListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_cat:
                onCatClick();
                break;
            case R.id.iv_search:
                onSearchClick();
                break;
        }
    }

    private void onCatClick() {
        if(isSearchEditViewShow && StringUtil.isEmpty(mBinding.etSearch.getText().toString())){
            closeKeyboard();
            mBinding.etSearch.setText("");
            mBinding.stvTitle.setVisibility(View.VISIBLE);
            mBinding.etSearch.setVisibility(View.GONE);
            toggleSearchEditView();
        }
    }

    public void onSearchClick() {
        if(!isSearchEditViewShow){
            toggleSearchEditView();
        }
    }

    private void toggleSearchEditView(){
        isSearchEditViewShow = !isSearchEditViewShow;
        final int[] stateSet = {android.R.attr.state_checked * (isSearchEditViewShow ? 1 : -1)};
        mBinding.ivSearch.setImageState(stateSet, true);
        mBinding.stvTitle.animateText(isSearchEditViewShow ? getString(R.string.search_hint) : getString(R.string.search_title));
    }

    @Override
    public void onAnimationEnd(HTextView hTextView) {
        if(isSearchEditViewShow){
            mBinding.stvTitle.setVisibility(View.INVISIBLE);
            mBinding.etSearch.setVisibility(View.VISIBLE);
            mBinding.etSearch.setFocusable(true);
            mBinding.etSearch.setFocusableInTouchMode(true);
            mBinding.etSearch.requestFocus();
            openKeyboard();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_SEARCH){
            keywords = mBinding.etSearch.getText().toString();
            if(isDataChecked()){
                search();
            }
        }
        return true;
    }

    private void search() {
        closeKeyboard();
        mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    private boolean isDataChecked() {
        if(StringUtil.isEmpty(keywords)){
            ToastUtil.show(R.string.search_input_keywords);
            return false;
        }
        return true;
    }

    private void openKeyboard(){
        InputMethodManager imm =
                (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(mBinding.etSearch, 0);
    }

    private void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(mActivity.getWindow().getDecorView().getWindowToken(), 0);
    }
}
