package com.example.vincenzo.myfirstofficialeopencvtest.ui;

import android.util.Log;

import com.example.vincenzo.myfirstofficialeopencvtest.R;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.BlackDama;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.BlackPawn;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.EmptyTile;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.WhiteDama;
import com.example.vincenzo.myfirstofficialeopencvtest.core.game_objects.WhitePawn;

import java.util.HashMap;

/**
 * Created by vincenzo on 07/11/2015.
 */
public class FlyWeightFactory {

    private static FlyWeightFactory instance = null;
    private HashMap<Class, Integer> flyWeightsPool = new HashMap<>();
    private HashMap<Integer, Class> keyRepository = new HashMap<>();

    private FlyWeightFactory(){
        fillPool();
    }

    public static FlyWeightFactory getInstance(){
        if(instance == null)
            instance = new FlyWeightFactory();
        return instance;
    }


    private void fillPool() {
        registerFlyWeight(EmptyTile.class, android.R.color.transparent);
        registerFlyWeight(WhitePawn.class, R.drawable.scaled_white_pawn);
        registerFlyWeight(BlackPawn.class, R.drawable.scaled_black_pawn);
        registerFlyWeight(WhiteDama.class, R.drawable.white_dama);
        registerFlyWeight(BlackDama.class, R.drawable.black_dama);
    }

    private void registerFlyWeight(Class item, Integer id){
        this.flyWeightsPool.put(item, id);
        this.keyRepository.put(this.keyRepository.size(), item);
    }


    public Class getNextClazz (Class inputStatus){

        Integer currentID = getCurrentStatus(inputStatus);
        Integer nextID = (currentID + 1)%flyWeightsPool.size();
        Class c = keyRepository.get(nextID);
        return c;
    }


    public Integer getCurrentResource (Class clazz){
        return flyWeightsPool.get(clazz);
    }


    private Integer getCurrentStatus (Class clazz){
        for(Integer i : keyRepository.keySet())
            if(keyRepository.get(i) == clazz)
                return i;

        throw new RuntimeException("Clazz not found");
    }

}
