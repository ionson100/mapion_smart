package com.example.mapion;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.example.mapion.models.MContentFile;
import com.example.mapion.models.MMediaContent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Main2Activity  {

    private String encryptedFileName = "encrypted_Audio.mp3";
    private static String algorithm = "AES";
    static SecretKey yourKey = null;
    private Context mContext;
    TelephonyManager tManager;
    String uuid;


    public Main2Activity(Context context){

        mContext = context;

         uuid = Settings.Secure.getString(mContext.getContentResolver(),
                 Settings.Secure.ANDROID_ID);
    }

    public static SecretKey generateKey(char[] passphraseOrPin, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.
        final int iterations = 1000;

        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey;
    }

//    public static SecretKey generateKey() throws NoSuchAlgorithmException {
//        // Generate a 256-bit key
//        final int outputKeyLength = 256;
//        SecureRandom secureRandom = new SecureRandom();
//        // Do *not* seed secureRandom! Automatically seeded from system entropy.
//        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//        keyGenerator.init(outputKeyLength, secureRandom);
//        yourKey = keyGenerator.generateKey();
//        return yourKey;
//    }

    public static byte[] encodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] encrypted = null;
        byte[] data = yourKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        encrypted = cipher.doFinal(fileData);
        return encrypted;
    }

    public static byte[] decodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, yourKey, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        decrypted = cipher.doFinal(fileData);
        return decrypted;
    }

    void saveFile(byte[] stringToSave) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator, encryptedFileName);

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            yourKey = generateKey(uuid.toCharArray(),"ewe".getBytes());
            byte[] filesBytes = encodeFile(yourKey, stringToSave);
            bos.write(filesBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void decodeFile() {

        try {
            byte[] decodedData = decodeFile(yourKey, readFile());
            // String str = new String(decodedData);
            //System.out.println("DECODED FILE CONTENTS : " + str);
            playMp3(decodedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] readFile() {
        byte[] contents = null;

        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator, encryptedFileName);
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

    public byte[] getAudioFile() throws FileNotFoundException
    {
        byte[] audio_data = null;
        byte[] inarry = null;
        AssetManager am = mContext.getAssets();
        try {
            InputStream is = am.open("Sleep Away.mp3"); // use recorded file instead of getting file from assets folder.
            int length = is.available();
            audio_data = new byte[length];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = is.read(audio_data)) != -1) {
                output.write(audio_data, 0, bytesRead);
            }
            inarry = output.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return inarry;

    }

    public void SaveFileCore(String fileNameTemp,String fileNameCrypto,String idRoute,String idContent) throws Exception {

        String d=idRoute.replace('-','d');
        String  path=Environment.getExternalStoragePublicDirectory("mapion")+ File.separator+d;
        File f = new File(path);
        if(f.exists()&&f.isDirectory()) {

        }else {
            f.mkdirs();
        }
        String  p= Environment.getExternalStorageDirectory()+mContext.getApplicationInfo().dataDir+
        File.separator+"temp"+File.separator+fileNameTemp;
        File file1 = new File(p);
        int size = (int) file1.length();
        byte[] bytes = new byte[size];
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file1));
        buf.read(bytes, 0, bytes.length);
        buf.close();
        ///////////////////////////

        String path2=path+File.separator+fileNameCrypto;
        File file = new File(path2);


        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        SecretKey yourKey = generateKey(uuid.toCharArray(),idRoute.getBytes());
        byte[] filesBytes = encodeFile(yourKey, bytes);
        bos.write(filesBytes);
        bos.flush();
        bos.close();
        file1.delete();
        MContentFile.saveFile(idContent,fileNameCrypto);

    }

//    public String getMediaContent(MMediaContent mMediaContent) throws Exception {
//        String nameCrypto=MContentFile.getFileName(mMediaContent.idContent);
//        if(nameCrypto==null){
//            return mMediaContent.url;
//        }
//                SecretKey yourKey = generateKey(uuid.toCharArray(),mMediaContent.idRoute.getBytes());
//                byte[] decodedData = decodeFile(yourKey, readFile());
//                // String str = new String(decodedData);
//                //System.out.println("DECODED FILE CONTENTS : " + str);
//                playMp3(decodedData);
//    }

    private void playMp3(byte[] mp3SoundByteArray) {

        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("kurchina", "mp3", mContext.getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();
            // Tried reusing instance of media player
            // but that resulted in system crashes...
            MediaPlayer mediaPlayer = new MediaPlayer();
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            ex.printStackTrace();

        }

    }
}
