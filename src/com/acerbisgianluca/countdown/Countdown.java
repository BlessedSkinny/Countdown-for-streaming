/*
 * The MIT License
 *
 * Copyright 2018 Acerbis Gianluca <www.acerbisgianluca.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.acerbisgianluca.countdown;

import com.acerbisgianluca.filemanager.FileManager;
import java.io.IOException;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Acerbis Gianluca
 */
public class Countdown extends Observable {

    private final JLabel label;
    private final JTextField txt;
    private final JPanel panel;
    private String sTimer;
    private long totalSecs;
    private Timer timer;
    private final FileManager fm;
    private boolean save;
    private String endText;
    private boolean finished;
    private long hours, minutes, seconds;

    public Countdown(JPanel panel, JLabel label, JTextField txt) {
        this.panel = panel;
        this.label = label;
        this.txt = txt;
        this.sTimer = null;
        this.fm = new FileManager();
        this.save = false;
        this.finished = false;
    }

    public void setsTimer(String sTimer) {
        this.sTimer = sTimer;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public void setEndText(String endText) {
        this.endText = endText;
    }

    public boolean isFinished() {
        return finished;
    }
    
    public void start(){
        int strHours = Integer.parseInt(sTimer.substring(0, 2));
        int strMinutes = Integer.parseInt(sTimer.substring(3, 5));
        int strSeconds = Integer.parseInt(sTimer.substring(6));
        totalSecs = (strHours * 3600) + (strMinutes * 60) + strSeconds;
        hours = totalSecs / 3600;
        minutes = (totalSecs % 3600) / 60;
        seconds = totalSecs % 60;

        String sTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        label.setText(sTime);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(totalSecs > 0) {
                    totalSecs--;
                    hours = totalSecs / 3600;
                    minutes = (totalSecs % 3600) / 60;
                    seconds = totalSecs % 60;

                    String sTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    label.setText(sTime);
                    
                    if(save){
                        try {
                            fm.stringToFile(sTime, "timer.txt");
                        } catch (IOException | NullPointerException ex) {
                            JOptionPane.showMessageDialog(panel, "There was an unexpected error while writing to file!\nIf 'timer.txt' already exists in the same directory of this app try to delete it.", "Error with file :(", JOptionPane.ERROR_MESSAGE, null);
                            finished = true;
                            stop();
                        }
                    }
                }
                else {
                    finished = true;
                    stop();
                }
            }
        }, 1000, 1000);
    }
    
    public void stop (){
        timer.cancel();
        label.setText("00:00:00");
        txt.setText("00:00:00");
        
        if(save){
            try {
                fm.stringToFile(endText, "timer.txt");
            } catch (IOException | NullPointerException ex) {
                JOptionPane.showMessageDialog(panel, "There was an unexpected error while writing to file!\nIf 'timer.txt' already exists in the same directory of this app try to delete it.", "Error with file :(", JOptionPane.ERROR_MESSAGE, null);
            }
        }
        
        if(finished){
            setChanged();
            notifyObservers();
        }

        sTimer = null;
        save = false;
        endText = null;
        finished = false;
    }
}
