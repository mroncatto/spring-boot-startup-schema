package com.mroncatto.startup.service;

import com.mroncatto.startup.domain.Role;

import java.util.List;

public interface RoleService {
    public List<Role> findAll();
    public Role findById(Long id);
}
