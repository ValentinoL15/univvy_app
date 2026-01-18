package com.unnivy.unnivy_app.repository;

import com.unnivy.unnivy_app.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IChatRepository extends JpaRepository<ChatMessage,Long> {

    List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);

}
