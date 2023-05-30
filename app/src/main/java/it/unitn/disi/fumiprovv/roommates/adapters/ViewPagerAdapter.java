package it.unitn.disi.fumiprovv.roommates.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import it.unitn.disi.fumiprovv.roommates.fragments.SpeseSituazione;
import it.unitn.disi.fumiprovv.roommates.fragments.SpeseComuniFragment;
import it.unitn.disi.fumiprovv.roommates.fragments.SpeseStorico;

public class ViewPagerAdapter
        extends FragmentPagerAdapter {

    public ViewPagerAdapter(
            @NonNull FragmentManager fm)
    {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;
        if (position == 0)
            fragment = new SpeseSituazione();
        else if (position == 1)
            fragment = new SpeseComuniFragment();
        else if (position == 2)
            fragment = new SpeseStorico();

        return fragment;
    }

    @Override
    public int getCount()
    {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        String title = null;
        if (position == 0)
            title = "Situazione";
        else if (position == 1)
            title = "Spese comuni";
        else if (position == 2)
            title = "Storico";
        return title;
    }
}
