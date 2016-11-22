/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tomatoSensor.series;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author d12131
 */
public class SapFlowSeries {

    private final int SENSOR_PORT_LENGTH = 6;
    private File logSaveDir;
    private FileReader fileReader;
    private BufferedReader br;
    private ArrayList<XYChart.Series<String, Double>> dataList;
    private ArrayList<XYChart.Series<String, Double>> individualSeries;
    private boolean[] selectedSensor;
    //茎の導管の半径[μm](推定値)
    private final int CONDUIT_RADIUS = 85;
    private final int CONDUIT_LENGTH = 6;

    public SapFlowSeries() {
        dataList = new ArrayList<>();
        individualSeries = new ArrayList<>();
        for (int i = 0; i < SENSOR_PORT_LENGTH; i++) {
            dataList.add(new XYChart.Series<>());
            individualSeries.add(new XYChart.Series<>());
        }
    }

    private ArrayList<XYChart.Series<String, Double>> getFileData() {
        try {
            fileReader = new FileReader("./logData/sampleData.csv");
            br = new BufferedReader(fileReader);
            String line;
            String[] dataStr;

            resetDataList();
            line = br.readLine();
            dataStr = line.split(",");
            setSeriesName(dataStr);
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                dataStr = line.split(",");
                setData(dataStr);
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "ファイルを読み見込めませんでした", ButtonType.OK).showAndWait();
        } finally {
            try {
                br.close();
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    public ArrayList<XYChart.Series<String, Double>> copySeries() {
        ArrayList<XYChart.Series<String, Double>> fileSeries = getFileData();
        for (int i = 0; i < fileSeries.size(); i++) {
            individualSeries.get(i).getData().removeAll(individualSeries.get(i).getData());
            individualSeries.get(i).setName(fileSeries.get(i).getName());
            for (int n = 0; n < fileSeries.get(i).getData().size(); n++) {
                XYChart.Data<String, Double> data = fileSeries.get(i).getData().get(n);
                individualSeries.get(i).getData().add(new XYChart.Data<>(data.getXValue(), data.getYValue()));
            }
        }
        return individualSeries;
    }

    public ArrayList<XYChart.Series<String, Double>> sortSeries() {
        ArrayList<XYChart.Series<String, Double>> returnDataList = new ArrayList<>(getFileData());
        ArrayList<XYChart.Series<String, Double>> removeDataList = new ArrayList<>();

        for (int i = 0; i < selectedSensor.length; i++) {
            if (selectedSensor[i] == false) {
                removeDataList.add(returnDataList.get(i));
            }
        }
        returnDataList.removeAll(removeDataList);
        return returnDataList;
    }

    public ArrayList<Double> getFlowRateSumList() {
        ArrayList<Double> sumFlowRateList = new ArrayList<>();
        double sumFlowRate = 0.0;
        ArrayList<XYChart.Series<String, Double>> fileDataSeriesList = getFileData();
        XYChart.Series<String,Double> series;
        //始点の流速と終点の流速の和
        double sum;
        //計測間隔の秒数
        long widthTime;
        int startMin,startSec,endMin,endSec;
        String startStr,endStr;
        for (int i = 0; i < fileDataSeriesList.size(); i++) {
            series = fileDataSeriesList.get(i);
            for (int n = 0; n < fileDataSeriesList.get(i).getData().size() - 1; n++) {
                sum = series.getData().get(n).getYValue()/1000 + series.getData().get(n+1).getYValue()/1000;
                startStr = series.getData().get(n).getXValue();
                endStr = series.getData().get(n+1).getXValue();
                startMin = Integer.valueOf(startStr.substring(0, 2));
                startSec = Integer.valueOf(startStr.substring(3, 5));
                endMin = Integer.valueOf(endStr.substring(0, 2));
                endSec = Integer.valueOf(endStr.substring(3, 5));
                widthTime = Duration.between(LocalDateTime.of(1, 1, 1, startMin, startSec), LocalDateTime.of(1, 1, 1, endMin, endSec)).getSeconds();
                sumFlowRate+= (sum*widthTime)/2;
            }
            sumFlowRateList.add(sumFlowRate);
            sumFlowRate = 0;
        }
        return sumFlowRateList;
    }

    private void resetDataList() {
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).getData().removeAll(dataList.get(i).getData());
        }
    }

    private void setData(String[] dataSrc) {
        for (int i = 0; i < SENSOR_PORT_LENGTH; i++) {
            dataList.get(i).getData().add(new XYChart.Data(dataSrc[0], Double.valueOf(dataSrc[i + 1])));
        }
    }

    private void setSeriesName(String[] dataSrc) {
        for (int i = 0; i < SENSOR_PORT_LENGTH; i++) {
            dataList.get(i).setName(dataSrc[i + 1]);
        }
    }

    public void setLogSaveDir(File logSaveDir) {
        this.logSaveDir = logSaveDir;
    }

    public void setSelectedSensor(boolean[] selectedSensor) {
        this.selectedSensor = selectedSensor;
    }
}
