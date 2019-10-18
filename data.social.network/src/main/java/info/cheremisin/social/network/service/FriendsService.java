package info.cheremisin.social.network.service;

import info.cheremisin.social.network.dto.UserDTO;

import java.util.Map;
import java.util.Set;

public interface FriendsService {

    Map<String, Set<UserDTO>> getFriends(Long userId);
}
