package com.sunsta.bear.engine.picker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sunsta.bear.R;
import com.sunsta.bear.engine.picker.widget.BasePickerView;
import com.sunsta.bear.engine.picker.widget.WheelOptions;
import com.sunsta.bear.immersion.RichTextView;

import java.util.ArrayList;


/**
 * 请关注个人知乎bgwan， 在【an框架】专栏会有详细的使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：条件选择器 && OptionsPickerView 原始名字  && term英文条件
 * <a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以通过关注我的知乎获取更详细的信息</a>
 * <h3>版权声明：(C) 2016 The Android Developer Sunst</h3>
 * <br>创建日期：2015/11/22
 * <br>邮件email：qyddai@gmail.com
 * <br>个人Github：https://qydq.github.io
 * <p>--#---- Revision History:  --- >  : |version|date|updateinfo|----#--
 * @author sunst
 * @version 1.0 |   2015/11/22           |   条件选择器
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class TermPickerView<T> extends BasePickerView implements View.OnClickListener {
    private Context mContext;

    private OnOptionsSelectListener mOptionsSelectListener;
    private WheelOptions<T> mWheelOptions;
    private Button mBtnSubmit, mBtnCancel;
    private TextView mTxtTitle;
    private View mHeadView;

    public TermPickerView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.base_pickerview_options, contentContainer);
        mWheelOptions = new WheelOptions<>(findViewById(R.id.optionspicker));
        mHeadView = findViewById(R.id.rlt_head_view);
        mTxtTitle = (TextView) findViewById(R.id.tvTitle);
        mBtnSubmit = (Button) findViewById(R.id.btnSubmit);
        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnSubmit.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    /**
     * 设置一级数据
     */
    public void setPicker(ArrayList<T> optionsItems) {
        mWheelOptions.setPicker(optionsItems, null, null, false);
    }

    /**
     * 设置二级数据
     */
    public void setPicker(ArrayList<T> options1Items, ArrayList<ArrayList<T>> options2Items, boolean linkage) {
        mWheelOptions.setPicker(options1Items, options2Items, null, linkage);
    }

    /**
     * 设置三级数据
     */
    public void setPicker(ArrayList<T> options1Items, ArrayList<ArrayList<T>> options2Items
            , ArrayList<ArrayList<ArrayList<T>>> options3Items, boolean linkage) {
        mWheelOptions.setPicker(options1Items, options2Items, options3Items, linkage);
    }

    /**
     * 设置选中的item位置
     * @param option1 位置
     */
    public void setSelectOptions(int option1) {
        mWheelOptions.setCurrentItems(option1, 0, 0);
    }

    public void setSelectOptions(int option1, int option2) {
        mWheelOptions.setCurrentItems(option1, option2, 0);
    }

    public void setSelectOptions(int option1, int option2, int option3) {
        mWheelOptions.setCurrentItems(option1, option2, option3);
    }

    /**
     * 设置选项的单位
     * @param label1 单位
     */
    public void setLabels(String label1) {
        mWheelOptions.setLabels(label1, null, null);
    }

    public void setLabels(String label1, String label2) {
        mWheelOptions.setLabels(label1, label2, null);
    }

    public void setLabels(String label1, String label2, String label3) {
        mWheelOptions.setLabels(label1, label2, label3);
    }

    /**
     * 设置是否循环滚动
     */
    public void setCyclic(boolean cyclic) {
        mWheelOptions.setCyclic(cyclic);
    }

    public void setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3) {
        mWheelOptions.setCyclic(cyclic1, cyclic2, cyclic3);
    }

    /**
     * 设置头部背景颜色
     */
    public void setHeadBackgroundColor(int color) {
        mHeadView.setBackgroundColor(color);
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        mTxtTitle.setText(title);
    }

    /**
     * 设置标题粗体
     */
    public void setBoldTitle(String title) {
        mTxtTitle.setText(title);
        RichTextView.setTextBold(mTxtTitle);
    }

    /**
     * 设置标题颜色
     */
    public void setTitleColor(int resId) {
        mTxtTitle.setTextColor(resId);
    }

    /**
     * 设置标题大小
     */
    public void setTitleSize(float size) {
        mTxtTitle.setTextSize(size);
    }

    /**
     * 设置取消文字
     */
    public void setCancelText(String text) {
        mBtnCancel.setText(text);
    }

    /**
     * 设置取消文字颜色
     */
    public void setCancelTextColor(int resId) {
        mBtnCancel.setTextColor(resId);
    }

    /**
     * 设置取消文字大小
     */
    public void setCancelTextSize(float size) {
        mBtnCancel.setTextSize(size);
    }

    /**
     * 设置确认文字
     */
    public void setSubmitText(String text) {
        mBtnSubmit.setText(text);
    }

    /**
     * 设置确认文字颜色
     */
    public void setSubmitTextColor(int resId) {
        mBtnSubmit.setTextColor(resId);
    }

    /**
     * 设置确认文字大小
     */
    public void setSubmitTextSize(float size) {
        mBtnSubmit.setTextSize(size);
    }

    /**
     * 设置滚动文字大小
     */
    public void setTextSize(float size) {
        mWheelOptions.setTextSize(size);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSubmit) {
            if (mOptionsSelectListener != null) {
                int[] optionsCurrentItems = mWheelOptions.getCurrentItems();
                mOptionsSelectListener.onOptionsSelect(optionsCurrentItems[0], optionsCurrentItems[1], optionsCurrentItems[2]);
            }
            dismiss();
        } else if (id == R.id.btnCancel) {
            dismiss();
        }
    }

    public interface OnOptionsSelectListener {
        void onOptionsSelect(int option1, int option2, int option3);
    }

    public void setOnOptionsSelectListener(
            OnOptionsSelectListener optionsSelectListener) {
        this.mOptionsSelectListener = optionsSelectListener;
    }
}
