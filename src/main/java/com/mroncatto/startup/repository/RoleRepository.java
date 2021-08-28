package com.mroncatto.startup.repository;

import com.mroncatto.startup.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
