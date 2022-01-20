package ru.krey.collections.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.Set;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.krey.collections.model.Role;

@Entity
@Data
@Table(name="usr")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Name can't be empty!")
    private String name;

    @Column(nullable = false,unique = true)
    @NotEmpty(message="Login can't be empty!")
    private String login;

    @NotEmpty(message = "Password can't be empty!")
    private String password;

    @NotEmpty(message = "Email can't be empty!")
    @Email(message = "You should input email!")
    private String email;

    private String language;

    private String theme;

    private boolean status;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name="user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public User(){}
    public User(String login, String password, String name, String email, String language, String theme, boolean status ){
        this.login=login;
        this.password=password;
        this.name=name;
        this.email=email;
        this.language=language;
        this.theme=theme;
        this.status=status;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isStatus();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isStatus();
    }
}
