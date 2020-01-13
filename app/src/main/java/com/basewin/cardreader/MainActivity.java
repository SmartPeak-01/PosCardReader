package com.basewin.cardreader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pos.sdk.cardreader.PosCardReaderInfo;
import com.pos.sdk.cardreader.PosCardReaderManager;
import com.pos.sdk.cardreader.PosFelicaCardReader;
import com.pos.sdk.cardreader.PosIccCardReader;
import com.pos.sdk.cardreader.PosMagCardReader;
import com.pos.sdk.cardreader.PosMemoryCardReader;
import com.pos.sdk.cardreader.PosMifareCardReader;
import com.pos.sdk.cardreader.PosPiccCardReader;
import com.pos.sdk.cardreader.PosPsamCardReader;
import com.pos.sdk.cardreader.PosSidCardReader;
import com.pos.sdk.cardreader.PosViccCardReader;
import com.pos.sdk.utils.PosByteArray;
import com.pos.sdk.utils.PosUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PosCardReader";

    private static final int MSG_SET_TEXT = 1;
    private static final int MSG_APPEND_TEXT = 2;
    private static final int MSG_SET_ENABLE_BTN = 3;

    private static final int MAX_TRY_CNT = 100;

    private TextView mTextViewStatus;
    private Button mBtnTestPsam;
    private Button mBtnTestIcc;
    private Button mBtnTestMemory;
    private Button mBtnTestPicc;
    private Button mBtnTestMifare;
    private Button mBtnTestSid;
    private Button mBtnTestVicc;
    private Button mBtnTestMag;
    private Button mBtnTestFelica;
    private Button mBtnTestMixed;
    private boolean mStopFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        mTextViewStatus = (TextView) findViewById(R.id.text_status);
        mBtnTestPsam = (Button) findViewById(R.id.button_test_psam);
        mBtnTestPsam.setOnClickListener(mViewOnClickListener);

        mBtnTestIcc = (Button) findViewById(R.id.button_test_icc);
        mBtnTestIcc.setOnClickListener(mViewOnClickListener);

        mBtnTestMemory = (Button) findViewById(R.id.button_test_memory);
        mBtnTestMemory.setOnClickListener(mViewOnClickListener);

        mBtnTestPicc = (Button) findViewById(R.id.button_test_picc);
        mBtnTestPicc.setOnClickListener(mViewOnClickListener);

        mBtnTestMifare = (Button) findViewById(R.id.button_test_mifare);
        mBtnTestMifare.setOnClickListener(mViewOnClickListener);

        mBtnTestSid = (Button) findViewById(R.id.button_test_sid);
        mBtnTestSid.setOnClickListener(mViewOnClickListener);

        mBtnTestVicc = (Button) findViewById(R.id.button_test_vicc);
        mBtnTestVicc.setOnClickListener(mViewOnClickListener);

        mBtnTestMag = (Button) findViewById(R.id.button_test_mag);
        mBtnTestMag.setOnClickListener(mViewOnClickListener);

        mBtnTestFelica = (Button) findViewById(R.id.button_test_felica);
        mBtnTestFelica.setOnClickListener(mViewOnClickListener);

        mBtnTestMixed = (Button) findViewById(R.id.button_test_mixed);
        mBtnTestMixed.setOnClickListener(mViewOnClickListener);
    }

    private View.OnClickListener mViewOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            final int resId = view.getId();
            Thread testThread = new Thread(new Runnable() {
                public void run() {
                    setEnableBtn(resId, false);
                    switch (resId) {
                        case R.id.button_test_psam:
                            testPsam();
                            break;
                        case R.id.button_test_icc:
                            testIcc();
                            break;
                        case R.id.button_test_memory:
                            testMemory();
                            break;
                        case R.id.button_test_picc:
                            testPicc();
                            break;
                        case R.id.button_test_mifare:
                            testMifare();
                            break;
                        case R.id.button_test_sid:
                            testSid();
                            break;
                        case R.id.button_test_vicc:
                            testVicc();
                            break;
                        case R.id.button_test_mag:
                            testMag();
                            break;
                        case R.id.button_test_felica:
                            testFelica();
                            break;
                        case R.id.button_test_mixed:
                            testMixed();
                            break;
                        default :
                            break;
                    }
                    setEnableBtn(resId, true);
                }
            });
            testThread.start();
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_TEXT:
                    mTextViewStatus.setText((String)msg.obj);
                    break;
                case MSG_APPEND_TEXT:
                    mTextViewStatus.append((String)msg.obj);
                    break;
                case MSG_SET_ENABLE_BTN: {
                    Button btn = (Button)msg.obj;
                    if (btn != null) {
                        btn.setEnabled(msg.arg1 == 1);
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        mStopFlag = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        mStopFlag = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void testPsam() {
        PosPsamCardReader cardReader = PosCardReaderManager.getDefault(this).getPsamCardReader();
        if (cardReader != null) {
            setText("****** PSAM test******\n");
            int ret = cardReader.open();
            appendText("open:: " + (ret == 0 ? "ok" : "fail"));
            if (ret == 0) {
                appendText("start to detect");
                int cnt = 0;
                boolean detected = false;
                while(!mStopFlag && cnt++ < MAX_TRY_CNT) {
                    if (cardReader.detect() == 0) {
                        detected = true;
                        break;
                    }
                    PosUtils.delayms(50);
                }

                appendText("detect:: " + (detected ? "ok" : "fail"));
                if (detected) {
                    ret = cardReader.reset();
                    appendText("reset:: " + (ret == 0 ? "ok" : "fail"));
                    if (ret == 0) {
                        PosCardReaderInfo info = cardReader.getCardReaderInfo();
                        appendText("reset:: " + (info != null ? info.toString() : "null"));

                        PosByteArray rspBuf = new PosByteArray();
                        PosByteArray swBuf = new PosByteArray();
                        ret = cardReader.transmitApdu(PosUtils.hexStringToBytes("0084000008"), rspBuf, swBuf);
                        appendText("transmitApdu:: " + (ret == 0 ? "ok, radom="
                                + PosUtils.bytesToHexString(rspBuf.len > 0 ? rspBuf.buffer : swBuf.buffer): "fail"));
                    }
                }

                ret = cardReader.close();
                appendText("close:: " + (ret == 0 ? "ok" : "fail"));
            }
        } else {
            setText("PSAM cardreader is not support!");
        }

    }

    private void testIcc() {
        PosIccCardReader cardReader = PosCardReaderManager.getDefault(this).getIccCardReader();
        if (cardReader != null) {
            setText("****** ICC test******\n");
            int ret = cardReader.open();
            appendText("open:: " + (ret == 0 ? "ok" : "fail"));
            if (ret == 0) {
                appendText("start to detect");
                int cnt = 0;
                boolean detected = false;
                while(!mStopFlag && cnt++ < MAX_TRY_CNT) {
                    if (cardReader.detect() == 0) {
                        detected = true;
                        break;
                    }
                    PosUtils.delayms(50);
                }

                appendText("detect:: " + (detected ? "ok" : "fail"));
                if (detected) {
                    ret = cardReader.reset();
                    appendText("reset:: " + (ret == 0 ? "ok" : "fail"));
                    if (ret == 0) {
                        PosCardReaderInfo info = cardReader.getCardReaderInfo();
                        appendText("reset:: " + (info != null ? info.toString() : "null"));

                        PosByteArray rspBuf = new PosByteArray();
                        PosByteArray swBuf = new PosByteArray();
                        ret = cardReader.transmitApdu(PosUtils.hexStringToBytes("0084000008"), rspBuf, swBuf);
                        appendText("transmitApdu:: " + (ret == 0 ? "ok, radom="
                                + PosUtils.bytesToHexString(rspBuf.len > 0 ? rspBuf.buffer : swBuf.buffer): "fail"));
                    }
                }

                ret = cardReader.close();
                appendText("close:: " + (ret == 0 ? "ok" : "fail"));
            }
        } else {
            setText("ICC cardreader is not support!");
        }

    }

    private void testMemory() {
        PosMemoryCardReader cardReader = PosCardReaderManager.getDefault(this).getMemoryCardReader();
        if (cardReader != null) {
            int ret = -1;
            setText("****** Memory test******\n");
            ret = cardReader.status();
            appendText("status:: " + (ret == 0 ? "ok" : "fail"));
            if (ret == 0) {
                PosByteArray rspBuf = new PosByteArray();
                ret = cardReader.open(PosMemoryCardReader.MEMORY_CARD_TYPE_AT24C02, rspBuf);
                appendText("open:: " + (ret == 0 ? "ok, rspBuf= " + PosUtils.bytesToHexString(rspBuf.buffer) : "fail"));
                if (ret == 0) {
                    //pac.
                    rspBuf = new PosByteArray();
                    ret = cardReader.pac(rspBuf);
                    appendText("pac:: " + (ret == 0 ? "ok, rspBuf= " + PosUtils.bytesToHexString(rspBuf.buffer) : "fail"));
                    //verify
                    ret = cardReader.verify(PosUtils.hexStringToBytes("FFFFFF"));
                    appendText("verify:: " + (ret == 0 ? "ok" : "fail"));
                    if (ret == 0) {
                        //read
                        rspBuf = new PosByteArray();
                        ret = cardReader.read(0x20, 0x10, rspBuf);
                        appendText("read:: " + (ret == 0 ? "ok, rspBuf= " + PosUtils.bytesToHexString(rspBuf.buffer) : "fail"));
                        //write
                        ret = cardReader.write(0x20, rspBuf.buffer);
                        appendText("write:: " + (ret == 0 ? "ok" : "fail"));

                        //Update.
                        ret = cardReader.update(PosUtils.hexStringToBytes("FFFFFF"));
                        appendText("update:: " + (ret == 0 ? "ok" : "fail"));
                    }
                }
            }
            ret = cardReader.close();
            appendText("close:: " + (ret == 0 ? "ok" : "fail"));
        } else {
            setText("Memory cardreader is not support!");
        }
    }

    private void testPicc() {
        PosPiccCardReader cardReader = PosCardReaderManager.getDefault(this).getPiccCardReader();
        if (cardReader != null) {
            setText("****** PICC test******\n");
            int ret = cardReader.open();
            appendText("open:: " + (ret == 0 ? "ok" : "fail"));
            if (ret == 0) {
                appendText("start to detect");
                int cnt = 0;
                boolean detected = false;
                while(!mStopFlag && cnt++ < MAX_TRY_CNT) {
                    if (cardReader.detect() == 0) {
                        detected = true;
                        break;
                    }
                    PosUtils.delayms(50);
                }

                appendText("detect:: " + (detected ? "ok" : "fail"));
                if (detected) {
                    handlePiccCmd(cardReader);
                }
                ret = cardReader.close();
                appendText("close:: " + (ret == 0 ? "ok" : "fail"));
            }
        } else {
            setText("PICC cardreader is not support!");
        }
    }

    private void testMifare() {
        PosMifareCardReader cardReader = PosCardReaderManager.getDefault(this).getMifareCardReader();
        if (cardReader != null) {
            setText("****** Mifare test******\n");
            int ret = cardReader.open();
            appendText("open:: " + (ret == 0 ? "ok" : "fail"));
            if (ret == 0) {
                appendText("start to detect");
                int cnt = 0;
                boolean detected = false;
                PosCardReaderInfo cardReaderInfo = null;

                while(!mStopFlag && cnt++ < MAX_TRY_CNT) {
                    //CARD_TYPE_MIFARE_ULTRALIGHT
                    cardReader.setCardType(PosMifareCardReader.CARD_TYPE_MIFARE_ULTRALIGHT);
                    if (cardReader.detect() == 0) {
                        cardReaderInfo = cardReader.getCardReaderInfo();
                        if (cardReaderInfo != null && cardReaderInfo.mCardType == PosMifareCardReader.CARD_TYPE_MIFARE_ULTRALIGHT) {
                            Log.d(TAG, "find mifare ULTRALIGHT **********************");
                            detected = true;
                            break;
                        }
                    }

                    //CARD_TYPE_MIFARE_CLASSIC
                    cardReader.setCardType(PosMifareCardReader.CARD_TYPE_MIFARE_CLASSIC);
                    if (cardReader.detect() == 0) {
                        cardReaderInfo = cardReader.getCardReaderInfo();
                        if (cardReaderInfo != null && cardReaderInfo.mCardType == PosMifareCardReader.CARD_TYPE_MIFARE_CLASSIC) {
                            Log.d(TAG, "find mifare classic **********************");
                            detected = true;
                            break;
                        }
                    }

                    //CARD_TYPE_MIFARE_PLUS
                    cardReader.setCardType(PosMifareCardReader.CARD_TYPE_MIFARE_PLUS);
                    if (cardReader.detect() == 0) {
                        cardReaderInfo = cardReader.getCardReaderInfo();
                        if (cardReaderInfo != null && cardReaderInfo.mCardType == PosMifareCardReader.CARD_TYPE_MIFARE_PLUS) {
                            Log.d(TAG, "find mifare plus **********************");
                            detected = true;
                            break;
                        }
                    }

                    //CARD_TYPE_MIFARE_DESFIRE
                    cardReader.setCardType(PosMifareCardReader.CARD_TYPE_MIFARE_DESFIRE);
                    if (cardReader.detect() == 0) {
                        cardReaderInfo = cardReader.getCardReaderInfo();
                        if (cardReaderInfo != null && cardReaderInfo.mCardType == PosMifareCardReader.CARD_TYPE_MIFARE_DESFIRE) {
                            Log.d(TAG, "find mifare desfire **********************");
                            detected = true;
                            break;
                        }
                    }
                    PosUtils.delayms(50);
                }

                appendText("detect:: " + (detected ? "ok" : "fail"));
                if (detected) {
                    handleMifareCmd(cardReader);
                }
                ret = cardReader.close();
                appendText("close:: " + (ret == 0 ? "ok" : "fail"));
            }
        } else {
            setText("Mifare cardreader is not support!");
        }
    }

    private void testSid() {
        PosSidCardReader cardReader = PosCardReaderManager.getDefault(this).getSidCardReader();
        if (cardReader != null) {
            setText("****** SID test******\n");
            int ret = cardReader.open();
            appendText("open:: " + (ret == 0 ? "ok" : "fail"));
            if (ret == 0) {
                appendText("start to detect");
                int cnt = 0;
                boolean detected = false;
                while(!mStopFlag && cnt++ < MAX_TRY_CNT) {
                    if (cardReader.detect() == 0) {
                        detected = true;
                        break;
                    }
                    PosUtils.delayms(50);
                }

                appendText("detect:: " + (detected ? "ok" : "fail"));
                if (detected) {
                    handleSidCmd(cardReader);
                }
                ret = cardReader.close();
                appendText("close:: " + (ret == 0 ? "ok" : "fail"));
            }
        } else {
            setText("SID cardreader is not support!");
        }
    }

    private void testVicc() {
    	/*
    	PosAccessoryManager.getDefault().setRFRegister(PosAccessoryManager.RF_REGISTER_TYPE_A,
	    		PosAccessoryManager.RF_REGISTER_ADDR, 0x00);
    	PosAccessoryManager.getDefault().setRFRegister(PosAccessoryManager.RF_REGISTER_TYPE_B,
    		PosAccessoryManager.RF_REGISTER_ADDR, 0x00);
    	*/
        PosViccCardReader cardReader = PosCardReaderManager.getDefault(this).getViccCardReader();
        if (cardReader != null) {
            setText("****** Vicc test******\n");
            int ret = -1;
            PosByteArray rspBuf = new PosByteArray();

            ret = cardReader.open();
            appendText("open:: " + (ret == 0 ? "ok" : "fail"));
            if (ret == 0) {
                appendText("start to detect");
                int cnt = 0;
                boolean detected = false;
                while(!mStopFlag && cnt++ < MAX_TRY_CNT) {
                    if (cardReader.inventory(rspBuf) == 0) {
                        detected = true;
                        break;
                    }
                    PosUtils.delayms(50);
                }

                appendText("detect:: " + (detected ? "ok, rspBuf= " + PosUtils.bytesToHexString(rspBuf.buffer) : "fail"));
                if (detected) {
                    int offset = 0;
                    //flag.
                    int flag = (int)(rspBuf.buffer[offset++] & 0xff);
                    //dsfid
                    int dfsid = (int)(rspBuf.buffer[offset++] & 0xff);
                    //uidBytes.
                    byte[] uidBytes = Arrays.copyOfRange(rspBuf.buffer, offset, rspBuf.len);
                    //select.
                    ret = cardReader.select(uidBytes);
                    appendText("select:: " + (ret == 0 ? "ok" : "fail"));

                    //reset.
                    ret = cardReader.reset(uidBytes);
                    appendText("reset:: " + (ret == 0 ? "ok" : "fail"));

                    //readBlock.
                    rspBuf = new PosByteArray();
                    ret = cardReader.readBlock(0x08, rspBuf);
                    appendText("readBlock:: " + (ret == 0 ? ("ok, flag= 0x" + Integer.toHexString(rspBuf.buffer[0])
                            + ", data= " + PosUtils.bytesToHexString(rspBuf.buffer, 1, rspBuf.len - 1)) : "fail"));

                    //writeBlock.
                    ret = cardReader.writeBlock(0x08, Arrays.copyOfRange(rspBuf.buffer, 1, rspBuf.len));
                    appendText("writeBlock:: " + (ret == 0 ? "ok" : "fail"));

                    //getSystemInfo.
                    rspBuf = new PosByteArray();
                    ret = cardReader.getSystemInfo(rspBuf);
                    appendText("getSystemInfo:: " + (ret == 0 ? "ok, rspBuf= " + PosUtils.bytesToHexString(rspBuf.buffer) : "fail"));
                }
            }

            ret = cardReader.close();
            appendText("close:: " + (ret == 0 ? "ok" : "fail"));
        } else {
            setText("Vicc cardreader is not support!");
        }
    }

    private void testMag() {
        PosMagCardReader cardReader = PosCardReaderManager.getDefault(this).getMagCardReader();
        if (cardReader != null) {
            setText("****** Mag test******\n");
            int ret = cardReader.open();
            appendText("open:: " + (ret == 0 ? "ok" : "fail"));
            if (ret == 0) {
                appendText("start to detect");
                int cnt = 0;
                boolean detected = false;
                while(!mStopFlag && cnt++ < MAX_TRY_CNT) {
                    if (cardReader.detect() == 0) {
                        detected = true;
                        break;
                    }
                    PosUtils.delayms(50);
                }

                appendText("detect:: " + (detected ? "ok" : "fail"));
                if (detected) {
                    byte[] stripDataBytes = null;
                    for (int i = PosMagCardReader.CARDREADER_TRACE_INDEX_1; i <= PosMagCardReader.CARDREADER_TRACE_INDEX_3; i++) {
                        stripDataBytes = cardReader.getTraceData(i);
                        if (stripDataBytes != null) {
                            appendText("getTraceData:: strip" + i + "'s data= "+ new String(stripDataBytes));
                        }
                    }
                }

                ret = cardReader.close();
                appendText("close:: " + (ret == 0 ? "ok" : "fail"));
            }
        } else {
            setText("Mag cardreader is not support!");
        }
    }

    private void testFelica() {
        PosFelicaCardReader cardReader = PosCardReaderManager.getDefault(this).getFelicaCardReader();
        if (cardReader != null) {
            setText("****** felica test******\n");
            int ret = cardReader.open();
            appendText("open:: " + (ret == 0 ? "ok" : "fail"));
            if (ret == 0) {
                appendText("start to detect");
                int cnt = 0;
                boolean detected = false;
                while(!mStopFlag && cnt++ < MAX_TRY_CNT) {
                    if (cardReader.detect(new byte[] {(byte)0xff, (byte)0xff}, 0x01, 0) == 0) {
                        detected = true;
                        break;
                    }
                    PosUtils.delayms(50);
                }

                appendText("detect:: " + (detected ? "ok" : "fail"));
                if (detected) {
                    handleFelicaCmd(cardReader);
                }

                ret = cardReader.close();
                appendText("close:: " + (ret == 0 ? "ok" : "fail"));
            }
        } else {
            setText("Mag cardreader is not support!");
        }
    }

    private void testMixed() {
        int ret = -1;
        PosPiccCardReader piccCardReader = PosCardReaderManager.getDefault(this).getPiccCardReader();
        PosSidCardReader sidCardReader = PosCardReaderManager.getDefault(this).getSidCardReader();
        PosMifareCardReader mifareCardReader = PosCardReaderManager.getDefault(this).getMifareCardReader();
        PosFelicaCardReader felicaCardReader = PosCardReaderManager.getDefault(this).getFelicaCardReader();

        if (piccCardReader != null && sidCardReader != null && mifareCardReader != null && felicaCardReader != null) {
            setText("****** Multi-type cards test******\n");
            ret = piccCardReader.open();
            appendText("open picc " + (ret == 0 ? "ok" : "fail"));

            ret = sidCardReader.open();
            appendText("open sid " + (ret == 0 ? "ok" : "fail"));

            ret = mifareCardReader.open();
            appendText("open mifare " + (ret == 0 ? "ok" : "fail"));

            ret = felicaCardReader.open();
            appendText("open felica " + (ret == 0 ? "ok" : "fail"));

            if (ret == 0) {
                appendText("start to detect");
                int cnt = 0;
                boolean detected = false;
                int cardType = PosCardReaderManager.CARDREADER_TYPE_UNKNOW;

                while(!mStopFlag && cnt++ < MAX_TRY_CNT) {
                    if (piccCardReader.detect() == 0) {
                        detected = true;
                        cardType = PosCardReaderManager.CARDREADER_TYPE_PICC;
                        appendText("detect picc " + (detected ? "ok" : "fail"));
                        break;
                    }

                    if (sidCardReader.detect() == 0) {
                        detected = true;
                        cardType = PosCardReaderManager.CARDREADER_TYPE_SID;
                        appendText("detect sid " + (detected ? "ok" : "fail"));
                        break;
                    }

                    if (mifareCardReader.detect() == 0) {
                        detected = true;
                        cardType = PosCardReaderManager.CARDREADER_TYPE_MIFARE;
                        appendText("detect mifare " + (detected ? "ok" : "fail"));
                        break;
                    }

                    if (felicaCardReader.detect(new byte[] {(byte)0xff, (byte)0xff}, 0x01, 0) == 0) {
                        detected = true;
                        cardType = PosCardReaderManager.CARDREADER_TYPE_FELICA;
                        appendText("detect felica " + (detected ? "ok" : "fail"));
                        break;
                    }

                    PosUtils.delayms(50);
                }

                if (detected) {
                    switch (cardType) {
                        case PosCardReaderManager.CARDREADER_TYPE_PICC:
                            handlePiccCmd(piccCardReader);
                            break;
                        case PosCardReaderManager.CARDREADER_TYPE_SID:
                            handleSidCmd(sidCardReader);
                            break;
                        case PosCardReaderManager.CARDREADER_TYPE_MIFARE:
                            handleMifareCmd(mifareCardReader);
                            break;
                        case PosCardReaderManager.CARDREADER_TYPE_FELICA:
                            handleFelicaCmd(felicaCardReader);
                            break;
                        default:
                            break;
                    }
                }

                ret = piccCardReader.close();
                appendText("close picc " + (ret == 0 ? "ok" : "fail"));

                ret = sidCardReader.close();
                appendText("close sid " + (ret == 0 ? "ok" : "fail"));

                ret = mifareCardReader.close();
                appendText("close mifare " + (ret == 0 ? "ok" : "fail"));

                ret = felicaCardReader.close();
                appendText("close felica " + (ret == 0 ? "ok" : "fail"));
            }
        } else {
            setText("some cardreader is not support!");
        }
    }

    private void setText(String text) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TEXT, text));
    }

    private void appendText(String text) {
        Log.d(TAG, "appendText:: text= " + text);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_APPEND_TEXT, (text + "\n")));
    }

    private void setEnableBtn(int resId, boolean enabled) {
        Button btn = (Button) findViewById(resId);
        if (btn != null) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ENABLE_BTN, enabled ? 1 : 0, 0, btn));
        }
    }

    private void handlePiccCmd(PosPiccCardReader cardReader) {
        int ret = -1;
        PosCardReaderInfo info = cardReader.getCardReaderInfo();
        appendText("getCardReaderInfo:: " + (info != null ? info.toString() : "null"));

        PosByteArray rspBuf = new PosByteArray();
        PosByteArray swBuf = new PosByteArray();
        ret = cardReader.transmitApdu(PosUtils.hexStringToBytes("0084000008"), rspBuf, swBuf);
        appendText("transmitApdu:: " + (ret == 0 ? "ok, radom="
                + PosUtils.bytesToHexString(rspBuf.len > 0 ? rspBuf.buffer : swBuf.buffer): "fail"));

        ret = cardReader.removeCard();
        appendText("removeCard:: " + (ret == 0 ? "ok" : "fail"));
    }

    private void handleMifareCmd(PosMifareCardReader cardReader) {
        int ret = -1;
        PosCardReaderInfo info = cardReader.getCardReaderInfo();
        appendText("getCardReaderInfo:: " + (info != null ? info.toString() : "null"));

        switch (info.mCardType) {
            case PosMifareCardReader.CARD_TYPE_MIFARE_ULTRALIGHT: {
                //Auth.
                ret = cardReader.auth('T', 0, PosUtils.hexStringToBytes("49454D4B41455242214E4143554F5946"), null);
                appendText("auth:: " + (ret == 0 ? "ok" : "fail"));
                if (ret == 0) {
                    PosByteArray rspBuf = new PosByteArray();
                    //Read
                    ret = cardReader.read(0x08, rspBuf);
                    appendText("readBlock:: " + (ret == 0 ? "ok, rspBuf= " + PosUtils.bytesToHexString(rspBuf.buffer): "fail"));
                    //Write
                    if (rspBuf.buffer != null) {
                        ret = cardReader.write(0x08, PosUtils.hexStringToBytes("FFFFFFFF"));
                        appendText("writeBlock:: " + (ret == 0 ? "ok": "fail"));
                    }
                    //Operate.
                }
                break;
            }
            case PosMifareCardReader.CARD_TYPE_MIFARE_CLASSIC:
            case PosMifareCardReader.CARD_TYPE_MIFARE_PLUS:
                int blcNo = 0x03;
                //Auth.
                ret = cardReader.auth('B', blcNo, PosUtils.hexStringToBytes("FFFFFFFFFFFF"), null);
                appendText("auth:: " + (ret == 0 ? "ok" : "fail"));

                if (ret == 0) {
                    PosByteArray rspBuf = new PosByteArray();
                    //Read
                    ret = cardReader.read(blcNo, rspBuf);
                    appendText("read:: " + (ret == 0 ? "ok, rspBuf= " + PosUtils.bytesToHexString(rspBuf.buffer): "fail"));
                    //Write
                    if (rspBuf.buffer != null) {
                        ret = cardReader.write(blcNo, PosUtils.hexStringToBytes("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"));
                        appendText("write 1111:: " + (ret == 0 ? "ok": "fail"));
                    }
                    //Operate.
                    {
                        //init value.
                        byte[] inBytes = new byte[16];
                        byte[] tmpBytes = PosUtils.intToBytesBe(0x100);
                        byte[] revTmpBytes = PosUtils.intToBytesBe(0x100^0xFFFFFFFF);
                        System.arraycopy(tmpBytes, 0, inBytes, 0, 4);
                        System.arraycopy(revTmpBytes, 0, inBytes, 4, 4);
                        System.arraycopy(tmpBytes, 0, inBytes, 8, 4);
                        inBytes[12] = 0x04;
                        inBytes[13] = (byte)((0x04 ^ 0xFF) &0xff);
                        inBytes[14] = 0x04;
                        inBytes[15] = (byte)((0x04 ^ 0xFF) &0xff);

                        ret = cardReader.write(blcNo, inBytes);
                        appendText("init money ret:: " + (ret == 0 ? "ok": "fail"));
                        if (ret == 0) {
                            //Read
                            ret = cardReader.read(blcNo, rspBuf);
                            appendText("read money:: " + (ret == 0 ? "ok, rspBuf= " + PosUtils.bytesToHexString(rspBuf.buffer): "fail"));

                            ret = cardReader.operate(PosMifareCardReader.OPERATE_BLK_TYPE_ADD,  blcNo, blcNo, PosUtils.intToBytesBe(0x02));
                            appendText("operate:: " + (ret == 0 ? "ok": "fail"));

                            ret = cardReader.read(blcNo, rspBuf);
                            appendText("read money2:: " + (ret == 0 ? "ok, rspBuf= " + PosUtils.bytesToHexString(rspBuf.buffer): "fail"));
                        }
                    }
                }
                break;
            case PosMifareCardReader.CARD_TYPE_MIFARE_DESFIRE: {
                //get version.
                PosByteArray rspBuf = new PosByteArray();
                ret = cardReader.transmitApdu(new byte[] {0x60}, rspBuf);
                appendText("transmitApdu:: getversion ret= " + (ret == 0 ? "ok, rspBuf= " + PosUtils.bytesToHexString(rspBuf.buffer) : "fail"));
                //Auth.
                ret = cardReader.transmitApdu(PosUtils.hexStringToBytes("0A00000000000000000000000000000000"), rspBuf);
                appendText("transmitApdu:: auth ret= " + (ret == 0 ? "ok, rspBuf= " + PosUtils.bytesToHexString(rspBuf.buffer) : "fail"));
                break;
            }
            default:
                break;
        }

        ret = cardReader.removeCard();
        appendText("removeCard:: " + (ret == 0 ? "ok" : "fail"));
    }

    private void handleSidCmd(PosSidCardReader cardReader) {
        int ret = -1;
        PosCardReaderInfo info = cardReader.getCardReaderInfo();
        appendText("getCardReaderInfo:: " + (info != null ? info.toString() : "null"));

        PosByteArray rspBuf = new PosByteArray();
        ret = cardReader.transmitCmd(PosUtils.hexStringToBytes("0084000008"), rspBuf);
        appendText("transmitCmd:: " + (ret == 0 ? "ok, radom="
                + PosUtils.bytesToHexString(rspBuf.buffer): "fail"));

        ret = cardReader.removeCard();
        appendText("removeCard:: " + (ret == 0 ? "ok" : "fail"));
    }

    private void handleFelicaCmd(PosFelicaCardReader cardReader) {
        int ret = -1;
        PosCardReaderInfo info = cardReader.getCardReaderInfo();

        if (info != null) {
            int offset = 0;
            byte[] idmBytes = null;
            byte[] pmmBytes = null;
            byte[] reqDataBytes = null;
            PosByteArray rspBuf = null;

            idmBytes = Arrays.copyOfRange(info.mAttribute, offset, offset + PosFelicaCardReader.FELICA_ID_SIZE);
            offset += PosFelicaCardReader.FELICA_ID_SIZE;
            pmmBytes = Arrays.copyOfRange(info.mAttribute, offset, offset + PosFelicaCardReader.FELICA_PM_SIZE);
            offset += PosFelicaCardReader.FELICA_PM_SIZE;
            if (info.mAttribute.length - offset == PosFelicaCardReader.FELICA_REQ_DATA_SIZE) {
                reqDataBytes = Arrays.copyOfRange(info.mAttribute, offset, info.mAttribute.length);
            }

            appendText("getCardReaderInfo:: type=" + info.mCardType
                    + ", idmBytes= " + PosUtils.bytesToHexString(idmBytes)
                    + ", pmmBytes= " + PosUtils.bytesToHexString(pmmBytes)
                    + ", reqDataBytes= " + PosUtils.bytesToHexString(reqDataBytes));

            if (reqDataBytes != null) {
                //requstService.
                List<byte[]> nodeList = new ArrayList<byte[]>();
                nodeList.add(reqDataBytes);
                rspBuf = new PosByteArray();
                ret = cardReader.requestService(idmBytes, nodeList, rspBuf);
                if (ret == 0) {
                    //idmBytes + n + n*NodeKeyVersion.
                    int nkv = 0;
                    offset = 0;
                    byte[] rspIdmBytes = Arrays.copyOfRange(rspBuf.buffer,
                            offset, offset + PosFelicaCardReader.FELICA_ID_SIZE);
                    offset += PosFelicaCardReader.FELICA_ID_SIZE;
                    nkv = rspBuf.buffer[offset++] & 0xff;
                    appendText("requestService OK: rspIdmBytes=" + PosUtils.bytesToHexString(rspIdmBytes) + ", nkv= " + nkv);
                    for (int i = 0; i < nkv; i++) {
                        appendText("NodeKeyVersion: " + i + ", " + PosUtils.bytesToHexString(
                                rspBuf.buffer, offset, PosFelicaCardReader.FELICA_NODE_SIZE));
                        offset += PosFelicaCardReader.FELICA_NODE_SIZE;
                    }
                } else {
                    appendText("requestService failed!");
                }

                //readNoSecure.
                List<byte[]> srvsList = new ArrayList<byte[]>();
                List<byte[]> blkList = new ArrayList<byte[]>();
                srvsList.add(reqDataBytes);
                blkList.add(reqDataBytes);
                rspBuf = new PosByteArray();
                ret = cardReader.readNoSecure(idmBytes, srvsList, blkList, rspBuf);
                if (ret == 0) {
                    //IDm + statusFlag1 + statusFlag2 + n + n*BLOCKDATA
                    int statusFlag1 = 0;
                    int statusFlag2 = 0;
                    int nBlkData = 0;
                    offset = 0;
                    byte[] rspIdmBytes = Arrays.copyOfRange(rspBuf.buffer,
                            offset, offset + PosFelicaCardReader.FELICA_ID_SIZE);
                    offset += PosFelicaCardReader.FELICA_ID_SIZE;
                    statusFlag1 = (rspBuf.buffer[offset++] & 0xff);
                    statusFlag2 = (rspBuf.buffer[offset++] & 0xff);

                    appendText("readNoSecure OK: rspIdmBytes=" + PosUtils.bytesToHexString(rspIdmBytes)
                            + ", statusFlag1=0x" + Integer.toHexString(statusFlag1)
                            + ", statusFlag2= 0x" + Integer.toHexString(statusFlag2));
                    if (offset < rspBuf.len) {
                        nBlkData = (rspBuf.buffer[offset++] & 0xff);
                        for (int i = 0; i < nBlkData; i++) {
                            appendText("BlockData: (" + i + "/" + nBlkData + "), "
                                    + PosUtils.bytesToHexString(rspBuf.buffer, offset, PosFelicaCardReader.FELICA_BLOCK_SIZE));
                            offset += PosFelicaCardReader.FELICA_BLOCK_SIZE;
                        }
                    }
                } else {
                    appendText("readNoSecure failed!");
                }

                //writeNoSecure.
                List<byte[]> blkDataList = new ArrayList<byte[]>();
                blkDataList.add(new byte[PosFelicaCardReader.FELICA_BLOCK_SIZE]);
                rspBuf = new PosByteArray();
                ret = cardReader.writeNoSecure(idmBytes, srvsList, blkList, blkDataList, rspBuf);
                if (ret == 0) {
                    //IDm + statusFlag1 + statusFlag2
                    int statusFlag1 = 0;
                    int statusFlag2 = 0;
                    offset = 0;
                    byte[] rspIdmBytes = Arrays.copyOfRange(rspBuf.buffer,
                            offset, offset + PosFelicaCardReader.FELICA_ID_SIZE);
                    offset += PosFelicaCardReader.FELICA_ID_SIZE;
                    statusFlag1 = (rspBuf.buffer[offset++] & 0xff);
                    statusFlag2 = (rspBuf.buffer[offset++] & 0xff);

                    appendText("writeNoSecure OK: rspIdmBytes=" + PosUtils.bytesToHexString(rspIdmBytes)
                            + ", statusFlag1=0x" + Integer.toHexString(statusFlag1)
                            + ", statusFlag2= 0x" + Integer.toHexString(statusFlag2));
                } else {
                    appendText("writeNoSecure failed!");
                }
            }
        }
    }
}
