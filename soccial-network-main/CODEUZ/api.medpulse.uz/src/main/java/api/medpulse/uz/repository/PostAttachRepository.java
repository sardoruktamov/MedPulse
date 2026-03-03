package api.medpulse.uz.repository;

import api.medpulse.uz.entity.PostAttachEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostAttachRepository extends CrudRepository<PostAttachEntity, Integer> {

    List<PostAttachEntity> findAllByPostId(String postId);

    @Transactional
    @Modifying
    @Query("delete from PostAttachEntity where postId = ?1")
    void deleteByPostId(String postId);
}
