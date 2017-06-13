package com.github.youchatproject.bmob_im;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.youchatproject.listener.OnMessageListResultListener;
import com.github.youchatproject.tools.Loger;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;

import java.util.List;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/9
 * 包名： com.github.youchatproject.bmob_im
 * 文档描述：消息发送工具
 */
public class MessageUtil {
    private MessageUtil(){}
    public static MessageUtil instance = new MessageUtil();

    public static MessageUtil getInstance(){
        return instance;
    }

    /**
     * [创建一条文本消息]
     * @param conversationId 对方id
     * @param content 内容
     * @param isGroup 是否为群组消息
     */
    public EMMessage createTextMessage(String conversationId , String content ,boolean isGroup){
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, conversationId);
        //如果是群聊，设置chattype，默认是单聊
        if(isGroup){
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        return message ;
    }

    /**
     * [构建一条语音消息]
     * @param conversationId 对方ID
     * @param filePath 录音文件地址
     * @param length 录音长度
     * @param isGroup 是否为群发
     */
    public EMMessage createVoiceMessage(String conversationId ,String filePath , int length , boolean isGroup){
        //filePath为语音文件路径，length为录音时间(秒)
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, conversationId);
        //如果是群聊，设置chattype，默认是单聊
        if(isGroup){
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        EMClient.getInstance().chatManager().sendMessage(message);
        return message;
    }

    /**
     * [构建一条图片消息]
     * @param conversationId 对方ID
     * @param imagePath 图片地址
     * @param isHighPic 是否为高清图片
     * @param isGroup 是否为群聊
     */
    public void createPicMessage(String conversationId , String imagePath , boolean isHighPic , boolean isGroup){
        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
        EMMessage message = EMMessage.createImageSendMessage(imagePath, isHighPic, conversationId);
        //如果是群聊，设置chattype，默认是单聊
        if(isGroup){
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * [获取聊天记录列表]
     * @param conversationId 对方会话ID
     * @param listener 回调接口
     */
    public void loadMessageList(String conversationId,OnMessageListResultListener listener){
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(conversationId);
        List<EMMessage> messages = null;
        //获取此会话的所有消息
        try {
            messages = conversation.getAllMessages();
            listener.onSuccess(messages);
        }catch (Exception e){
            listener.onFailed(new Throwable("无聊天记录数据"));
        }
    }

    /**
     * [处理消息类型的方法]
     * @param msg 消息对象
     * @param messageImage 图片消息控件
     * @param messageText 文本消息控件
     */
    public void handleMessagesType(EMMessage msg, ImageView messageImage , TextView messageText){
        switch(msg.getType()){
            //图片消息
            case IMAGE:{
                messageImage.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.GONE);
                EMImageMessageBody imageBody = (EMImageMessageBody) msg.getBody();
                String url = imageBody.getThumbnailUrl();
                Loger.i("图像地址",url);
                break;
            }
            case TXT:{
                messageImage.setVisibility(View.GONE);
                messageText.setVisibility(View.VISIBLE);
                EMTextMessageBody txtBody = (EMTextMessageBody) msg.getBody();
                String content = txtBody.getMessage();
                messageText.setText(content);
                break;
            }
            case FILE:
                EMFileMessageBody fileBody = (EMFileMessageBody) msg.getBody();

                break;
            case VOICE:
                EMVoiceMessageBody voiceMessageBody = (EMVoiceMessageBody) msg.getBody();

                break;
            case VIDEO:
                EMVideoMessageBody videoMessageBody = (EMVideoMessageBody) msg.getBody();

                break;
        }
    }

    /**
     * [存储聊天记录到数据库]
     * @param msgs 聊天记录
     */
    public void saveToDatabase(List<EMMessage> msgs){
        EMClient.getInstance().chatManager().importMessages(msgs);
    }
}
