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
import gnu.io.UnsupportedCommOperationException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *
 * @author NakamuraYugo
 */
public class SerialIO  {

    private SerialPort serialPort;
    private CommPortIdentifier portID;
    private boolean serialOpened;
    
    /**
     * 利用できるシリアルポートの一覧を取得する
     *
     * @return
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
     * 特定のシリアルポートを開く ボーレート：9600 データビット：8 ストップビット：1 パリティ：イーブン フロー制御：無し
     */
    public void openSerialPort(String portName) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
        final int TIME_OUT = 2000;
        final int BAUDRATE = 9600;
        portID = CommPortIdentifier.getPortIdentifier(portName);
        //シリアルポートをopenするとき、文字列を与えて、競合が起こった時に対応できるようにする。同時にタイムアウトを定めて置く。[msec]
        serialPort = (SerialPort) portID.open(getClass().getSimpleName(), TIME_OUT);
        //ボーレート・データビット数などを設定する。
        serialPort.setSerialPortParams(BAUDRATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
        //フロー制御はしない
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        serialOpened=true;
    }

    public void closeSerialPort() {
        serialPort.close();
        serialOpened=false;
    }
    
    public SerialPort getSerialPort() {
        return serialPort;
    }

    public CommPortIdentifier getPortID() {
        return portID;
    }

    public boolean isSerialOpened() {
        return serialOpened;
    }
}
