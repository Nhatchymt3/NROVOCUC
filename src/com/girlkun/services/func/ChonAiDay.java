/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.services.func;

import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.server.Client;
import com.girlkun.services.ChatGlobalService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ChonAiDay  implements Runnable{
    public int goldNormar;
    public int goldVip;
    public long lastTimeEnd;
    public List<Player> PlayersNormar = new ArrayList<>();
    public List<Player> PlayersVIP = new ArrayList<>();
    private static ChonAiDay instance;
    
    public static ChonAiDay gI() {
        if (instance == null) {
            instance = new ChonAiDay();
        }
        return instance;
    }
    
    public void addPlayerVIP(Player pl){
        if(!PlayersVIP.equals(pl)){
            PlayersVIP.add(pl);
        }
    }
    
    public void addPlayerNormar(Player pl){
        if(!PlayersNormar.equals(pl)){
            PlayersNormar.add(pl);
        }
    }
    
    public void removePlayerVIP(Player pl){
        if(PlayersVIP.equals(pl)){
            PlayersVIP.remove(pl);
        }
    }
    
    public void removePlayerNormar(Player pl){
        if(PlayersNormar.equals(pl)){
            PlayersNormar.remove(pl);
        }
    }
    
    @Override
    public void run() {

    }
}
