/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tomatoSensor.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tomatoSensor.series.SapFlowSeries;

/**
 *
 * @author 中村 勇吾
 */
public class MainFrameController implements Initializable {

    //グラフ関係のインスタンス
    @FXML
    private LineChart<String, Double> mainChart;
    private SapFlowSeries sapFlowDataList;
    private ArrayList<XYChart.Series<String, Double>> flowDataList;
    private Stage mainStage;

    //設定Controllerのインスタンス
    private SettingFrameController settingFrameController;
    private Stage settingStage;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sapFlowDataList = new SapFlowSeries();
        //設定ウィンドウのFXMLファイル
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingFrame.fxml"));
            Parent settingLayout = loader.load();
            //ダイアログの閉じるボタンのデザインの設定
            settingStage = new Stage(StageStyle.DECORATED);
            settingStage.setScene(new Scene(settingLayout));
            settingFrameController = loader.getController();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "ファイルを読み見込めませんでした", ButtonType.OK).showAndWait();
        }

        //流速のCSVファイル
        updateChartData();
        /*        for (int i = 0; i < settingFrameController.getSelectedSensor().length; i++) {
            mainChart.getData().add(flowDataList.get(i));
        }*/
    }

    @FXML
    private void reloadData(ActionEvent event) {
        updateChartData();
    }

    private boolean once = true;

    @FXML
    private void openSettingWindow(ActionEvent event) {
        if (once) {
            settingStage.initOwner(mainStage);
            settingStage.initModality(Modality.WINDOW_MODAL);
            settingStage.setResizable(false);
            once = false;
        }
        settingStage.showAndWait();
        updateChartData();
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Platform.exit();
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    private void updateChartData() {
        /*        int sensorLength = 0;
        int seriesLength = 0;
        boolean selectedSensor[] = settingFrameController.getSelectedSensor();

        flowDataList = sapFlowDataList.updateSeries(settingFrameController.getSelectedSensor(), settingFrameController.getSelectedDir());

        for (int i = 0; i < selectedSensor.length; i++) {
            if (selectedSensor[i]) {
                sensorLength++;
            }
        }
        seriesLength = mainChart.getData().size();
        
        ArrayList<XYChart.Series<String,Double>> removeSeries = new ArrayList<>();
        if (sensorLength < seriesLength) {
            for (int i = 0; i < selectedSensor.length; i++) {
                if(selectedSensor[i]==false) {
                    removeSeries.add(mainChart.getData().get(i));
                }
            }
            mainChart.getData().removeAll(removeSeries);
        } else if (sensorLength > seriesLength) {
            for (int i = 0; i < sensorLength - seriesLength; i++) {
                mainChart.getData().add(flowDataList.get(seriesLength + i));
            }
        }*/

        if (mainChart.getData().size() > 0) {
            for (int i = 0; i < 6; i++) {
                System.out.println("a" + mainChart.getData().get(i).getName());
            }
        }

        ArrayList<XYChart.Series<String, Double>> fileDataSeries;
        ObservableList<XYChart.Series<String, Double>> chartDataSeries;
        ArrayList<XYChart.Series<String, Double>> addSeries;
        ArrayList<XYChart.Series<String, Double>> removeSeries;

        //グラフに表示されているデータを得る
        chartDataSeries = mainChart.getData();
        //sapFlowDataListから表示したいデータを得る。
        fileDataSeries = sapFlowDataList.updateSeries(settingFrameController.getSelectedSensor(), settingFrameController.getSelectedDir());

        //追加したいデータを選別する。
        fileDataSeries.removeAll(chartDataSeries);
        addSeries = new ArrayList<>(fileDataSeries);

        fileDataSeries = sapFlowDataList.updateSeries(settingFrameController.getSelectedSensor(), settingFrameController.getSelectedDir());

        //削除したいデータを選別する。
        chartDataSeries.removeAll(fileDataSeries);
        removeSeries = new ArrayList<>(chartDataSeries);

        if (removeSeries.size() != 0) {
            mainChart.getData().removeAll(removeSeries);
        }
        
        flowDataList = sapFlowDataList.updateSeries(settingFrameController.getSelectedSensor(), settingFrameController.getSelectedDir());
        
        for (int i = 0; i < addSeries.size(); i++) {
            for (int n = 0; n < fileDataSeries.size(); n++) {
                if (addSeries.get(i).getName().equals(fileDataSeries.get(n).getName())) {
                    mainChart.getData().add(flowDataList.get(n));
                }
            }
        }
    }
}
