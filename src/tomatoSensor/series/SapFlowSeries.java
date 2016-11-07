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
    private boolean[] selectedSensor;

    public SapFlowSeries() {
        dataList = new ArrayList<>();
        for (int i = 0; i < SENSOR_PORT_LENGTH; i++) {
            dataList.add(new XYChart.Series<>());
        }
    }

    public ArrayList<XYChart.Series<String, Double>> updateSeries(boolean selectedSensor[], File saveLogDir) {
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
            }
        }

        ArrayList<XYChart.Series<String, Double>> returnDataList = new ArrayList<>(dataList);        
        ArrayList<XYChart.Series<String, Double>> removeDataList = new ArrayList<>();
        
        for (int i = 0; i < selectedSensor.length; i++) {
            if (selectedSensor[i] == false) {
                removeDataList.add(returnDataList.get(i));
            }
        }
        returnDataList.removeAll(removeDataList);
        return returnDataList;
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
}
