package com.jason.experiment.somecoinapp.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.jason.experiment.somecoinapp.R;
import com.jason.experiment.somecoinapp.logging.Logg;
import com.jason.experiment.somecoinapp.ui.dash.FollowedFragment;
import com.jason.experiment.somecoinapp.ui.listing.ListingFragment;
import com.jason.experiment.somecoinapp.ui.search.SearchFragment;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    private static final int FRAG_ID_LISTING  = 0;
    private static final int FRAG_ID_FOLLOWED = 1;
    private static final int FRAG_ID_SEARCH   = 2;

    MainViewModel        mainViewModel;
    DrawerLayout         drawerLayout;
    BottomNavigationView bottomNavigationView;
    ViewPager            viewPagerMain;
    View                 vNavDrawerBlankSpace;
    TextView             tvFaq;
    TextView             tvLogs;
    TextView             tvExitApp;
    GenericPagerAdapter  pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        Logg.addLogTracker(mainViewModel);
        bindViews();
        bindObservers();
        drawerLayout.closeDrawer(Gravity.START, false);
    }

    private void bindViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottom_nav_main);
        viewPagerMain = findViewById(R.id.viewpager_main);
        vNavDrawerBlankSpace = findViewById(R.id.nav_drawer_blankspace);
        tvFaq = findViewById(R.id.tv_faq);
        tvLogs = findViewById(R.id.tv_logs);
        tvExitApp = findViewById(R.id.tv_exit_app);

        bindNavItemListeners();
        disableShiftMode(bottomNavigationView);
        setupViewPager();
    }

    private void bindNavItemListeners() {
        tvFaq.setOnClickListener(v -> {
            String FAQ_MESSAGE = "Top Coins:\nSee the top 100 coins\n\n" +
                    "Followed:\nView your followed coins\n\n" +
                    "Search:\nSearch for coins based on symbol, name or id\n\n" +
                    "Select a listed coin to view it's details";
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("FAQ")
                    .setMessage(FAQ_MESSAGE)
                    .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                    })
                    .create();
            alertDialog.show();
        });

        tvLogs.setOnClickListener(v -> {
            showLogFragment();
        });

        tvExitApp.setOnClickListener(v -> {
            finish();
        });

        vNavDrawerBlankSpace.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Integer navItem = translateItemId(item.getItemId());
            mainViewModel.setCurrentNavItem(navItem);
            return true;
        });
    }

    private void bindObservers() {
        Observer<Integer> navItemObserver = integer -> {
            if (integer != null) {
                viewPagerMain.setCurrentItem(integer);
                bottomNavigationView.getMenu().getItem(integer).setChecked(true);
            }
        };
        mainViewModel.getCurrentNavItem().observe(this, navItemObserver);
    }

    private void setupViewPager() {
        pagerAdapter = new GenericPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new ListingFragment(), "Home");
        pagerAdapter.addFragment(new FollowedFragment(), "Dashboard");
        pagerAdapter.addFragment(new SearchFragment(), "Notifications");
        viewPagerMain.setAdapter(pagerAdapter);
        viewPagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { /* Irrelevant */ }

            @Override
            public void onPageSelected(int position) {
                mainViewModel.setCurrentNavItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) { /* Irrelevant */ }
        });
    }

    private Integer translateItemId(int itemId) {
        switch (itemId) {
            case R.id.navigation_listing:
                return FRAG_ID_LISTING;
            case R.id.navigation_dashboard:
                return FRAG_ID_FOLLOWED;
            case R.id.navigation_search:
                return FRAG_ID_SEARCH;
            default:
                return null;
        }
    }

    @Override
    public void onBackPressed() {
        mainViewModel.backPressed();
    }

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("+_","Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("+_", "Unable to change value of shift mode", e);
        }
    }

    private void showLogFragment(){
        DialogFragment logFragment = new LogDisplayFragment();
        FragmentManager supportFM = getSupportFragmentManager();
        logFragment.show(supportFM, "log");
        supportFM.executePendingTransactions();

    }


}
