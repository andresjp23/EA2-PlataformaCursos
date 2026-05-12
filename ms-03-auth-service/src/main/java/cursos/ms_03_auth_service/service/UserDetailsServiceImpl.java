package cursos.ms_03_auth_service.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cursos.ms_03_auth_service.model.entity.User;
import cursos.ms_03_auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Buscamos el usuario en la BD por su username
        // Si no existe lanzamos una excepción que Spring Security entiende
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Convertimos nuestro User al formato que entiende Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().getName())
                .build();
    }
}
