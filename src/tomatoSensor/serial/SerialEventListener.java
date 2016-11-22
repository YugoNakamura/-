/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tomatoSensor.serial;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author NakamuraYugo
 */
public class SerialEventListener implements SerialPortEventListener {
    SerialPort serialPort;
    public SerialEventListener(SerialPort serialPort) {
        this.serialPort = serialPort;
    }
    /**
     * シリアルデータを受信した時のイベント
     */
    @Override
    public void serialEvent(SerialPortEvent serialEvent) {
        final char STX = 0x02;
        final char ETX = 0x03;
        int receivedData;
        //大量の文字列の連結を行うのでStringBufferを使用する。
        StringBuffer buffer = new StringBuffer();
        InputStream in = null;

        try {
            in = serialPort.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        if (serialEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            while (true) {
                try {
                    receivedData = in.read();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }
                if (receivedData == STX) {
                    buffer.delete(0, buffer.length());
                    continue;
                }
                if (receivedData == ETX || receivedData == -1) {
                    break;
                }
                buffer.append((char) receivedData);
            }
            System.out.println(buffer);
        }
    }
}
