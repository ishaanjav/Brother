package com.example.anany.brother;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;

public class MainActivity extends AppCompatActivity {

    Button b;
    TextView tView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b = findViewById(R.id.b);
        tView = findViewById(R.id.text);
        //Print();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printUSB();
                //print();
            }
        });

    }

    public void printUSB(){
        Printer myPrinter = new Printer();

        //print setting
        PrinterInfo printerInfo;
        printerInfo = myPrinter.getPrinterInfo();
        printerInfo.printerModel = PrinterInfo.Model.QL_820NWB;
        printerInfo.port = PrinterInfo.Port.USB;
        printerInfo.paperSize = PrinterInfo.PaperSize.A7;
        printerInfo.orientation = PrinterInfo.Orientation.PORTRAIT;
        printerInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAGE;
        printerInfo.numberOfCopies = 1;
        printerInfo.halftone = PrinterInfo.Halftone.PATTERNDITHER;
        myPrinter.setPrinterInfo(printerInfo);

        //prepare usb connection
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbDevice usbDevice = myPrinter.getUsbDevice(usbManager);
        PendingIntent permissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(), 0);
        usbManager.requestPermission(usbDevice, permissionIntent);

        //print
        PrinterStatus printResult;
        printResult = new PrinterStatus();
        printResult = myPrinter.printFile("/mnt/sdcard/ABC.jpg");
        tView.setText("Battery Level: " + printResult.batteryLevel);

    }

    public void print() {
        Thread trd = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String externalStorageDir = Environment.getExternalStorageDirectory().toString();
                    // define printer and printer setting information
                    Printer printer = new Printer();
                    PrinterInfo printInfo = new PrinterInfo();
                    printInfo.printerModel = PrinterInfo.Model.QL_820NWB;
                    printInfo.port = PrinterInfo.Port.BLUETOOTH;
                    printInfo.customPaper = externalStorageDir + "/rj3150_76mm.bin";
                   // printInfo.macAddress = "00:11:EE:BB:AA:CC";
                    printInfo.ipAddress = "192.168.118.1";
                    printer.setPrinterInfo(printInfo);

                    // Pass Bluetooth adapter to the library (Bluetooth only)
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    printer.setBluetooth(bluetoothAdapter);

                    //print
                    String srcPath = externalStorageDir + "/sample.png";
                    printer.startCommunication();
                    //PrinterStatus status = printer.printFile(srcPath);
                    printer.endCommunication();
                } catch (Exception e) {
                    makeToast(e.toString());
                }
            }
        });
        trd.start();
    }

    public void Print() {
        Printer myPrinter = new Printer();
        PrinterInfo myPrinterInfo = new PrinterInfo();
        PrinterStatus myPrinterStatus = new PrinterStatus();
        makeToast("Battery Level: " + myPrinterStatus.batteryLevel);
        try {
            // Retrieve printer information
            myPrinterInfo = myPrinter.getPrinterInfo();

            // Set printer information
            myPrinterInfo.printerModel = PrinterInfo.Model.QL_820NWB;
            myPrinterInfo.port = PrinterInfo.Port.NET;
            myPrinterInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAGE;
            myPrinterInfo.paperSize = PrinterInfo.PaperSize.CUSTOM;

            myPrinterInfo.ipAddress = "192.168.1.90";
            //myPrinterInfo.macAddress="00:00:00:00:00"; //hidden for security reasons

            LabelInfo mLabelInfo = new LabelInfo();
            mLabelInfo.labelNameIndex = LabelInfo.QL700.W50.ordinal();
            mLabelInfo.isAutoCut = true;
            mLabelInfo.isEndCut = true;
            mLabelInfo.isHalfCut = false;
            mLabelInfo.isSpecialTape = false;
            myPrinter.setPrinterInfo(myPrinterInfo);
            myPrinter.setLabelInfo(mLabelInfo);

            // Create bitmap
            Bitmap bmap = BitmapFactory.decodeResource(getResources(), R.drawable.smiling);

            try {
                tView.append("Start" + "\n");

                myPrinter.startCommunication();
                PrinterStatus printerStatus = myPrinter.printImage(bmap);
                myPrinter.endCommunication();

                tView.append(printerStatus.errorCode.toString() + "\n");

            } catch (Exception e) {
                tView.setText(e.toString());
            }

        } catch (Exception e) {
            tView.setText(e.toString());
            //e.printStackTrace();
        }

    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}
