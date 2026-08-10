package com.edugauge.repositiry;

import com.edugauge.domain.friendship.Friendship;
import com.edugauge.domain.friendship.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    boolean existsByRequester_IdAndReceiver_IdAndStatus(
            Long requesterId,
            Long receiverId,
            FriendshipStatus status
    );

    boolean existsByReceiver_IdAndRequester_IdAndStatus(
            Long receiverId,
            Long requesterId,
            FriendshipStatus status
    );

    Optional<Friendship> findByRequester_IdAndReceiver_IdAndStatus(
            Long requesterId,
            Long receiverId,
            FriendshipStatus status
    );

    List<Friendship> findByReceiver_IdAndStatus(
            Long receiverId,
            FriendshipStatus status
    );

    List<Friendship> findByRequester_IdAndStatusOrReceiver_IdAndStatus(
            Long requesterId,
            FriendshipStatus requesterStatus,
            Long receiverId,
            FriendshipStatus receiverStatus
    );
}