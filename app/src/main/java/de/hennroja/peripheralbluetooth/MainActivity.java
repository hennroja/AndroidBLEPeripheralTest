package de.hennroja.peripheralbluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattServer mGattServer;
    public static final byte MANUFACTURER_ID = (byte) 0x07;
    private static final UUID SERVICE_UUID = UUID.fromString("00009999-0000-1000-8000-00805f9b34fb");
    private static final String PRIVACY_MAC_UUID = "00009999-0000-1000-8000-00805f9b34fb";
    public static final byte[] PRIVACY_MAC_DATA = new byte[]{3, 1, 4};

    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (RelativeLayout) findViewById(R.id.layout);

        checkBLESupport();
        setupPeripheral();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkBLESupport() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            layout.setBackgroundColor(0xffFF0000);
            finish();
        } else {
            Log.v(TAG, "Bluetooth Low Energy is Supported");
        }
    }


    private void setupPeripheral() {

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            BluetoothLeAdvertiser peripheral = mBluetoothAdapter.getBluetoothLeAdvertiser();
            if (peripheral == null) return;
            mGattServer = bluetoothManager.openGattServer(getApplicationContext(),
                    new BluetoothGattServerCallback() {

                    });

            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW)
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                    .build();

            AdvertiseData adData = new AdvertiseData.Builder()
                    .addManufacturerData(MANUFACTURER_ID, new byte[]{MANUFACTURER_ID, 0})
                    .addServiceData(new ParcelUuid(SERVICE_UUID), PRIVACY_MAC_DATA)
                    .build();


            AdvertiseCallback callback = new AdvertiseCallback() {

                int COUNT = 0;

                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                    COUNT++;
                    System.out.println("AdvertiseCallback Count = " + COUNT);
                }
            };

            peripheral.startAdvertising(settings, adData, callback);
            layout.setBackgroundColor(0xff66CD00);


        } else {
            Toast.makeText(this, "No BLE Advertisment Support", Toast.LENGTH_SHORT).show();
            Log.v(TAG, "Advertisment is not supported");
            layout.setBackgroundColor(0xffff8d00);
        }
    }

}
