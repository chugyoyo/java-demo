package com.chugyoyo.db.mapper;

import org.apache.ibatis.annotations.Param;

public interface ChugUserMapper {

    // 支持方法重载
    // 在实际开发中，并不推荐在 Mapper 接口中使用重载，因为这样容易引起混淆，而且 MyBatis 在处理时也可能遇到问题。
    String getUserName(@Param("id") Long id);
    String getUserName(@Param("id") Long id, @Param("status") Integer status);
}
