/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.girlkun.nhatchymte;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.server.Client;


/**
 *
 * @author nhatchymte An Trom
 */
public class NhatManager {
    private static NhatManager instance = null;
    
    private NhatManager() {
        compositeDisposable = new CompositeDisposable();
    }

    // Static method
    // Static method to create instance of Singleton class
    public static synchronized NhatManager getInstance() {
        if (instance == null) {
            instance = new NhatManager();
        }
        return instance;
    }
    
    private CompositeDisposable compositeDisposable;
    
    public void autoSave() {
        System.out.println("[AutoSaveManager] start autosave");
        Disposable subscribe = Observable.interval(60, 90, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribe(i -> {
                    this.handleAutoSave();
                },  throwable -> {
              System.out.println("[AutoSaveManager] start autosave error: " + throwable.getLocalizedMessage());
        });
        compositeDisposable.add(subscribe);               
    }
    
    public void handleAutoSave() {
        Client.gI().getPlayers().forEach(player -> {
            PlayerDAO.updatePlayer(player);
        });
    }
    
    private void dispose() {
        compositeDisposable.dispose();
        compositeDisposable = null;
    }
}
