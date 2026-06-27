package com.agrosmart.agrosmart.repository;

import com.agrosmart.agrosmart.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    /** Lista los usuarios de un rol específico (ej: "agricultor" = clientes). */
    List<Usuario> findByRolOrderByIdUsuarioDesc(String rol);
}
