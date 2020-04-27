package io.github.jlrods.mypsswrdsecure;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

class Cryptographer {
    //Attribute definition
    //Initial vector, generated by KeyStore when a key is first created.
    private IvParameterSpec iv;
    private static final String KEYSTORE_ALIAS= "MyPsswrdSecureKey";
    private static final String CIPHER_TYPE ="AES/CBC/PKCS7Padding";
    private static final String KEYSTORE_PROVIDER ="AndroidKeyStore";

    //Method definition

    //Constructor
    public Cryptographer() {
        Log.d("Crypto_Const","Enter Cryptographer Default constructor in Cryptographer class.");
        try{
            this.createSecurityKey();
            Log.d("Crypto_Const","Exit Cryptographer Default constructor in Cryptographer class.");
        }
        catch(InvalidAlgorithmParameterException e){
            Log.e("InvAlgParamExc",e.getMessage());
        }
        catch(NoSuchAlgorithmException e){
            Log.e("NoSuchAlgExc",e.getMessage());
        }
        catch(NoSuchProviderException e){
            Log.e("NoSuchProExc",e.getMessage());
        }
        catch(Exception e){
            String errorMessage = e.getMessage();
            Log.d("Crypto_Const","Exit Cryptographer Default constructor in Cryptographer class with error: "+ errorMessage);
        }
    }//End of Cryptographer constructor

    //Getter and Setter methods
    public IvParameterSpec getIv() {
        return iv;
    }

    public void setIv(IvParameterSpec iv) {
        this.iv = iv;
    }
    //Other

    //Method to create new Security key in KeyStore
    private void createSecurityKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        Log.d("Crypt_createKey","Enter createSecurityKey method in Cryptographer class.");
        KeyGenParameterSpec keySpec;
        KeyGenerator keyGen;
        keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(KEYSTORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);
            keySpec = builder.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setRandomizedEncryptionRequired(true)
                    .setUserAuthenticationRequired(true)
                    //.setUserAuthenticationValidityDurationSeconds(getKeyPinDuration())
                    .build();;
            keyGen.init(keySpec);
            keyGen.generateKey();
        }else{
            //FIXME: Investigate and implement how to manage keystore for older versions below version M
        }
        Log.d("Crypt_createKey","Exit createSecurityKey method in Cryptographer class.");
    }// End of createSecurityKey method


    //Method to encrypt a messaged passed in as plain text string and returned a byte array
    public byte[] encryptText(String plainText)  {
        Log.d("Crypt_encrypt","Enter encryptText method in Cryptographer class.");
        try{

            byte[] encryptedText;
            KeyStore keyStore =  KeyStore.getInstance(KEYSTORE_PROVIDER);
            keyStore.load(null);
            KeyStore.SecretKeyEntry keyEntry = null;
            if(!keyStore.containsAlias(KEYSTORE_ALIAS)){
                this.createSecurityKey();
            }else{
                keyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(KEYSTORE_ALIAS,null);
            }

            Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE,keyEntry.getSecretKey());
            encryptedText = cipher.doFinal(plainText.getBytes("UTF8"));
            this.iv = new IvParameterSpec(cipher.getIV());
            Log.d("Crypt_encrypt","Exit successfully encryptText method in Cryptographer class.");
            return encryptedText;
        }
        catch(Exception e){
            Log.d("Crypt_encrypt","Exit encryptText method in Cryptographer class with error: "+e.getMessage());
            return e.getMessage().getBytes();
        }//End of try catch block
    }//End of encryptText method


    //Method to decrypt a text passed as byte array and returned as readable String
    public String decryptText(byte[] encryptedText, IvParameterSpec initVector){
        Log.d("Crypt_decrypt","Enter decryptText method in Cryptographer class.");
        try{
            String decryptedText ="";
            KeyStore keyStore =  KeyStore.getInstance(KEYSTORE_PROVIDER);
            keyStore.load(null);
            KeyStore.SecretKeyEntry keyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(KEYSTORE_ALIAS,null);
            Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
            cipher.init(cipher.DECRYPT_MODE,keyEntry.getSecretKey(),initVector);
            decryptedText = new String(cipher.doFinal(encryptedText));
            Log.d("Crypt_decrypt","Exit successfully decryptText method in Cryptographer class.");
            return decryptedText;
        }catch (Exception e){
            Log.d("Crypt_decrypt","Exit decryptText method in Cryptographer class with error: "+e.getMessage());
            return e.getMessage();
        }//End of try catch block
    }//End of decryptText method

    //Function to create random initial vector when not using KeyStore to create the secret key
    private IvParameterSpec getInitVector(){
        Log.d("Crypt_iv","Enter getInitVector method in Cryptographer class.");
        byte[] initVector = new byte[16];
        SecureRandom ivRandom = new SecureRandom();
        ivRandom.nextBytes(initVector);
        IvParameterSpec ivParSpec = new IvParameterSpec(initVector);
        Log.d("Crypt_iv","Exit getInitVector method in Cryptographer class.");
        return ivParSpec;
    }

}// End of Cryptographer class