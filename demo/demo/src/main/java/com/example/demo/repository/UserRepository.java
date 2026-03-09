package com.example.demo.repository;

import com.example.demo.domain.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRepository {

	int insertUser(User user);

	User findByUserId(@Param("userId") String userId);

	List<User> findAllUsers();

	boolean existsByUserId(@Param("userId") String userId);

	int updateUser(User user);

	int deleteUser(@Param("userId") String userId);
}
