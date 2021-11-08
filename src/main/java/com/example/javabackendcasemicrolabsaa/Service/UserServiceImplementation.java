package com.example.javabackendcasemicrolabsaa.Service;

import com.example.javabackendcasemicrolabsaa.Models.Persistance.PasswordResetToken;
import com.example.javabackendcasemicrolabsaa.Models.Persistance.Role;
import com.example.javabackendcasemicrolabsaa.Models.Persistance.User;
import com.example.javabackendcasemicrolabsaa.Repositories.PasswordTokenRepository;
import com.example.javabackendcasemicrolabsaa.Repositories.RoleRepository;
import com.example.javabackendcasemicrolabsaa.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional @Slf4j
public class UserServiceImplementation implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordTokenRepository passwordTokenRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.error("User not found in the database {}", username);

        User user = userRepository.findByUsername(username);
        if(user == null){
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        }else{
            log.info("User found in the database {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role ->
        {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public void createPasswordResetTokenForUser(final User user, final String token) {
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        myToken.setExpiryDate(calculateExpiryDate(myToken.getEXPIRATION()));
        passwordTokenRepository.save(myToken);
    }

    @Override
    public User getUserByPasswordResetToken(String token) {
        final PasswordResetToken passwordResetToken = passwordTokenRepository.findByToken(token);
        return passwordResetToken.getUser();
    }

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passwordResetToken = passwordTokenRepository.findByToken(token);
        log.info("User with token, {}", passwordResetToken.getUser().getUsername());
        return !tokenExists(passwordResetToken) ? "invalidToken"
                : tokenExpired(passwordResetToken) ? "expired"
                : null;
    }

    private boolean tokenExists(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean tokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    @Override
    public void saveUser(User user) {
        log.info("Saving new user {} to the database", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {} to the database", roleName, username);
        User user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public User getUser(String username) {
        log.info("Get user {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public void deleteUser(User user) {
        log.info("Deleting user: {}", user.getUsername());
        userRepository.delete(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Get all users");
        return userRepository.findAllByOrderByIdAsc();
    }

    @Override
    public List<User> getAllUsersByUsername(String username) {
        log.info("Get all users search");
        return userRepository.findAllByUsername(Sort.by(Sort.Direction.DESC, "username"), username);
    }

    public long getUsersCount() {
        log.info("Get users count");
        return userRepository.count();
    }
}
