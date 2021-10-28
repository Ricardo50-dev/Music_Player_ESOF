package com.example.music_player;

import org.junit.Test;

import java.sql.SQLOutput;
import java.lang.Object;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals("2:00", PlayerActivity.createTime(120000));
        assertEquals("1:30", PlayerActivity.createTime(90000));
        assertEquals("1:45", PlayerActivity.createTime(105000));
        assertEquals("0:45", PlayerActivity.createTime(45000));
        assertEquals("1:00", PlayerActivity.createTime(155000));
    }
}