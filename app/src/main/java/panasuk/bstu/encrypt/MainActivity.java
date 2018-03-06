package panasuk.bstu.encrypt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.LayerDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private final String fileName = "key.txt";
    private final String fileName2 = "text.txt";
    private String cipherText, secretKey, seed;
    private File file, fileJSON;
    private SecretKeySpec secretKeySpec;
    private EditText et_text;
    private TextView tv_encrypt_text, decr;
    private Button btn_go_encrypt;
    private byte[] key, enc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_text = findViewById(R.id.et_text);
        tv_encrypt_text = findViewById(R.id.tv_encrypt);
        btn_go_encrypt = findViewById(R.id.btn_go);
        decr = findViewById(R.id.tv_decr);
        seed = "mortystrk is a best arms warrior";

        btn_go_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!setupSecretKey())
                    return;
                if (!encode(et_text.getText().toString()))
                    return;
                cipherText = tv_encrypt_text.getText().toString();
                cipherText = cipherText.replaceAll("\n", "");

                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(key);
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

               // writeJSON(cipherText);

                et_text.setText("");

                Toast.makeText(getApplicationContext(), "Данные записаны в файл", Toast.LENGTH_SHORT).show();

                /*byte[] decodedBytes = null;
                try {
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                    decodedBytes = cipher.doFinal(enc);

                    decr.setText(new String(decodedBytes));
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });

        file = new File(Environment.getExternalStorageDirectory(), fileName);
        //file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        fileJSON = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName2);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!fileJSON.exists()) {
            try {
                fileJSON.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean setupSecretKey() {

        secretKeySpec = null;

        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(seed.getBytes());
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128, secureRandom);
            key = (keyGenerator.generateKey().getEncoded());
            secretKey = new BigInteger(1, key).toString(16);
            secretKeySpec = new SecretKeySpec(key, "AES");
            return true;
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(getApplicationContext(), "AES secret key spec error", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean encode(String text) {

        byte[] encodeBytes = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encodeBytes = cipher.doFinal(text.getBytes());

            try {
                FileOutputStream fos = new FileOutputStream(fileJSON);
                fos.write(encodeBytes);
                fos.close();
                enc = encodeBytes;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
            return false;
        }

        tv_encrypt_text.setText(Base64.encodeToString(encodeBytes, Base64.DEFAULT));
        return true;
    }

    private void writeJSON(String text) {

        JSONObject object = new JSONObject();
        object.put("cipher_text", text);

        try (FileWriter writer = new FileWriter(fileJSON)) {
            writer.write(object.toJSONString());
            writer.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Невозможно записать", Toast.LENGTH_SHORT).show();
        }
    }

}
