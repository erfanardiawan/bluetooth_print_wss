package com.example.bluetooth_print;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// This Class
// Add by: GOPAN
public class USBPrinterAdapter {

    private USBPrinterAdapter mInstance;
    private Context mContext;
    private UsbManager mUSBManager;
    private PendingIntent mPermissionIndent;
    UsbDevice mUsbDevice;
    private UsbDeviceConnection mUsbDeviceConnection;
    private UsbInterface mUsbInterface;
//    private BroadcastReceiver mUsbDeviceReceiver;

    static final String ACTION_USB_PERMISSION = "gopan.flutter_usb_printer.USB_PERMISSION";

    USBPrinterAdapter getInstance() {
        if (mInstance == null) {
            mInstance = this;
        }
        return mInstance;
    }

//    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
//                synchronized (this) {
//                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                        mUsbDevice = usbDevice;
//                    } else {
//                        Toast.makeText(context, "User refused to give USB device permissions" + usbDevice.getDeviceName(),
//                                Toast.LENGTH_LONG).show();
//                    }
//                }
//            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
//                if (mUsbDevice != null) {
//                    Toast.makeText(context, "USB device has been turned off", Toast.LENGTH_LONG)
//                            .show();
//                    closeConnectionIfExists();
//                }
//            }
//        }
//    };

    void init(Context reactContext, BroadcastReceiver mUsbDeviceReceiver) {
        mContext = reactContext;
        mUSBManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        mPermissionIndent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mContext.registerReceiver(mUsbDeviceReceiver, filter);
    }

    void closeConnectionIfExists() {
        if (mUsbDeviceConnection != null) {
            mUsbDeviceConnection.releaseInterface(mUsbInterface);
            mUsbDeviceConnection.close();
            mUsbInterface = null;
//            mEndPoint = null
            mUsbDeviceConnection = null;
            mUsbDevice = null;
        }
    }

    List<UsbDevice> getDeviceList() {
        if (mUSBManager == null) {
            Toast.makeText(mContext,
                    "USB Manager is not initialized while get device list",
                    Toast.LENGTH_LONG
            ).show();
            return new ArrayList<>();
        }
        return new ArrayList<>(mUSBManager.getDeviceList().values());
    }

    UsbDevice selectDevice(int vendorId, int productId) {
//        if (mUsbDevice == null || mUsbDevice.getVendorId() != vendorId || mUsbDevice.getProductId() != productId) {
        closeConnectionIfExists();
        List<UsbDevice> usbDevices = getDeviceList();
        for (UsbDevice usbDevice : usbDevices) {
            if (usbDevice.getVendorId() == vendorId && usbDevice.getProductId() == productId) {
                closeConnectionIfExists();
                mUSBManager.requestPermission(usbDevice, mPermissionIndent);
                return usbDevice;
            }
        }
        return null;
//        }
//        return null;
    }
}
