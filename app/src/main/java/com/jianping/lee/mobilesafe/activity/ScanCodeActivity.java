package com.jianping.lee.mobilesafe.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.utils.LogUtils;

import java.io.IOException;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;

public class ScanCodeActivity extends BaseActivity implements QRCodeView.Delegate {

    @InjectView(R.id.zv_scan_code_view)
    QRCodeView mQRCodeView;

    @InjectView(R.id.iv_scan_code_light)
    ImageView mLight;

    @InjectView(R.id.iv_scan_code_picture)
    ImageView mPicture;

    private boolean mPlayBeep = true;

    private MediaPlayer mediaPlayer;

    private static final float BEEP_VOLUME = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        initView();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_qrcode));
        mBack.setVisibility(View.VISIBLE);

        mQRCodeView.setDelegate(this);

        //声音设置
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL){
            mPlayBeep = false;
        }
        initSound();
    }

    private void initSound() {
        if (mPlayBeep && mediaPlayer == null){
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.qrcode_completed);

            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                mediaPlayer = null;
            }

        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.showScanRect();
        mQRCodeView.startCamera();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mQRCodeView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQRCodeView.onDestroy();
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.iv_scan_code_light)
    void onClickLight(View view){
        view.setSelected(!view.isSelected());
        if (view.isSelected()){
            mQRCodeView.openFlashlight();
        }else {
            mQRCodeView.closeFlashlight();
        }
    }

    @OnClick(R.id.iv_scan_code_picture)
    void onClickPicture(){
        //调用相册
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        showToast(result);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        mQRCodeView.stopSpot();

        if (mPlayBeep && mediaPlayer != null){
            mediaPlayer.start();
        }
        jump2Result(result);
    }

    private void jump2Result(String result){
        Intent intent = new Intent(this, ScanResultActivity.class);
        intent.putExtra("result", result);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            final String imagePath = c.getString(columnIndex);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String string = QRCodeDecoder.syncDecodeQRCode(imagePath);
                    if (string != null){
                        jump2Result(string);
                    }else {
                        Looper.prepare();
                        showToast("未发现二维码");
                        Looper.loop();
                    }
                }
            }).start();
        }
    }
}
