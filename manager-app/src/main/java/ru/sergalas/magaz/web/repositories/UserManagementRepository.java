package ru.sergalas.magaz.web.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.sergalas.magaz.web.entity.UserManagement;

import java.util.Optional;

public interface UserManagementRepository extends CrudRepository<UserManagement, Integer> {

    Optional<UserManagement> findByUsername(String username);
}
