package org.example.ec.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.ec.entity.Messages;

import java.util.List;

@Mapper
public interface MessagesMapper {
    @Select("SELECT * FROM messages WHERE channel_id = #{channelId} ORDER BY create_time ASC")
    // 技巧：如果搞不定时间字段，先按 id 排序保证功能正常
    List<Messages> getByChannel(Integer channelId);

    @Insert("INSERT INTO messages(channel_id, author, content, color, create_time) " +
            "VALUES(#{channelId}, #{author}, #{content}, #{color}, NOW())")
    int insertMessage(Messages message);
}
