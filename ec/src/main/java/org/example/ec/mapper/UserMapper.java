package org.example.ec.mapper;

import org.apache.ibatis.annotations.*;
import org.example.ec.entity.User;

@Mapper
public interface UserMapper {
    @Select("select * from users where username=#{username}")
    User findByUsername(String username);

    @Insert("insert into Users(username, password, role, avatar) values (#{username}, #{password}, 'user', '/avatars/default_avatar.svg')")
    int InsertUser(String username, String password);

    @Select("select * from users where id=#{id}")
    User findById(Long id);

    @Update("update users set password=#{password} where id=#{id}")
    void updatePassword(Long id, String password);

    @Update("update users set email=#{email} where id=#{id}")
    void updateEmail(@Param("id") Long id, @Param("email") String email);

    @Update("UPDATE users SET bio=#{bio}, location=#{location} WHERE id=#{id}")
    void updateProfile(@Param("id") Long id, @Param("bio") String bio, @Param("location") String location);
}
