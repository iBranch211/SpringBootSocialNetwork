package info.cheremisin.social.network.repositories;

import info.cheremisin.social.network.entities.Friendship;
import info.cheremisin.social.network.entities.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FriendshipRepository extends CrudRepository<Friendship, Long> {

    List<Friendship> findAllByUserSenderIdOrUserReceiverId(Long userSenderId, Long userReceiverId);

    @Query("SELECT f from Friendship f WHERE (f.userSender.id = :userId OR f.userReceiver.id = :userId) " +
            "AND f.accepted = true")
    List<Friendship> findAcceptedFriendshipUsers(Long userId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Friendship f WHERE (f.userSender = :user AND f.userReceiver = :friend) " +
            "OR (f.userSender = :friend AND f.userReceiver = :user)")
    void deleteFriendRequests(User user, User friend);

    @Modifying(clearAutomatically = true)
    @Query(value = "insert into friendship (user_sender, user_receiver, accepted) VALUES (:user, :friend, true)",
            nativeQuery = true)
    void addFriendship(User user, User friend);

    @Modifying(clearAutomatically = true)
    @Query(value = "insert into friendship (user_sender, user_receiver, accepted) VALUES (:user, :friend, false)",
            nativeQuery = true)
    void addToFriends(User user, User friend);

    @Query("SELECT case when count(f)> 0 then true else false end FROM Friendship f WHERE (f.userSender = :user AND f.userReceiver = :friend) " +
            "OR (f.userSender = :friend AND f.userReceiver = :user)")
    boolean checkFriendshipExists(User user, User friend);
}
