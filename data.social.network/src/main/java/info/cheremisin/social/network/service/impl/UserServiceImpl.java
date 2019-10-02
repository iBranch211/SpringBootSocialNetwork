package info.cheremisin.social.network.service.impl;

import info.cheremisin.social.network.constants.Gender;
import info.cheremisin.social.network.converters.UserDtoToUserConverter;
import info.cheremisin.social.network.converters.UserToUserDtoConverter;
import info.cheremisin.social.network.dto.UserDTO;
import info.cheremisin.social.network.entities.Role;
import info.cheremisin.social.network.entities.User;
import info.cheremisin.social.network.repositories.RoleRepository;
import info.cheremisin.social.network.repositories.UserRepository;
import info.cheremisin.social.network.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static info.cheremisin.social.network.constants.Constants.ROLE_USER;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserToUserDtoConverter userToUserDtoConverter;
    private UserDtoToUserConverter userDtoToUserConverter;
    private BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserToUserDtoConverter userToUserDtoConverter,
                           UserDtoToUserConverter userDtoToUserConverter, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userToUserDtoConverter = userToUserDtoConverter;
        this.userDtoToUserConverter = userDtoToUserConverter;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email);
        UserDTO userDTO = userToUserDtoConverter.convert(user);
        return userDTO;
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id = " + id));
        UserDTO userDTO = userToUserDtoConverter.convert(user);
        return userDTO;
    }

    @Override
    public void createUser(UserDTO userDTO) {
        User user = userDtoToUserConverter.convert(userDTO);

        if(user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        Role role = roleRepository.getRoleByName(ROLE_USER);
        user.getRoles().add(role);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updatePassword(String password, Long id) {
        String encodedPassword = passwordEncoder.encode(password);
        userRepository.updatePassword(encodedPassword, id);
    }

    @Override
    @Transactional
    public void updateUser(UserDTO user) {
        int gender = Gender.getGenderByName(user.getSex());
        userRepository.updateUserSettings(user.getFirstName(), user.getLastName(), user.getDob(), gender, user.getPhone(),
                user.getId());
    }

    @Override
    public void updateUserImage(UserDTO userDTO, byte[] image) {
        User user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        Byte[] byteObject = new Byte[image.length];
        int i = 0;
        for (byte aByte : image) {
            byteObject[i++] = aByte;
        }

        user.setImage(byteObject);
        userRepository.save(user);

    }
}
