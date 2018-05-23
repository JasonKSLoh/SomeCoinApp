package com.jason.experiment.somecoinapp.util;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;

/**
 * ReflectionUtilsTest
 * Created by jason.
 */
public class ReflectionUtilsTest {

    private static final String testString = "SSSSS";

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getPrivateFieldValue() {
        try {
            SomeClass someClass = new SomeClass();
            String s = (String)ReflectionUtils.getPrivateFieldValue("s", someClass, someClass.getClass());
            Assert.assertEquals(s, testString);
        } catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void getMethodResultTest(){
        try {
            SomeClass someClass = new SomeClass();
            String s = (String)ReflectionUtils.getPrivateMethodResult("paramsToString", someClass, someClass.getClass(),null, null);
            Assert.assertEquals(s, testString + 1);

            Class<?>[] paramClasses = {String.class, int.class};
            String inputString = "ABCDE";
            int inputMultiplier = 2;
            Object[] paramValues = {inputString, inputMultiplier};
            int i = (int)ReflectionUtils.getPrivateMethodResult("getNumChars", someClass, someClass.getClass(), paramClasses, paramValues);
            Assert.assertEquals(i, inputMultiplier * inputString.length());
        } catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void getPrivateClassTest(){
        try{
            String fullClassName = SomeClass.class.getName();
            System.out.println("Full Class Name: " + fullClassName);
            SomeClass someClass = (SomeClass)ReflectionUtils.getPrivateObject(fullClassName);

            Object o = ReflectionUtils.getPrivateObject(fullClassName);
            Assert.assertTrue(o instanceof SomeClass);

            Class<?>[] paramTypes = {String.class, int.class};
            String sVal = "wololo";
            int iVal = 5;
            Object[] paramVals = {sVal, iVal};
            Object o2 = ReflectionUtils.getPrivateObject(fullClassName, paramTypes, paramVals);
            Assert.assertTrue(o2 instanceof SomeClass);
            SomeClass someClass1 = (SomeClass)o2;

            Assert.assertEquals(someClass1.getI(), iVal);
            Assert.assertEquals(someClass1.getS(), sVal);
        } catch (Exception e){
            System.out.println(e);
            Assert.fail();
        }
    }

    private static class SomeClass {
        private String s = testString;
        private int i = 1;

        private SomeClass(){
        }

        private SomeClass(String s, int i){
            this.s = s;
            this.i = i;
        }

        private String paramsToString(){
            return testString + i;
        }

        private int getNumChars(String s, int multiplier){
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < multiplier; i++){
                sb.append(s);
            }
            String finalString = sb.toString();
            return finalString.length();
        }

        public String getS(){
            return s;
        }

        public int getI(){
            return i;
        }
    }
}