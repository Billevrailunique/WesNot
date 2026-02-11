package test;

import util.*;

public class TimelineTest{

    public static void main(String[] args) {

        Music mu = new Music();
        Runnable r = () -> mu.play("Music");
        Task t = new Task(0,r);
        Timeline.add(t);
        for (int i = 0 ; i < Integer.MAX_VALUE ; i++){
            for (int j = 0 ; j < 20 ; j++){

            }
        }

    }


}