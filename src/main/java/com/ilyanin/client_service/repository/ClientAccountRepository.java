package com.ilyanin.client_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ilyanin.client_service.entity.ClientAccountEntity;

@Repository 
public interface ClientAccountRepository extends JpaRepository<ClientAccountEntity, Long> {}
