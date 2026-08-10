package com.edugauge.repositiry;

import com.edugauge.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);
    Optional<User> findByLoginId(String loginId);
    List<User> findByLoginIdContainingOrNicknameContaining(
            String loginId,
            String nickname
    );
}
