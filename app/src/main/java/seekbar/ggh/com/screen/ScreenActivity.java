package seekbar.ggh.com.screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boegam.ewsrvinterface.EwRecvSrvInterface;
import com.bumptech.glide.Glide;

import seekbar.ggh.com.soundsrecord.R;

public class ScreenActivity extends AppCompatActivity implements View.OnClickListener,
        EwRecvSrvInterface.OnEwRecvOpListener {

    private final String TAG = "GUIDE";
    private ImageView mImageViewButton;

    private TextView mTvDevName;
    private TextView mTvSSID;
    private TextView mTvSKey;
    private TextView mTvPINCODE;

    private ImageView mIVRegStat_OK;
    private ImageView mIVRegStat_NO;
    private ImageView mImageViewBgImg;
    private Context mContext;
    private EwRecvSrvInterface mEwSrvInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().addFlags(flags);

        setContentView(R.layout.activity_screen);
        mContext = this;

        mEwSrvInterface = new EwRecvSrvInterface(mContext);
        mEwSrvInterface.setEwOpListener(this);
        mEwSrvInterface.start();
        initUI();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();

        if (mEwSrvInterface != null) {
            mEwSrvInterface.stop();
            mEwSrvInterface = null;
        }
    }

    private void initUI() {
        mImageViewButton = (ImageView) findViewById(R.id.bn_setting_id);
        mImageViewButton.setOnClickListener(this);

        mTvDevName = (TextView) findViewById(R.id.tv_device_name);
        mTvSSID = (TextView) findViewById(R.id.tv_wifi_ssid);
        mTvSKey = (TextView) findViewById(R.id.tvb_wifi_skey);
        mTvPINCODE = (TextView) findViewById(R.id.tvb_pin_code);
        mIVRegStat_OK = (ImageView) findViewById(R.id.iv_airplay_reg_ok);
        mIVRegStat_OK.setOnClickListener(this);
        mIVRegStat_NO = (ImageView) findViewById(R.id.iv_airplay_no_reg);
        mIVRegStat_NO.setOnClickListener(this);

        mImageViewBgImg = (ImageView) findViewById(R.id.iv_backgroudimg);

        Configuration mConfiguration = this.getResources().getConfiguration();
        int ori = mConfiguration.orientation;
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            loadBackgroudImage(this, ori);
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            loadBackgroudImage(this, ori);
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.bn_setting_id: {
                mEwSrvInterface.startSettingActivity(mContext);
            }
            break;
            case R.id.iv_airplay_no_reg: {
                String MsgHtmlStr = "<big><font color='#FF0F0F'>" + getResources().getString(R.string.register_stat_NO) + "</font></big>";
                Toast.makeText(this, Html.fromHtml(MsgHtmlStr), Toast.LENGTH_LONG).show();
            }
            break;
            case R.id.iv_airplay_reg_ok: {
                String MsgHtmlStr = "<big><font color='#B5E600'>" + getResources().getString(R.string.register_stat_OK) + "</font></big>";
                Toast.makeText(this, Html.fromHtml(MsgHtmlStr), Toast.LENGTH_LONG).show();
            }
            break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        loadBackgroudImage(this, newConfig.orientation);
    }

    private void loadBackgroudImage(Context context, int orientation) {

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Glide.with(context).load(R.drawable.home).skipMemoryCache(true).into(mImageViewBgImg);
        } else {
            Glide.with(context).load(R.drawable.home_portrait).skipMemoryCache(true).into(mImageViewBgImg);
        }
    }

    @Override
    public void OnRemoteServiceReady(boolean isConnected) {

        if (isConnected == true) {
            mEwSrvInterface.getApInfo();
            mEwSrvInterface.getPinCode();
            mEwSrvInterface.getDeviceName();
            mEwSrvInterface.getRegisterStat();

            //test only
            if (true) {
                runTests();
            }
        }
    }

    @Override
    public void OnDeviceNameInfo(String DeviceName) {
        Log.i(TAG, "OnDeviceNameInfo :DeviceName=" + DeviceName);
        Message msg = mHandler.obtainMessage(GOT_DEV_NAME, DeviceName);
        mHandler.sendMessage(msg);
    }

    @Override
    public void OnApInfo(String Ssid, String pwd) {
        Log.i(TAG, "OnApInfo:" + Ssid + " " + pwd);
        Message msg = mHandler.obtainMessage();
        msg.what = GOT_AP_INFO;
        Bundle data = new Bundle();
        data.putString("SSID", Ssid);
        data.putString("PWD", pwd);
        msg.setData(data);
        mHandler.sendMessage(msg);

    }

    @Override
    public void OnPinCodeInfo(boolean isEnable, String pinCode) {
        Log.i(TAG, "OnPinCodeInfo: isEnable=" + isEnable + " pinCode=" + pinCode);

        Message msg = mHandler.obtainMessage();
        msg.what = GOT_PIN_CODE;
        Bundle data = new Bundle();
        data.putString("PINCODE", pinCode);
        data.putBoolean("ENABLE", isEnable);
        msg.setData(data);
        mHandler.sendMessage(msg);
    }

    @Override
    public void OnRegisterStat(boolean isRegister) {
        Log.i(TAG, "OnRegisterStat: isRegister=" + isRegister);
        Message msg = mHandler.obtainMessage();
        msg.what = GOT_REG_STAT;
        Bundle data = new Bundle();
        data.putBoolean("REGISTER", isRegister);
        msg.setData(data);
        mHandler.sendMessage(msg);
    }

    private int GOT_AP_INFO = 0;
    private int GOT_DEV_NAME = 1;
    private int GOT_PIN_CODE = 2;
    private int GOT_REG_STAT = 3;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == GOT_AP_INFO) {
                Bundle data = msg.getData();
                String ssid = data.getString("SSID");
                String pwd = data.getString("PWD");

                if ("".equals(ssid) || "".equals(pwd)) {
                    mTvSSID.setText(getResources().getString(R.string.wifi_ssid) + " ");
                    mTvSKey.setText(getResources().getString(R.string.wifi_skey) + " ");
                } else {
                    mTvSSID.setText(getResources().getString(R.string.wifi_ssid) + " " + ssid);
                    mTvSKey.setText(getResources().getString(R.string.wifi_skey) + " " + pwd);
                }

            } else if (msg.what == GOT_DEV_NAME) {
                String device_name = (String) msg.obj;
                mTvDevName.setText(getResources().getString(R.string.device_name_text) + " " + device_name);
            } else if (msg.what == GOT_PIN_CODE) {
                Bundle data = msg.getData();
                boolean pwd = data.getBoolean("ENABLE");
                String pincode = data.getString("PINCODE");
                if (pincode == null) {
                    mTvPINCODE.setVisibility(View.INVISIBLE);
                } else {
                    mTvPINCODE.setVisibility(View.VISIBLE);
                    mTvPINCODE.setText(getResources().getString(R.string.pin_code_title) + " " + pincode);
                }

            } else if (msg.what == GOT_REG_STAT) {
                Bundle data = msg.getData();
                boolean isRegister = data.getBoolean("REGISTER");
                if (isRegister) {
                    mIVRegStat_OK.setVisibility(View.VISIBLE);
                    mIVRegStat_NO.setVisibility(View.GONE);
                } else {
                    mIVRegStat_OK.setVisibility(View.GONE);
                    mIVRegStat_NO.setVisibility(View.VISIBLE);
                }
            }
        }
    };


    private void runTests() {

        mEwSrvInterface.setDeviceName("RECV_ABC123");

        mEwSrvInterface.setPinCode(true, "7890");
        //mEwSrvInterface.setPinCode(false);
        //mEwSrvInterface.setPinCode(true);

        mEwSrvInterface.getVersionName();
        mEwSrvInterface.getLGVersion();

        String type = mEwSrvInterface.getPairingChannelType();
        Log.d(TAG, "getPairingChannelType:" + type);
        mEwSrvInterface.setPairingChannelType("2.4G");//5G or 2.4G

        mEwSrvInterface.getMultViewNum();
        mEwSrvInterface.setMultViewNum(4);

        mEwSrvInterface.getMacAddr();

        mEwSrvInterface.checkUpgrade();
    }

}
