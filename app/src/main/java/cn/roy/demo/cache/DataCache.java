package cn.roy.demo.cache;

import java.util.List;

import cn.roy.demo.model.ChatMoment;

/**
 * @Description
 * @Author kk20
 * @Date 2020/11/1
 * @Version V1.0.0
 */
public class DataCache {
    private static DataCache instance;

    private List<ChatMoment> chatMoments;

    private DataCache() {

    }

    public static DataCache getInstance() {
        if (instance == null) {
            synchronized (DataCache.class) {
                if (instance == null) {
                    instance = new DataCache();
                }
            }
        }
        return instance;
    }

    public void setChatMoments(List<ChatMoment> chatMoments) {
        this.chatMoments = chatMoments;
    }

    public List<ChatMoment> getAllChatMoments() {
        return chatMoments;
    }

    public List<ChatMoment> getChatMomentsByPage(int page) {
        int start = (page - 1) * 5;
        int end = Math.min((start + 5), chatMoments.size());
        return chatMoments.subList(start, end);
    }

    public boolean hasMore(int page) {
        return chatMoments.size() > page * 5;
    }
}
