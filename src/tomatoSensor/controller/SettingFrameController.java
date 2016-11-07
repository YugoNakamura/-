/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tomatoSensor.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javax.mail.MessagingException;
import tomatoSensor.mail.MailSender;

/**
 *
 * @author 中村 勇吾
 */
public class SettingFrameController implements Initializable {

    //センサーの総数
    final int SENSOR_PORT_LENGTH = 6;

    //一般設定タブ
    @FXML
    private ChoiceBox<String> comportChoiceBox;
    @FXML
    private TextField saveDirPath;
    @FXML
    private CheckBox sensorCombo1, sensorCombo2, sensorCombo3, sensorCombo4, sensorCombo5, sensorCombo6;
    boolean selectedSensor[] = new boolean[SENSOR_PORT_LENGTH];

    //メール設定タブ
    @FXML
    private CheckBox alertMailSetting, regularlyMailSetting;
    @FXML
    private TextField senderAddressTextField, receiverAddressTextField, smtpAddressTextField,
            smtpPortTextField, userNameTextField;
    @FXML
    private PasswordField userPasswordTextField;

    private File selectedDir;

    private MailSender mailSender;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.mailSender = new MailSender();
        this.selectedDir = new File("logData/");
        saveDirPath.setText(selectedDir.getAbsolutePath());
        for(int i=0;i<selectedSensor.length;i++) {
            selectedSensor[i] = true;
        }
        
    }

    @FXML
    private void searchDir(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        selectedDir = dc.showDialog(((Node) event.getSource()).getScene().getWindow());
        saveDirPath.setText(selectedDir.getAbsolutePath());
    }

    @FXML
    private void sendTestMail() {
        try {
            mailSender.setFrom(senderAddressTextField.getText());
            mailSender.setTo(receiverAddressTextField.getText());
            mailSender.setStmp(smtpAddressTextField.getText());
            mailSender.setPort(Integer.valueOf(smtpPortTextField.getText()));
            mailSender.setUserName(userNameTextField.getText());
            mailSender.setPassword(userPasswordTextField.getText());
            mailSender.setSubject("Tomato Sensor Test Mail");
            mailSender.setMsg("This is a test mail form tomato sensor.\n");
            mailSender.sendMail();
        } catch (MessagingException | NumberFormatException e) {
            Alert sendFailer = new Alert(Alert.AlertType.ERROR, "テストメールの送信に失敗しました。", ButtonType.OK);
            sendFailer.show();
            return;
        }
        Alert sendSuccess = new Alert(Alert.AlertType.INFORMATION, "テストメールの送信に成功しました。", ButtonType.OK);
        sendSuccess.show();
    }

    //保存ボタンが押された時の動作
    @FXML
    private void submit(ActionEvent event) {
        String selectedComPort = comportChoiceBox.getValue();
        String logDataPath = saveDirPath.getText();

        //メールの送信設定
        if (alertMailSetting.isSelected() || regularlyMailSetting.isSelected()) {
            try {
                mailSender.setFrom(senderAddressTextField.getText());
                mailSender.setTo(receiverAddressTextField.getText());
                mailSender.setStmp(smtpAddressTextField.getText());
                mailSender.setPort(Integer.valueOf(smtpPortTextField.getText()));
                mailSender.setUserName(userNameTextField.getText());
                mailSender.setPassword(userPasswordTextField.getText());
                mailSender.saveSetting();
            } catch (IOException | NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "データの不正", ButtonType.OK).showAndWait();
            }
        }

        //センサの選択
        selectedSensor[0] = sensorCombo1.isSelected();
        selectedSensor[1] = sensorCombo2.isSelected();
        selectedSensor[2] = sensorCombo3.isSelected();
        selectedSensor[3] = sensorCombo4.isSelected();
        selectedSensor[4] = sensorCombo5.isSelected();
        selectedSensor[5] = sensorCombo6.isSelected();

        //設定ウィンドウを閉じる
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    private void cancel(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public File getSelectedDir() {
        return selectedDir;
    }

    public boolean[] getSelectedSensor() {
        return selectedSensor;
    }
}