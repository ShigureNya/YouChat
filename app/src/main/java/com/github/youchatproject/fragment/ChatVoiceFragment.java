package com.github.youchatproject.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.youchatproject.R;
import com.github.youchatproject.bmob_im.MessageUtil;
import com.github.youchatproject.listener.OnFragmentResultListener;
import com.github.youchatproject.tools.VoiceUtil;
import com.github.youchatproject.view.BaseFragment;
import com.hyphenate.chat.EMMessage;

import butterknife.InjectView;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/11
 * 包名： com.github.youchatproject.fragment
 * 文档描述：发送短语音的Fragment
 */
public class ChatVoiceFragment extends BaseFragment {

    @InjectView(R.id.chat_voice_send_btn)
    ImageButton chatVoiceSendBtn;
    @InjectView(R.id.chat_voice_state_text)
    TextView chatVoiceStateText;
    @InjectView(R.id.chat_voice_duration_text)
    TextView chatVoiceDurationText;

    public OnFragmentResultListener resultListener;
    public String conversationId = null;
    private VoiceBroadcast broadcast = null ;
    @Override
    public int bindLayout() {
        return R.layout.fragment_chat_voice;
    }

    @Override
    public void initView(View view) {
        setVoiceBtnClick();
    }

    @Override
    public void doBusiness(Context mContext) {
        registerBroadcast();
    }

    @Override
    public void initParams(Bundle bundle) {
        conversationId = bundle.getString("ConversationId");
    }

    String voicePath = null;   //音频路径

    public void setVoiceBtnClick() {
        chatVoiceSendBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        chatVoiceDurationText.setVisibility(View.VISIBLE);
                        chatVoiceSendBtn.setBackgroundResource(R.drawable.chat_voice_btn_click_drawable);
                        chatVoiceStateText.setText(R.string.chat_voice_hold_up_text);
                        voicePath = VoiceUtil.getInstance().startRecord($context(),conversationId);    //开始录音 并返回文件名
                        break;
                    case MotionEvent.ACTION_UP:
                        chatVoiceDurationText.setVisibility(View.GONE);
                        chatVoiceSendBtn.setBackgroundResource(R.drawable.chat_voice_btn_not_click_drawable);
                        chatVoiceStateText.setText(R.string.chat_voice_hold_down_text);
                        int length = VoiceUtil.getInstance().stopRecord();   //停止录音
                        EMMessage voiceMessage = MessageUtil.getInstance().createVoiceMessage(conversationId, voicePath, length, false);
                        resultListener.resultMessage(voiceMessage, OnFragmentResultListener.TYPE_VOICE);
                        break;
                }
                return false;
            }
        });
    }

    //绑定监听器对象注册事件
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            resultListener = (OnFragmentResultListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getClass().getName()
                    + " must implements interface MyListener");
        }
    }

    public void registerBroadcast() {
        IntentFilter filter = new IntentFilter("VoiceDuration");
        broadcast = new VoiceBroadcast();
        $context().registerReceiver(broadcast, filter);
    }


    class VoiceBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int duration = intent.getIntExtra("Duration", 0);
            chatVoiceDurationText.setText(duration+"s");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        $context().unregisterReceiver(broadcast);
        broadcast = null ;
    }
}
