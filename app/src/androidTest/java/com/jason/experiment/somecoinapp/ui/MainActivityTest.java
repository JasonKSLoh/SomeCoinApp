package com.jason.experiment.somecoinapp.ui;

import android.support.design.widget.BottomNavigationView;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.DrawerMatchers;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;

import com.jason.experiment.somecoinapp.R;
import com.jason.experiment.somecoinapp.model.ticker.TickerDatum;
import com.jason.experiment.somecoinapp.model.ticker.TickerQuote;
import com.jason.experiment.somecoinapp.ui.dash.FollowedFragment;
import com.jason.experiment.somecoinapp.ui.dash.FollowedViewModel;
import com.jason.experiment.somecoinapp.ui.listing.ListingFragment;
import com.jason.experiment.somecoinapp.ui.listing.ListingViewModel;
import com.jason.experiment.somecoinapp.util.ReflectionUtils;
import com.jason.experiment.somecoinapp.util.SharedPrefsUtils;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * MainActivityTest
 * Created by jason.
 */
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    MainActivity mainActivity;
    UiDevice     uiDevice;

    @Before
    public void setup() {
        mainActivity = activityRule.getActivity();
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.waitForIdle();
    }

    @Test
    public void baseTest() {
        try {
            BottomNavigationView bottomNavigationView = mainActivity.findViewById(R.id.bottom_nav_main);
            ViewPager            pager                = mainActivity.findViewById(R.id.viewpager_main);
            assertNotNull(bottomNavigationView);
            assertNotNull(pager);
            assertEquals(3, pager.getAdapter().getCount());
            assertEquals(3, bottomNavigationView.getMenu().size());

//
//            UiObject uiObject = uiDevice.findObject(new UiSelector().className(ViewPager.class));
//            uiObject.swipeRight(1);
        } catch ( NullPointerException e) {
            fail();
        }
    }

    @Test
    public void pageSwitchingTest() {
        try {
            ViewInteraction interaction = onView(ViewMatchers.withId(R.id.viewpager_main));
            onView(ViewMatchers.withId(R.id.rv_listing))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()));
            interaction.perform(ViewActions.swipeLeft());

            uiDevice.waitForIdle();
            onView(ViewMatchers.withId(R.id.rv_followed))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()));

            interaction.perform(ViewActions.swipeLeft());
            uiDevice.waitForIdle();
            onView(ViewMatchers.withId(R.id.rv_search_main))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()));


            interaction.perform(ViewActions.swipeRight());
            uiDevice.waitForIdle();
            onView(ViewMatchers.withId(R.id.rv_followed))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()));

            interaction.perform(ViewActions.swipeRight());
            uiDevice.waitForIdle();
            onView(ViewMatchers.withId(R.id.rv_listing))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()));
        } catch (NullPointerException e) {
            fail();
        }
    }

    @Test
    public void listingFragmentTest() {
        try {
            RecyclerView rvListing = mainActivity.findViewById(R.id.rv_listing);
            assertNotNull(rvListing);
            ViewInteraction interaction = onView(ViewMatchers.withId(R.id.rv_listing));

            Thread.sleep(5000);
            assertEquals(100, rvListing.getAdapter().getItemCount());

            interaction.perform(RecyclerViewActions.actionOnItemAtPosition(50, ViewActions.click()));
            uiDevice.waitForIdle();

            List<Fragment>   fragmentList     = (List<Fragment>) ReflectionUtils.getPrivateFieldValue("fragmentList", mainActivity.viewPagerMain.getAdapter(), GenericPagerAdapter.class);
            ListingFragment  listingFragment  = (ListingFragment) fragmentList.get(0);
            ListingViewModel listingViewModel = (ListingViewModel) ReflectionUtils.getPrivateFieldValue("listingViewModel", listingFragment, ListingFragment.class);
            TickerDatum      datum            = listingViewModel.getSelectedCoin().getValue();

            String      currency = datum.getQuotes().keySet().toArray()[0].toString();
            TickerQuote quote    = datum.getQuotes().get(currency);

            boolean isFollowed = SharedPrefsUtils.isCoinSaved(mainActivity, datum.getId());
            String  expectedFollowedBtnText;
            String  converseFollowBtnText;
            if (isFollowed) {
                expectedFollowedBtnText = mainActivity.getString(R.string.unfollow);
                converseFollowBtnText = mainActivity.getString(R.string.follow);
            } else {
                expectedFollowedBtnText = mainActivity.getString(R.string.follow);
                converseFollowBtnText = mainActivity.getString(R.string.unfollow);
            }


            onView(ViewMatchers.withId(R.id.tv_detail_name))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(
                            ViewMatchers.withText(datum.getName())));

            onView(ViewMatchers.withId(R.id.tv_detail_price_currency))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(
                            ViewMatchers.withText(currency)));

            onView(ViewMatchers.withId(R.id.tv_detail_symbol))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(
                            ViewMatchers.withText(datum.getSymbol())));

            onView(ViewMatchers.withId(R.id.btn_detail_follow))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(
                            ViewMatchers.withText(expectedFollowedBtnText)))
                    .perform(ViewActions.click())
                    .check(ViewAssertions.matches(
                            ViewMatchers.withText(converseFollowBtnText)))
                    .perform(ViewActions.click())
                    .check(ViewAssertions.matches(
                            ViewMatchers.withText(expectedFollowedBtnText)));


        } catch (Exception e) {
            Log.d("+_+", e.getMessage(), e);
            fail();
        }
    }

    @Test
    public void followedFragmentTest() {
        try {
            ViewInteraction topLevelInteraction = onView(ViewMatchers.withId(R.id.viewpager_main));
            onView(ViewMatchers.withId(R.id.rv_listing))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()));
            topLevelInteraction.perform(ViewActions.swipeLeft());

            uiDevice.waitForIdle();
            Thread.sleep(1000);

            RecyclerView rvFollowed = mainActivity.findViewById(R.id.rv_followed);
            assertNotNull(rvFollowed);
            ViewInteraction interaction = onView(ViewMatchers.withId(R.id.rv_followed));

            List<Fragment>    fragmentList     = (List<Fragment>) ReflectionUtils.getPrivateFieldValue("fragmentList", mainActivity.viewPagerMain.getAdapter(), GenericPagerAdapter.class);
            FollowedFragment  followedFragment = (FollowedFragment) fragmentList.get(1);
            FollowedViewModel followedViewModel = (FollowedViewModel) ReflectionUtils.getPrivateFieldValue("followedViewModel", followedFragment, FollowedFragment.class);

            List<TickerDatum> followedData = followedViewModel.getFollowedCoinData().getValue();


            ArrayList<Integer> coinIds = SharedPrefsUtils.getSavedCoins(mainActivity);
            assertEquals(coinIds.size(), rvFollowed.getAdapter().getItemCount());

            for(TickerDatum datum: followedData){
                onView(Matchers.allOf(ViewMatchers.withId(R.id.tv_item_following_symbol), ViewMatchers.withText(datum.getSymbol())))
                        .check(ViewAssertions.matches(
                                ViewMatchers.isDisplayed()));
            }

            if(coinIds.size() <= 0){
                return;
            }
            interaction.perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
            uiDevice.waitForIdle();

           TickerDatum       datum            = followedViewModel.getSelectedCoin().getValue();

            String      currency = datum.getQuotes().keySet().toArray()[0].toString();
            TickerQuote quote    = datum.getQuotes().get(currency);

            boolean isFollowed = SharedPrefsUtils.isCoinSaved(mainActivity, datum.getId());
            String  expectedFollowedBtnText;
            String  converseFollowBtnText;
            if (isFollowed) {
                expectedFollowedBtnText = mainActivity.getString(R.string.unfollow);
                converseFollowBtnText = mainActivity.getString(R.string.follow);
            } else {
                expectedFollowedBtnText = mainActivity.getString(R.string.follow);
                converseFollowBtnText = mainActivity.getString(R.string.unfollow);
            }


            onView(ViewMatchers.withId(R.id.tv_detail_name))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(
                            ViewMatchers.withText(datum.getName())));

            onView(ViewMatchers.withId(R.id.tv_detail_price_currency))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(
                            ViewMatchers.withText(currency)));

            onView(ViewMatchers.withId(R.id.tv_detail_symbol))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(
                            ViewMatchers.withText(datum.getSymbol())));

            onView(ViewMatchers.withId(R.id.btn_detail_follow))
                    .check(ViewAssertions.matches(
                            ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(
                            ViewMatchers.withText(expectedFollowedBtnText)))
                    .perform(ViewActions.click())
                    .check(ViewAssertions.matches(
                            ViewMatchers.withText(converseFollowBtnText)))
                    .perform(ViewActions.click())
                    .check(ViewAssertions.matches(
                            ViewMatchers.withText(expectedFollowedBtnText)));


        } catch (Exception e) {
            Log.d("+_+", e.getMessage(), e);
            fail();
        }
    }

    @Test
    public void navDrawerTest(){
        onView(ViewMatchers.withId(R.id.drawer_layout))
                .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());

        uiDevice.waitForIdle();

        onView(ViewMatchers.withId(R.id.tv_faq))
                .perform(ViewActions.click());

        uiDevice.waitForIdle();

        String ok = mainActivity.getString(R.string.ok);
        onView(ViewMatchers.withText("FAQ")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withText(ok)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        uiDevice.pressBack();
        uiDevice.waitForIdle();

        onView(ViewMatchers.withId(R.id.tv_logs))
                .perform(ViewActions.click());

        uiDevice.waitForIdle();
        String dismiss = mainActivity.getString(R.string.dismiss);
        String clear = mainActivity.getString(R.string.clear_logs);
        String noLogsMessage = mainActivity.getString(R.string.no_logs_message);

        onView(ViewMatchers.withText(dismiss))
                .check(ViewAssertions.matches(
                        ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withText(clear))
                .check(ViewAssertions.matches(
                        ViewMatchers.isDisplayed()))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.tv_log_data))
                .check(ViewAssertions.matches(
                        ViewMatchers.withText(noLogsMessage)));


        onView(ViewMatchers.withId(R.id.drawer_layout))
                .check(ViewAssertions.matches(DrawerMatchers.isOpen(Gravity.LEFT)))
                .perform(DrawerActions.close());
    }

}