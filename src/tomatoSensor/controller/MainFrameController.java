/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tomatoSensor.controller;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import tomatoSensor.serial.SerialIO;
import tomatoSensor.serial.SerialEventListener;
import tomatoSensor.series.SapFlowSeries;

/**
 *
 * @author 中村 勇吾
 */
public class MainFrameController implements Initializable {

    //グラフ関係のインスタンス
    @FXML
    private LineChart<String, Double> mainChart, sensorChart1, sensorChart2, sensorChart3, sensorChart4, sensorChart5, sensorChart6;
    @FXML
    private Tab sensorTab1, sensorTab2, sensorTab3, sensorTab4, sensorTab5, sensorTab6;
    @FXML
    private Label flowRateSumLabel1, flowRateSumLabel2, flowRateSumLabel3, flowRateSumLabel4, flowRateSumLabel5, flowRateSumLabel6;
    private SapFlowSeries sapFlowSeries;
    private ArrayList<XYChart.Series<String, Double>> flowSeriesList, fileSeriesList;
    private Stage mainStage;

    //設定ボタン・更新ボタンのインスタンス
    @FXML
    Button settingBtn, reloadBtn;
    //設定Controllerのインスタンス
    private SettingFrameController settingFrameController;
    private Stage settingStage;

    private SerialIO serialIO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sapFlowSeries = new SapFlowSeries();
        serialIO = new SerialIO();
        //設定ウィンドウを生成・設定を行う
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingFrame.fxml"));
            Parent settingLayout = loader.load();
            //ダイアログの閉じるボタンのデザインの設定
            settingStage = new Stage(StageStyle.DECORATED);
            settingStage.setScene(new Scene(settingLayout));
            settingFrameController = loader.getController();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "SettingFrame.fxmlを読み見込めませんでした", ButtonType.OK).showAndWait();
        }
        settingStage.initOwner(mainStage);
        settingStage.initModality(Modality.WINDOW_MODAL);
        settingStage.setResizable(false);

        sapFlowSeries.setLogSaveDir(settingFrameController.getSelectedDir());
        sapFlowSeries.setSelectedSensor(settingFrameController.getSelectedSensor());
        //ファイルからデータを系列個別のグラフに張り付ける
        fileSeriesList = new ArrayList<>(sapFlowSeries.copySeries());
        sensorChart1.getData().add(fileSeriesList.get(0));
        sensorChart2.getData().add(fileSeriesList.get(1));
        sensorChart3.getData().add(fileSeriesList.get(2));
        sensorChart4.getData().add(fileSeriesList.get(3));
        sensorChart5.getData().add(fileSeriesList.get(4));
        sensorChart6.getData().add(fileSeriesList.get(5));
        //系列全体のグラフにデータを張り付ける
        updateChartData();

        //ツールチップを設定する
        settingBtn.setTooltip(new Tooltip("設定ウィンドウを開く"));
        reloadBtn.setTooltip(new Tooltip("データを更新する"));

    }

    //データの更新
    @FXML
    private void reloadData(ActionEvent event) {
        updateChartData();
        fileSeriesList = sapFlowSeries.copySeries();
        setFlowRateSum();
    }

    //設定ウィンドウを開く
    @FXML
    private void openSettingWindow(ActionEvent event) {
        settingStage.showAndWait();
        updateChartData();
        changeActiveChart();
        sapFlowSeries.setLogSaveDir(settingFrameController.getSelectedDir());
        sapFlowSeries.setSelectedSensor(settingFrameController.getSelectedSensor());

        if (!serialIO.isSerialOpened() && settingFrameController.getSelectedCOMPort() != null) {
            try {
                serialIO.openSerialPort(settingFrameController.getSelectedCOMPort());
                mainStage.setTitle("シリアル通信は確立しました - SapFlow");
                serialIO.getSerialPort().addEventListener(new SerialEventListener(serialIO.getSerialPort()));
            } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException | TooManyListenersException ex) {
                ex.printStackTrace();
            }
            //Eventがあったとき、EventListenerに伝える
            serialIO.getSerialPort().notifyOnDataAvailable(true);
        }
    }

    //アプリケーションを終了する
    @FXML
    private void closeWindow(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void saveMainChartAsImage(ActionEvent event) {
        saveNodeAsImage(mainChart, "グラフ\"AllSensors\"を画像で保存");
    }

    @FXML
    private void saveSensorChart1AsImage() {
        saveNodeAsImage(sensorChart1, "グラフ\"Sensor1\"を画像で保存");
    }

    @FXML
    private void saveSensorChart2AsImage() {
        saveNodeAsImage(sensorChart2, "グラフ\"Sensor2\"を画像で保存");
    }

    @FXML
    private void saveSensorChart3AsImage() {
        saveNodeAsImage(sensorChart3, "グラフ\"Sensor3\"を画像で保存");
    }

    @FXML
    private void saveSensorChart4AsImage() {
        saveNodeAsImage(sensorChart4, "グラフ\"Sensor4\"を画像で保存");
    }

    @FXML
    private void saveSensorChart5AsImage() {
        saveNodeAsImage(sensorChart5, "グラフ\"Sensor5\"を画像で保存");
    }

    @FXML
    private void saveSensorChart6AsImage() {
        saveNodeAsImage(sensorChart6, "グラフ\"Sensor6\"を画像で保存");
    }

    private void setFlowRateSum() {
        ArrayList<Double> flowRateSumList = sapFlowSeries.getFlowRateSumList();
        flowRateSumLabel1.setText(Double.toString(flowRateSumList.get(0)));
        flowRateSumLabel2.setText(Double.toString(flowRateSumList.get(1)));
        flowRateSumLabel3.setText(Double.toString(flowRateSumList.get(2)));
        flowRateSumLabel4.setText(Double.toString(flowRateSumList.get(3)));
        flowRateSumLabel5.setText(Double.toString(flowRateSumList.get(4)));
        flowRateSumLabel6.setText(Double.toString(flowRateSumList.get(5)));
    }

    //設定ウィンドウのセンサの指定に従って表示するグラフを設定する
    private void changeActiveChart() {
        boolean[] selectedSensor = settingFrameController.getSelectedSensor();
        sensorTab1.setDisable(!selectedSensor[0]);
        sensorTab2.setDisable(!selectedSensor[1]);
        sensorTab3.setDisable(!selectedSensor[2]);
        sensorTab4.setDisable(!selectedSensor[3]);
        sensorTab5.setDisable(!selectedSensor[4]);
        sensorTab6.setDisable(!selectedSensor[5]);
    }

    //系列全体のグラフのデータ・系列を変更する。
    private void updateChartData() {
        ArrayList<XYChart.Series<String, Double>> fileDataSeries;
        ArrayList<XYChart.Series<String, Double>> chartDataSeries;
        ArrayList<XYChart.Series<String, Double>> addSeries;
        ArrayList<XYChart.Series<String, Double>> removeSeries;

        ArrayList<XYChart.Series<String, Double>> baceDataSeriesList;
        baceDataSeriesList = new ArrayList<>(sapFlowSeries.sortSeries());

        //グラフに表示されている系列を得る
        chartDataSeries = new ArrayList<>(mainChart.getData());

        //sapFlowDataListから表示したい系列を得る。
        fileDataSeries = new ArrayList<>(baceDataSeriesList);

        //追加したい系列を選別する。
        fileDataSeries.removeAll(chartDataSeries);
        addSeries = new ArrayList<>(fileDataSeries);

        fileDataSeries = new ArrayList<>(baceDataSeriesList);

        //削除したい系列を選別する。
        chartDataSeries.removeAll(fileDataSeries);
        removeSeries = new ArrayList<>(chartDataSeries);

        //データを更新する
        flowSeriesList = new ArrayList<>(baceDataSeriesList);

        //グラフに系列を追加する
        for (int i = 0; i < addSeries.size(); i++) {
            for (int n = 0; n < flowSeriesList.size(); n++) {
                if (addSeries.get(i).getName().equals(flowSeriesList.get(n).getName())) {
                    mainChart.getData().add(n, flowSeriesList.get(n));
                }
            }
        }
        //グラフから不要な系列を消す
        mainChart.getData().removeAll(removeSeries);
    }

    private void saveNodeAsImage(Node node, String title) {
        WritableImage image = node.snapshot(null, null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG形式", "*.png"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("すべてのファイル", "*.*"));
        File file = fileChooser.showSaveDialog(mainStage);
        if (file == null) {
            return;
        }
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //mainChartを印刷する
    public void printChart() {
        Printer printer = Printer.getDefaultPrinter();
        System.out.println(Printer.getDefaultPrinter().getName());
        PageLayout pageLayout = printer.getDefaultPageLayout();
        PrinterJob printerJob = PrinterJob.createPrinterJob(printer);
        printerJob.showPrintDialog(mainStage);
        printerJob.showPageSetupDialog(mainStage);
        boolean success = printerJob.printPage(pageLayout, mainChart);
        if (success) {
            printerJob.endJob();
        }
        System.out.println("終了");
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public SerialIO getSerialIO() {
        return serialIO;
    }
}
