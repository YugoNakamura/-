/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tomatoSensor.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author 中村 勇吾
 */
public class FileIO {
    public void fileWrite(File file) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file);
//            fileWriter.write(str);
        } catch(IOException e) {
            
        }
    } 
}
