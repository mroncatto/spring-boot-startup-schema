package com.mroncatto.startup.service;

import com.mroncatto.startup.domain.Role;
import com.mroncatto.startup.domain.User;
import com.mroncatto.startup.exception.domain.*;
import freemarker.template.TemplateException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {

    List<User> findAll();

    User save(User user, BindingResult result) throws EmailExistException, UsernameExistException,
            ValidationException, UserNotFoundException, IOException;
    User update(String username, User user, BindingResult result) throws EmailExistException, UsernameExistException,
            ValidationException, UserNotFoundException;
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User changePassword(String username, String password) throws UsernameNotFoundException, MessagingException, TemplateException, IOException;
    User resetPassword(String email) throws EmailNotFoundException, MessagingException, TemplateException, IOException;
    User updateRole(String username, Role role) throws UsernameNotFoundException;
}
