package com.microusers.gesusers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microusers.gesusers.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username); // Método para buscar por username
}
