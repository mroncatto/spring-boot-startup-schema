package com.mroncatto.startup.service.impl;

import com.mroncatto.startup.domain.Role;
import com.mroncatto.startup.repository.RoleRepository;
import com.mroncatto.startup.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> findAll() {
        return this.roleRepository.findAll();
    }

    @Override
    public Role findById(Long id) {
        return this.roleRepository.findById(id).orElseThrow(() -> new NoResultException("Error handling roles"));
    }
}
