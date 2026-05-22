package com.agrosmart.agrosmart.service;

import com.agrosmart.agrosmart.entity.Agricultor;
import com.agrosmart.agrosmart.entity.Usuario;
import com.agrosmart.agrosmart.repository.AgricultorRepository;
import com.agrosmart.agrosmart.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AgricultorRepository agricultorRepository;

    @Transactional
    public Usuario registrarAgricultor(String nombre, String email, String password, String telefono, String rut, String direccion) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El email ya está registrado.");
        }

        // Crear el usuario
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(nombre);
        usuario.setEmail(email);
        usuario.setContrasenaHash(password); // En un sistema real, usar BCrypt
        usuario.setTelefono(telefono);
        usuario.setRol("agricultor");
        usuario = usuarioRepository.save(usuario);

        // Crear el agricultor vinculado
        Agricultor agricultor = new Agricultor();
        agricultor.setIdUsuario(usuario.getIdUsuario());
        agricultor.setRut(rut);
        agricultor.setRazonSocial(nombre);
        agricultor.setDireccionPredio(direccion);
        agricultorRepository.save(agricultor);

        return usuario;
    }

    public Usuario login(String email, String password, HttpSession session) {
        Optional<Usuario> opt = usuarioRepository.findByEmail(email);

        if (opt.isPresent() && opt.get().getContrasenaHash().equals(password)) {
            Usuario usuario = opt.get();
            
            // Buscar el ID de agricultor si corresponde
            Optional<Agricultor> agriOpt = agricultorRepository.findByIdUsuario(usuario.getIdUsuario());

            session.setAttribute("idUsuario", usuario.getIdUsuario());
            session.setAttribute("nombreUsuario", usuario.getNombreCompleto());
            session.setAttribute("emailUsuario", usuario.getEmail());
            session.setAttribute("telefonoUsuario", usuario.getTelefono());
            session.setAttribute("rolUsuario", usuario.getRol());
            
            if (agriOpt.isPresent()) {
                session.setAttribute("idAgricultor", agriOpt.get().getIdAgricultor());
            }

            return usuario;
        }

        return null;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}
