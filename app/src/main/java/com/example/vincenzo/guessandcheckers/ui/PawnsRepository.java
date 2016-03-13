package com.example.vincenzo.guessandcheckers.ui;

import com.example.vincenzo.guessandcheckers.R;
import com.example.vincenzo.guessandcheckers.core.game_objects.BlackDama;
import com.example.vincenzo.guessandcheckers.core.game_objects.BlackPawn;
import com.example.vincenzo.guessandcheckers.core.game_objects.EmptyTile;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhiteDama;
import com.example.vincenzo.guessandcheckers.core.game_objects.WhitePawn;

import java.util.HashMap;

/**
 * Created by vincenzo on 07/11/2015.
 */
public class PawnsRepository {

    private static PawnsRepository instance = null;
    private HashMap<Class, Integer> resourcesPool = new HashMap<>();
    private HashMap<Integer, Class> classesPool = new HashMap<>();

    private PawnsRepository(){
        fillPool();
    }

    public static PawnsRepository getInstance(){
        if(instance == null)
            instance = new PawnsRepository();
        return instance;
    }


    private void fillPool() {
        addResource(EmptyTile.class, android.R.color.transparent);
        addResource(WhitePawn.class, R.drawable.scaled_white_pawn);
        addResource(BlackPawn.class, R.drawable.scaled_black_pawn);
        addResource(WhiteDama.class, R.drawable.white_dama);
        addResource(BlackDama.class, R.drawable.black_dama);
    }

    private void addResource(Class item, Integer id){
        this.resourcesPool.put(item, id);
        this.classesPool.put(this.classesPool.size(), item);
    }


    public Class getNextClazz (Class inputStatus){

        Integer currentID = getCurrentStatus(inputStatus);
        Integer nextID = (currentID + 1)% resourcesPool.size();
        Class c = classesPool.get(nextID);
        return c;
    }


    public Integer getCurrentResource (Class clazz){
        return resourcesPool.get(clazz);
    }


    private Integer getCurrentStatus (Class clazz){
        for(Integer i : classesPool.keySet())
            if(classesPool.get(i) == clazz)
                return i;

        throw new RuntimeException("Clazz not found");
    }

}
