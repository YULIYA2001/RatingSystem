package by.ratingsystem.repository;

import by.ratingsystem.model.GameObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameObjectRepository extends JpaRepository<GameObject, Long> {
    int countByGameId(Long gameId);
}
