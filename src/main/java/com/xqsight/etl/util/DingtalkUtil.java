package com.xqsight.etl.util;

import com.alibaba.fastjson.JSON;
import com.dingtalk.chatbot.DingtalkChatbotClient;
import com.dingtalk.chatbot.SendResult;
import com.dingtalk.chatbot.message.MarkdownMessage;
import com.dingtalk.chatbot.message.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DingtalkUtil {

    private static Logger logger = LoggerFactory.getLogger(DingtalkUtil.class);

    private static final String DINGTALK_URL = "https://oapi.dingtalk.com/robot/send?access_token=bc50f033233494db6ba595251b2e47d350becbc05bb1fd5a86826f7c75f32ad6";

    /**
     * 
     * @param text
     * @param atMobiles
     * @param isAtAll
     * @throws Exception
     */
    public static void textMessage(String text, List<String> atMobiles, boolean isAtAll) throws Exception {
        DingtalkChatbotClient client = new DingtalkChatbotClient();
        TextMessage message = new TextMessage(text);
        message.setAtMobiles(atMobiles);
        message.setIsAtAll(isAtAll);
        SendResult result = client.send(DINGTALK_URL, message);
        logger.info(JSON.toJSONString(result));
    }

    public static void reportMarkdownMessage(String title, String header, String... texts) throws Exception {
        DingtalkChatbotClient client = new DingtalkChatbotClient();
        MarkdownMessage message = new MarkdownMessage();
        message.setTitle(title);
        message.add(MarkdownMessage.getHeaderText(1, header));
        for (String text : texts) {
            message.add(MarkdownMessage.getReferenceText(text));
            message.add("\n\n");
        }
        SendResult result = client.send(DINGTALK_URL, message);
        logger.info(JSON.toJSONString(result));
    }
}
