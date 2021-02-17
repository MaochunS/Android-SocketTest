package com.maochun.sockettest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    private EditText    mEditTextServerIP;
    private EditText    mEditTextPort;
    private EditText    mEditTextSend;
    private TextView    mTextViewLog;

    private Socket          mClientSocket = new Socket();
    private BufferedWriter  mSocketWriter = null;
    private BufferedReader  mSocketReader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextServerIP = findViewById(R.id.editTextServerIP);
        mEditTextPort = findViewById(R.id.editTextPort);
        mTextViewLog = findViewById(R.id.textViewLog);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (mSocketReader != null) {
                mSocketReader.close();
            }

            if (mSocketWriter != null) {
                mSocketWriter.close();
            }
            mClientSocket.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void onConnectButtonClick(View v){
        String serverIP = mEditTextServerIP.getText().toString();
        String port = mEditTextPort.getText().toString();

        try {
            int nPort = NumberFormat.getIntegerInstance().parse(port).intValue();

            if (serverIP.length() > 0 && nPort > 0){
                connectToServer(serverIP, nPort);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onSendButtonClick(View v){

        String msg = mEditTextSend.getText().toString();
        if (mSocketWriter != null && msg.length() > 0){
            try {
                mSocketWriter.write(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void connectToServer(String ip, int port){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress serverAddr = InetAddress.getByName(ip);

                    mClientSocket = new Socket(serverAddr, port);

                    mSocketWriter = new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream()));
                    mSocketReader = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));

                    while(mClientSocket.isConnected()){
                        String msg = mSocketReader.readLine();
                        System.out.println("recv:" + msg);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}