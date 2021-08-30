package com.mroncatto.startup.service.impl;

import com.mroncatto.startup.domain.Role;
import com.mroncatto.startup.domain.User;
import com.mroncatto.startup.domain.UserPrincipal;
import com.mroncatto.startup.exception.domain.*;
import com.mroncatto.startup.repository.RoleRepository;
import com.mroncatto.startup.repository.UserRepository;
import com.mroncatto.startup.service.EmailService;
import com.mroncatto.startup.service.LoginAttemptService;
import com.mroncatto.startup.service.UserService;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.mail.MessagingException;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.*;

import static com.mroncatto.startup.constant.UserConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public User save(User user, BindingResult result) throws EmailExistException, UsernameExistException, ValidationException, UserNotFoundException, IOException {
        if (result.hasErrors())
            throw new ValidationException(Objects.requireNonNull(Objects.requireNonNull(result.getFieldError())
                    .getDefaultMessage()).toUpperCase());

        validateNewUsernameAndEmail(StringUtils.EMPTY, user.getUsername(), user.getEmail());
        User newuser = new User();
        user.setPassword(encodePassword(user.getPassword()));
        newuser = userRepository.save(user);
        return this.userRepository.save(newuser);
    }

    @Override
    public User update(String username, User user, BindingResult result) throws EmailExistException, UsernameExistException, ValidationException, UserNotFoundException {
        if (result.hasErrors())
            throw new ValidationException(Objects.requireNonNull(Objects.requireNonNull(result.getFieldError())
                    .getDefaultMessage()).toUpperCase());

        User currentUser = validateNewUsernameAndEmail(username, user.getUsername(), user.getEmail());

        if(currentUser != null) {
            currentUser.setFullName(user.getFullName());
            currentUser.setEmail(user.getEmail());
            currentUser.setUsername(user.getUsername());
            currentUser = userRepository.save(currentUser);
            return this.userRepository.save(currentUser);
        } else {
            return null;
        }
    }

    @Override
    public User findUserByUsername(String username) {
        return this.userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return this.userRepository.findUserByEmail(email);
    }

    @Override
    public User changePassword(String username, String password) throws UsernameNotFoundException, MessagingException, TemplateException, IOException {

        User user = this.userRepository.findUserByUsername(username);

        if(user == null)
            throw new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME + username);

        user.setPassword(encodePassword(password));
        User updatedUser = this.userRepository.save(user);
        String firstName = user.getFullName().split(" ")[0];
        this.emailService.sendEmail(user.getEmail(),
                firstName,
                "Your password has changed",
                "Hi " + firstName + ", your password has changed !");
        return updatedUser;
    }

    @Override
    public User resetPassword(String email) throws EmailNotFoundException, MessagingException, TemplateException, IOException {
        return null;
        // TODO: implement you use case
    }

    @Override
    public User updateRole(String username, Role role) throws UsernameNotFoundException {

        User currentUser = this.userRepository.findUserByUsername(username);
        Role currentRole = this.roleRepository.findById(role.getId()).orElse(null);

        if(currentRole == null)
            throw new NoResultException("Error while handling roles");

        if(currentUser == null)
            throw new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME);

        List<Role> roles = currentUser.getRoles();

        boolean exists = false;

        for(Role rol:roles){
            if(rol == currentRole){
                roles.remove(roles.indexOf(currentRole));
                exists = true;
                break;
            }
        }

        if (!exists)
            roles.add(currentRole);

        currentUser.setRoles(roles);
        return this.userRepository.save(currentUser);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }


    private void validateLoginAttempt(User user) {
        if (user.isNonLocked()) {
            user.setNonLocked(!loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private User validateNewUsernameAndEmail(String currentUsername, String username, String email)
            throws EmailExistException, UsernameExistException, UserNotFoundException {
        User userByNewUsername = this.findUserByUsername(username);
        User userByNewEmail = this.findUserByEmail(email);

        // New user or Update
        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = userRepository.findUserByUsername(currentUsername);

            // Username not found
            if (currentUser == null)
                throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME + currentUsername);

            // Username already exists
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId()))
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);

            // Email already exists
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId()))
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);

            return currentUser;

        } else {

            // Email already exists
            if (userByNewEmail != null)
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);

            // Username already exists
            if (userByNewUsername != null)
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);

            return null;
        }

    }
}
