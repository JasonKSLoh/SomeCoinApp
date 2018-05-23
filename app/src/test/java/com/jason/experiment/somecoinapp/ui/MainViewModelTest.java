package com.jason.experiment.somecoinapp.ui;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * MainViewModelTest
 * Created by jason.
 */
public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MainViewModel mainViewModel;

    @Before
    public void setUp(){
        mainViewModel = new MainViewModel();
    }

    @Test
    public void backPressedTest() {
        mainViewModel.setBackPressed(false);
        Assert.assertNotNull(mainViewModel.getBackPressed().getValue());
        Assert.assertFalse(mainViewModel.getBackPressed().getValue());

        mainViewModel.setBackPressed(true);
        Assert.assertNotNull(mainViewModel.getBackPressed().getValue());
        Assert.assertTrue(mainViewModel.getBackPressed().getValue());

        mainViewModel.setBackPressed(false);
        mainViewModel.backPressed();
        Assert.assertNotNull(mainViewModel.getBackPressed().getValue());
        Assert.assertTrue(mainViewModel.getBackPressed().getValue());
    }


    @Test
    public void logDataTest() {
        int priority = 1;
        String tag = "some_tag";
        String data = "This is some data";
        Throwable throwable = null;
        mainViewModel.trackLog(priority, tag, data, throwable);

        String stackTraceString = "";
        if(throwable != null){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            stackTraceString = sw.toString();
        }
        String expectedValue = priority + ':' + tag + ':' +
                data + '\n' +
                stackTraceString + "\n\n";
        Assert.assertEquals(expectedValue, mainViewModel.getLogData().getValue());

        int priority2 = 2;
        String tag2 = "other_tag";
        String data2 = "Other Data";
        Throwable throwable2 = new Throwable();

        String stackTraceString2 = "";
        if(throwable2 != null){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable2.printStackTrace(pw);
            stackTraceString2 = sw.toString();
        }
        String expectedValue2 = priority2 + ':' + tag2 + ':' +
                data2 + '\n' +
                stackTraceString2 + "\n\n";

        String finalExpectedValue = expectedValue2 + expectedValue;

        mainViewModel.trackLog(priority2, tag2, data2, throwable2);
        Assert.assertEquals(finalExpectedValue, mainViewModel.getLogData().getValue());

        mainViewModel.clearLogData();
        Assert.assertEquals("", mainViewModel.getLogData().getValue());
    }


    @Test
    public void currentNavItemTest() {
        try {
            mainViewModel.setCurrentNavItem(MainViewModel.NAV_ITEM_FOLLOWED);
            Assert.assertEquals((int)mainViewModel.getCurrentNavItem().getValue(), MainViewModel.NAV_ITEM_FOLLOWED);

            mainViewModel.setCurrentNavItem(MainViewModel.NAV_ITEM_LISTING);
            Assert.assertEquals((int)mainViewModel.getCurrentNavItem().getValue(), MainViewModel.NAV_ITEM_LISTING);
        } catch (NullPointerException ignored){
            Assert.fail();
        }
    }

    @Test
    public void backPressedObserverTest(){
        try {
            Observer<Boolean> backPressedObserver = (Observer<Boolean>)Mockito.mock(Observer.class);
            mainViewModel.getBackPressed().observeForever(backPressedObserver);



            Mockito.verify(backPressedObserver, Mockito.times(0)).onChanged(Boolean.TRUE);
            mainViewModel.backPressed();

            Mockito.verify(backPressedObserver, Mockito.times(1)).onChanged(Boolean.TRUE);
            mainViewModel.backPressed();

            Mockito.verify(backPressedObserver, Mockito.times(2)).onChanged(Boolean.TRUE);
        } catch (Exception e){
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }

    @Test
    public void navObserverTest(){
        try{
            Observer<Integer> observer = (Observer<Integer>)Mockito.mock(Observer.class);
            mainViewModel.getCurrentNavItem().observeForever(observer);


            Mockito.verify(observer, Mockito.times(1)).onChanged(0);
            Mockito.verify(observer, Mockito.times(0)).onChanged(1);

            mainViewModel.setCurrentNavItem(1);
            Mockito.verify(observer, Mockito.times(1)).onChanged(1);

            mainViewModel.setCurrentNavItem(2);
            Mockito.verify(observer, Mockito.times(1)).onChanged(2);
        } catch (Exception e){
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }

    @Test
    public void logObserverTest(){
        try{
            Observer<String> observer = (Observer<String>)Mockito.mock(Observer.class);
            mainViewModel.getLogData().observeForever(observer);


            Mockito.verify(observer, Mockito.times(1)).onChanged("");
            Mockito.verify(observer, Mockito.times(0)).onChanged("Something");

            int priority = 1;
            String tag = "some_tag";
            String data = "This is some data";
            Throwable throwable = null;
            String stackTraceString = "";
            if(throwable != null){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                stackTraceString = sw.toString();
            }
            String expectedValue = priority + ':' + tag + ':' +
                    data + '\n' +
                    stackTraceString + "\n\n";
            mainViewModel.trackLog(priority,tag, data, throwable);
            Mockito.verify(observer, Mockito.times(1)).onChanged(expectedValue);
        } catch (Exception e){
            System.out.println(e.getMessage());
            Assert.fail();
        }
    }



}