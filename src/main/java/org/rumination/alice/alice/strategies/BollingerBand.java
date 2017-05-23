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
 * 
 */

public class BollingerBand extends ScripAlgorithm{



    

    @Override
    public HashMap<String, Class> getScripParameterMappings() {
        HashMap<String,Class> map = new HashMap<>();
        map.put("pricefield", String.class);
        map.put("periods", Integer.class);
        map.put("matype", String.class);
        map.put("stdev", Integer.class);
        return map;
    }

    @Override
    public void stop() {
        
    }

    @Override
    public String getUniqueIdentifier() {
        return "BollingerBands";
    }

    @Override
    public HashMap<String, Class> getStrategyMappings() {
        return new HashMap<>();
    }

    @Override
    public HashMap<String, Object> getDefaultMappingValues() {
     HashMap<String,Object> map = new HashMap<>();
        map.put("pricefield", "C");
        map.put("periods", 20);
        map.put("matype", "S");
        map.put("stdev", 1);
        return map;
    }

    @Override
    public void checkSignal(DBManager db, StockData data, Strategy strategy, ScripStrategyParameter param, Signal signal) throws Exception {
            String priceField = param.getValue("pricefield").toString();
            int periods = (int)param.getValue("periods");
            String maType = param.getValue("matype").toString();
            int stdev = (int)param.getValue("stdev");
//            System.out.println("Data size:"+data.size());
//            org.jboss.logging.Logger.getLogger(BollingerBand.class).info("MAType: "+maType+"\t maperiods "+periods);
            double[] series;
            
            switch (priceField) {
                case "H":
                    series = data.getHighSeries();
                    break;
                case "L":
                    series = data.getLowSeries();
                    break;
                case "O":
                    series = data.getOpenSeries();
                    break;
                default:
                    series = data.getCloseSeries();
            }
            double movAvg[];
            
            
            switch (maType) {
                case "E":
                    movAvg =  Technicals.MovingAverage(1,periods ,series);
                    break;
                case "W":
                    movAvg =  Technicals.MovingAverage(2,periods ,series);
                    break;
                case "T":
                    movAvg =  Technicals.MovingAverage(3,periods ,series);
                    break;
                default:
                    movAvg =  Technicals.MovingAverage(0,periods ,series);
            }
            double[] stdDev = Technicals.StandardDeviation(series, periods);
            
            int i = data.size()-1;
            double average = movAvg[i];
            double upperband = average+(stdDev[i]/stdev);
            double lowerband = average-(stdDev[i]/stdev);
            DataModel model = data.getLast();
            
            boolean buy=  model.getClose()>upperband && (signal==null || signal.getSignalTrend()!=SignalTrend.LONG) ;
           // coverShort = buy;
            boolean shortSell = model.getClose()<lowerband && (signal==null || signal.getSignalTrend()!=SignalTrend.SHORT);
            //sell = shortSell;
            SignalTrend newSignal = null;
            if(buy) newSignal = SignalTrend.LONG;
            else if(shortSell) newSignal = SignalTrend.SHORT;
            validateSignal(db,signal, newSignal, model.getClose(), model.getDate(),OrderType.MARKET);
    }

    @Override
    public void checkSignal(DBManager db, Strategy strategy, ScripStrategyParameter param, BroadcastItem broadcast, Signal signal) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	
    
    
}
