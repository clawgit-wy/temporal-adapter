package com.yutong.safety.common.utils.excel;

import com.yutong.safety.common.utils.StreamUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CsvUtil {
    public static void writeCsv() throws Exception {
        FileOutputStream fos = new FileOutputStream("E:/abc.csv");
        OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");

        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("姓名", "年龄", "家乡");
        CSVPrinter csvPrinter = new CSVPrinter(osw, csvFormat);
        //csvPrinter = CSVFormat.DEFAULT.withHeader("姓名", "年龄", "家乡").print(osw);

        for (int i = 0; i < 10; i++) {
            csvPrinter.printRecord("张三", 20, "湖北");
        }

        csvPrinter.flush();
        csvPrinter.close();
    }

    public static List<Map> readCsv(InputStream is, String[] headArray){
        List<Map> ret = new ArrayList<>();
        InputStreamReader isr = null;
        Reader reader = null;
        CSVParser parser = null;
        try {
            isr = new InputStreamReader(is, "GBK");
            reader = new BufferedReader(isr);
            parser = CSVFormat.EXCEL.withHeader(headArray).parse(reader);
            List<CSVRecord> list = parser.getRecords();
            for (CSVRecord record : list) {
                Map map = new HashMap();
                for(int i=0;i<headArray.length;i++){
                    map.put("key"+i, record.get(headArray[i]));
                }
                ret.add(map);
                /*System.out.println(record.getRecordNumber()
                        + ":" + record.get("name")
                        + ":" + record.get("age")
                        + ":" + record.get("jia"));*/
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            CsvUtil.close(parser);
            StreamUtil.close(reader);
            StreamUtil.close(isr);
            StreamUtil.close(is);
        }
        return ret;
    }

    public static void readCsv() throws IOException {
        InputStream is = new FileInputStream("E:/cjsworkspace/cjs-excel-demo/target/abc.csv");
        InputStreamReader isr = new InputStreamReader(is, "GBK");
        Reader reader = new BufferedReader(isr);

        CSVParser parser = CSVFormat.EXCEL.withHeader("name", "age", "jia").parse(reader);
        //CSVParser csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT.withHeader("name", "age", "jia"));
        List<CSVRecord> list = parser.getRecords();
        for (CSVRecord record : list) {
            System.out.println(record.getRecordNumber()
                    + ":" + record.get("name")
                    + ":" + record.get("age")
                    + ":" + record.get("jia"));
        }
        parser.close();
        reader.close();
        isr.close();
        is.close();
    }

    /**
     * Parsing an Excel CSV File
     */
    public static void parseCsv() throws Exception {
        Reader reader = new FileReader("C:/Users/Administrator/Desktop/abc.csv");
        CSVParser parser = CSVFormat.EXCEL.parse(reader);
        for (CSVRecord record : parser.getRecords()) {
            System.out.println(record);
        }
        parser.close();
    }

    /**
     * Defining a header manually
     */
    public static void parseCsvWithHeader() throws Exception {
        Reader reader = new FileReader("C:/Users/Administrator/Desktop/abc.csv");
        CSVParser parser = CSVFormat.EXCEL.withHeader("id", "name", "code").parse(reader);
        for (CSVRecord record : parser.getRecords()) {
            System.out.println(record.get("id") + ","
                    + record.get("name") + ","
                    + record.get("code"));
        }
        parser.close();
    }

    /**
     * Using an enum to define a header
     */
    enum MyHeaderEnum {
        ID, NAME, CODE
    }

    public static void parseCsvWithEnum() throws Exception {
        Reader reader = new FileReader("C:/Users/Administrator/Desktop/abc.csv");
        CSVParser parser = CSVFormat.EXCEL.withHeader(MyHeaderEnum.class).parse(reader);
        for (CSVRecord record : parser.getRecords()) {
            System.out.println(record.get(MyHeaderEnum.ID) + ","
                    + record.get(MyHeaderEnum.NAME) + ","
                    + record.get(MyHeaderEnum.CODE));
        }
        parser.close();
    }

    public void writeMuti() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch doneSignal = new CountDownLatch(2);

        List<Map<String, String>> recordList = new ArrayList<>();

        executorService.submit(new exportThread("E:/0.csv", recordList, doneSignal));
        executorService.submit(new exportThread("E:/1.csv", recordList, doneSignal));

        doneSignal.await();
        System.out.println("Finish!!!");
    }

    class exportThread implements Runnable {

        private String filename;
        private List<Map<String, String>> list;
        private CountDownLatch countDownLatch;

        public exportThread(String filename, List<Map<String, String>> list, CountDownLatch countDownLatch) {
            this.filename = filename;
            this.list = list;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                CSVPrinter printer = new CSVPrinter(new FileWriter(filename), CSVFormat.EXCEL.withHeader("NAME", "CODE"));
                for (Map<String, String> map : list) {
                    printer.printRecord(map.values());
                }
                printer.close();
                countDownLatch.countDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(CSVParser o){
        try {
            if(o != null){
                o.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
