package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.UserData;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {
    @Query("select userData from UserData userData where userData.user.login = ?#{authentication.name}")
    List<UserData> findByUserIsCurrentUser();
    boolean existsByUserId(Long userId);
}
