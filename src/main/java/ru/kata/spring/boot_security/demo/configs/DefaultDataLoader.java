package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private boolean alreadyLoaded = false;

    private final UserService userService;

    private final RoleService roleService;

    public DefaultDataLoader(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        if (alreadyLoaded) {
            return;
        }

        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_USER");

        List<Role> adminRoles = new ArrayList<>();
        adminRoles.add(roleService.findByName("ROLE_ADMIN"));
        adminRoles.add(roleService.findByName("ROLE_USER"));

        User user = new User();
        user.setFirstName("admin");
        user.setLastName("admin");
        user.setEmail("admin@mail.ru");
        user.setUsername("admin");
        user.setPassword("admin");
        user.setRoles(adminRoles);
        userService.save(user);

        alreadyLoaded = true;
    }

    @Transactional
    void createRoleIfNotFound(String roleName) {
        Role role = roleService.findByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            roleService.save(role);
        }
    }
}