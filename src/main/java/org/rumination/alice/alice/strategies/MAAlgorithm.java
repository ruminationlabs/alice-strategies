/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rumination.alice.alice.strategies;

import org.rumination.alice.algo.core.ScripAlgorithm;
import org.rumination.alice.data.core.BroadcastItem;
import org.rumination.alice.data.core.DataModel;
import org.rumination.alice.data.core.StockData;
import org.rumination.alice.managers.DBManager;
import org.rumination.alice.quant.functions.Technicals;
import org.rumination.alice.strategy.ScripStrategyParameter;
import org.rumination.alice.strategy.Signal;
import org.rumination.alice.strategy.SignalTrend;
import org.rumination.alice.strategy.Strategy;
import org.rumination.alice.trading.OrderType;
import java.util.HashMap;



/**
 *
 * @author Janus
 */
public class MAAlgorithm extends ScripAlgorithm
{


    @Override
    public void stop() {
        
    }

    @Override
    public String getUniqueIdentifier() {
       return "MovingAverage";
    }

    @Override
    public HashMap<String, Class> getStrategyMappings() {
       return new HashMap<>();
    }

    @Override
    public HashMap<String, Class> getScripParameterMappings() {
         HashMap<String, Class> map = new HashMap<>();
        map.put("periods",Integer.class);
        map.put("displacement",Integer.class);
        return map;
    }

    @Override
    public HashMap<String, Object> getDefaultMappingValues() {
     HashMap<String, Object> map = new HashMap<>();
        map.put("periods",16);
        map.put("displacement",5);
        return map;
    }

    @Override
    public void checkSignal(DBManager db, StockData data, Strategy strategy, ScripStrategyParameter param, Signal signal) throws Exception {
            DataModel mod = data.getLast();
            double[] ma = Technicals.MovingAverage(0, (int)param.getValue("periods"), data.getCloseSeries());
            int i = ma.length-(int)param.getValue("displacement")-1;
            boolean buy =mod.getClose()>ma[i] && (signal.getSignalTrend()!=SignalTrend.LONG);
            boolean shortSell=mod.getClose()<ma[i] && (signal.getSignalTrend()!=SignalTrend.SHORT);
            SignalTrend newSignal = null;
            if(buy) newSignal = SignalTrend.LONG;
            if(shortSell) newSignal = SignalTrend.SHORT;
             validateSignal(db, signal,newSignal, mod.getClose(), mod.getDate(),OrderType.LIMIT);
    }

    @Override
    public void checkSignal(DBManager db, Strategy strategy, ScripStrategyParameter param, BroadcastItem broadcast, Signal signal) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
