package com.agrosmart.agrosmart.config;

import com.agrosmart.agrosmart.entity.Usuario;
import com.agrosmart.agrosmart.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Siembra un usuario ADMINISTRADOR por defecto si todavía no existe.
 *
 * Antes no había forma de crear un administrador desde la aplicación
 * (el registro fija siempre rol "agricultor"). Este inicializador garantiza
 * que siempre exista al menos un admin para poder entrar al panel.
 *
 * NOTA DE SEGURIDAD: la contraseña se guarda en texto plano, igual que el
 * resto del sistema actual. Pendiente: migrar a BCrypt.
 */
@Component
@Order(10)
public class DataInitializer implements CommandLineRunner {

    public static final String ADMIN_EMAIL = "admin@agrosmart.cl";
    public static final String ADMIN_PASSWORD = "Admin123";

    private final UsuarioRepository usuarioRepository;

    public DataInitializer(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        try {
            if (usuarioRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNombreCompleto("Administrador AgroSmart");
                admin.setEmail(ADMIN_EMAIL);
                admin.setContrasenaHash(ADMIN_PASSWORD); // texto plano (consistente con login actual)
                admin.setRol(Roles.ADMIN);
                admin.setActivo(true);
                usuarioRepository.save(admin);
                System.out.println(">>> Usuario ADMIN creado: " + ADMIN_EMAIL + " / " + ADMIN_PASSWORD);
            } else {
                System.out.println(">>> Usuario ADMIN ya existe: " + ADMIN_EMAIL);
            }
        } catch (Exception e) {
            System.out.println(">>> Nota: no se pudo sembrar el ADMIN (" + e.getMessage() + ").");
        }
    }
}
