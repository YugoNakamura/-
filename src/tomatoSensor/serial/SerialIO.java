/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tomatoSensor.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *
 * @author NakamuraYugo
 */
public class SerialIO implements SerialPortEventListener {

    SerialPort serialPort;
    CommPortIdentifier portID;

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.library.path"));
        SerialIO test = new SerialIO();
        test.getSetialPortList();
    }

    /**
     * 利用できるシリアルポートの一覧を取得する
     */
    public ArrayList<String> getSetialPortList() {
        ArrayList<String> portList = new ArrayList<>();
        //システムにあるシリアルポート一覧を返す(プログラムに使われてみるかは調べない)
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier) portEnum.nextElement();
            if (!port.isCurrentlyOwned()) {
                if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    portList.add(port.getName());
                }
            }
        }
        return portList;
    }

    /**
     * 特定のシリアルポートを開く
     * ボーレート：9600
     * データビット：8
     * ストップビット：1
     * パリティ：イーブン
     * フロー制御：無し
     */
    public boolean openSerialPort(String portName) {
        int timeOut = 2000;
        int baudRate = 9600;
        try {
            portID = CommPortIdentifier.getPortIdentifier(portName);
            //シリアルポートをopenするとき、文字列を与えて、競合が起こった時に対応できるようにする。同時にタイムアウトを定めて置く。[msec]
            serialPort = (SerialPort)portID.open(getClass().getSimpleName(), timeOut);
            //ボーレート・データビット数などを設定する。
            serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
            //フロー制御の設定
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        } catch (NoSuchPortException e) {
            e.printStackTrace();
            return false;
        } catch (PortInUseException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void closeSerialPort() {
        serialPort.close();
    }

    /**
     * シリアルデータを受信した時のイベント
     */
    @Override
    public void serialEvent(SerialPortEvent setialEvent) {

    }
}
