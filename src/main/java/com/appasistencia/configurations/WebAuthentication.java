package com.appasistencia.configurations;

import com.appasistencia.models.Usuario;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebAuthentication {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return inputName -> {
            Usuario u = usuarioRepository.findByCorreo(inputName);
            if (u == null) throw new UsernameNotFoundException("Usuario no encontrado: " + inputName);
            return new User(
                    u.getCorreo(),
                    u.getContrasena(),  // debe estar cifrada en DB
                    AuthorityUtils.createAuthorityList(u.getRol().name())
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // delegating encoder -> {bcrypt} por defecto
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
