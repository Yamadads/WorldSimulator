/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldsimulator;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class supports all file operations
 *
 * @author Sebastian
 */
public class FileSupport {

    private volatile HashMap<String, BufferedReader> bufferedFileReaders;

    /**
     * Constructor, initiate all collections
     */
    public FileSupport() {
        bufferedFileReaders = new HashMap<String, BufferedReader>();
    }


    /**
     * Initiate BufferedReader and mark(0) at the begining
     *
     * @param file
     * @return
     */
    private BufferedReader createBuffer(String fileName) {
        BufferedReader buffer=null;
        try {
            buffer = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FileSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            buffer.mark(0);
        } catch (IOException ex) {
            Logger.getLogger(FileSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return buffer;
    }

    /**
     * Reading from file
     *
     * @param fileName
     * @return String with single line from selected file
     */
    public String readLineFromFile(String fileName) {
        String line = "";
        if (bufferedFileReaders.containsKey(fileName)) {
            line = readLineFromBuffer(bufferedFileReaders.get(fileName));
        } else {
            bufferedFileReaders.put(fileName, createBuffer(fileName));
            line = readLineFromBuffer(bufferedFileReaders.get(fileName));
        }
        return line;
    }

    /**
     * Read line from BufferedReader, if EOF read from the beginning
     *
     * @param file BufferedReader
     * @return single line from file
     */
    private String readLineFromBuffer(BufferedReader file) {
        String line;
        try {
            line = file.readLine();
            if (line != null) {
                return line;
            } else {
                file.reset();
                line = file.readLine();
                if (line != null) {
                    return line;
                }
            }
        } catch (IOException e) {
            System.out.println("FILE READ ERROR!");
            System.exit(2);
        }
        return "";
    }

    /**
     * close all files
     */
    public void closeAllFiles() {
        for (Entry<String, BufferedReader> fr : bufferedFileReaders.entrySet()) {
            closeFile(fr.getValue());
        }
        bufferedFileReaders.clear();
    }

    /**
     * Close selected file
     *
     * @param file a file to close
     */
    private void closeFile(BufferedReader file) {
        try {
            file.close();
        } catch (IOException e) {
            System.out.println("ERROR IN CLOSING A FILE!");
            System.exit(3);
        }
    }

    /**
     * Open file with results and return ArrayList with results
     *
     * @param fileName name of file with results
     * @return sorted list of best results
     */
    public ArrayList<Result> getRanking(String fileName) {
        ArrayList<Result> results = new ArrayList<>();
        XMLDecoder file = null;
        try {
            file = new XMLDecoder(new BufferedInputStream(new FileInputStream(fileName)));
        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
        }
        if (file == null) {
            return null;
        }
        try {
            results = (ArrayList<Result>) file.readObject();
        } catch (Exception o) {
            System.out.print("Reading XML ERROR");
        }
        file.close();
        return results;
    }

    /**
     * Save Ranking in file
     *
     * @param fileName name of file with results
     * @param actualResult Object with actual Result
     */
    public void saveRanking(String fileName, Result actualResult) {
        ArrayList<Result> results = getRanking(fileName);
        if (results == null) {
            results = new ArrayList<Result>();
        }
        results.add(actualResult);
        Collections.sort(results);
        while (results.size() > 5) {
            results.remove(5);
        }
        XMLEncoder file = null;
        try {
            file = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(fileName)));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < results.size(); i++) {
            file.writeObject(results);
        }
        file.close();
    }

}
