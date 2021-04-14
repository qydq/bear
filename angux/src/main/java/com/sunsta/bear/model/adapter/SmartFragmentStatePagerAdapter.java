package com.sunsta.bear.model.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/*
Extension of FragmentStatePagerAdapter which intelligently caches
all active fragments and manages the fragment lifecycles.
Usage involves extending from AbstractFragmentStatePagerAdapter as you would any other PagerAdapter.
参考 ：//https://www.jianshu.com/p/59ea57417a6c 防止getContext为空， 而不引起内存泄露的影响
*/
public class SmartFragmentStatePagerAdapter extends AbstractFragmentStatePagerAdapter {

    public SmartFragmentStatePagerAdapter(@NonNull FragmentActivity fmActivity) {
        super(fmActivity.getSupportFragmentManager());
    }

    public SmartFragmentStatePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public SmartFragmentStatePagerAdapter(@NonNull FragmentManager fm, int behaviorResumeOnlyCurrentFragment) {
        super(fm, behaviorResumeOnlyCurrentFragment);
    }

    public SmartFragmentStatePagerAdapter(@NonNull FragmentActivity fmActivity, List<Fragment> fragments) {
        super(fmActivity.getSupportFragmentManager(), fragments, new ArrayList<>(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public SmartFragmentStatePagerAdapter(@NonNull FragmentActivity fmActivity, List<Fragment> fragments, List<String> titles) {
        super(fmActivity.getSupportFragmentManager(), fragments, titles, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public SmartFragmentStatePagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm, fragments, titles, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public SmartFragmentStatePagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments, int behaviorResumeOnlyCurrentFragment) {
        super(fm, fragments, new String[]{}, behaviorResumeOnlyCurrentFragment);
    }

    public SmartFragmentStatePagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments, String[] titles, int behaviorResumeOnlyCurrentFragment) {
        super(fm, fragments, titles, behaviorResumeOnlyCurrentFragment);
    }

    public SmartFragmentStatePagerAdapter(@NonNull FragmentActivity fmActivity, List<Fragment> fragments, String[] titles) {
        super(fmActivity.getSupportFragmentManager(), fragments, titles, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public SmartFragmentStatePagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
        super(fm, fragments, new String[]{}, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public SmartFragmentStatePagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments, String[] titles) {
        super(fm, fragments, titles, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }
}