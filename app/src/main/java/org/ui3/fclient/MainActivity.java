package org.ui3.fclient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;


import org.ui3.fclient.databinding.ActivityMainBinding;

interface TransactionEvents {
    String enterPin(int ptc, String amount);
    void transactionResult(boolean result);
}

public class MainActivity extends AppCompatActivity implements TransactionEvents{

    // Used to load the 'fclient' library on application startup.
    static {
        System.loadLibrary("fclient");
        System.loadLibrary("mbedcrypto");

    }

    private ActivityMainBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;

    private String pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int res = initRng();
        byte[] v = randomBytes(10);

        byte[] key = new byte[16];
        byte[] dataOne = "Hello world".getBytes();
        byte[] encryptedData = encrypt(key, dataOne);
        byte[] decryptedData = decrypt(key, encryptedData);
        Log.d("decryptedData", new String(decryptedData));

        // Example of a call to a native method
//        TextView tv = binding.sampleText;
//        tv.setText(stringFromJNI());

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        //String pin = data.getStringExtra("pin");
                        assert data != null;
                        pin = data.getStringExtra("pin");
                        synchronized (MainActivity.this) {
                            MainActivity.this.notifyAll();
                        }
                    }
                });


    }

    @Override
    public String enterPin(int ptc, String amount){
        pin = new String();
        Intent it = new Intent(MainActivity.this, PinpadActivity.class);
        it.putExtra("ptc", ptc);
        it.putExtra("amount", amount);
        synchronized (MainActivity.this) {
            activityResultLauncher.launch(it);
            try {
                MainActivity.this.wait();
            } catch (Exception ex) {
                //todo: log error
            }
        }
        return pin;
    }

    @Override
    public void transactionResult(boolean result) {
        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, result ? "ok" : "failed", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * A native method that is implemented by the 'fclient' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);
    public native boolean transaction(byte[] trd);

    public static byte[] stringToHex(String s)
    {
        byte[] hex;
        try
        {
            hex = Hex.decodeHex(s.toCharArray());
        }
        catch (DecoderException ex)
        {
            hex = null;
        }
        return hex;
    }

    public void onButtonClick(View v)
    {
        byte[] trd = stringToHex("9F0206000000000100");
        transaction(trd);
    }

}