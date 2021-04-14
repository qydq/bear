package com.sunsta.bear.model.adapter;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sunsta.bear.faster.DataService;

import java.util.ArrayList;
import java.util.List;

/*
   Extension of FragmentStatePagerAdapter which intelligently caches
   all active fragments and manages the fragment lifecycles.
   Usage involves extending from AbstractFragmentStatePagerAdapter as you would any other PagerAdapter.
   参考 ：//https://www.jianshu.com/p/59ea57417a6c   防止getContext为空， 而不引起内存泄露的影响
*/
public abstract class AbstractFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    // Sparse array to keep track of registered fragments in memory
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    public AbstractFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public AbstractFragmentStatePagerAdapter(FragmentManager fm, int behaviorResumeOnlyCurrentFragment) {
        super(fm, behaviorResumeOnlyCurrentFragment);
    }

    public AbstractFragmentStatePagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles, int behaviorResumeOnlyCurrentFragment) {
        super(fm, behaviorResumeOnlyCurrentFragment);
        this.fragments = fragments;
        this.titles = titles;
    }

    public AbstractFragmentStatePagerAdapter(FragmentManager fm, List<Fragment> fragments, String[] titles, int behaviorResumeOnlyCurrentFragment) {
        super(fm, behaviorResumeOnlyCurrentFragment);
        this.fragments = fragments;
        this.titles = DataService.getInstance().transferArrayToList(titles);
    }

    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (registeredFragments != null && registeredFragments.size() > 0) {
            registeredFragments.remove(position);
        }
        if (fragments != null && fragments.size() > 0) {
            fragments.remove(position);
        }
        if (titles != null && titles.size() > 0) {
            titles.remove(position);
        }
        super.destroyItem(container, position, object);
    }

    // Returns the fragment for the position (if instantiated)
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}