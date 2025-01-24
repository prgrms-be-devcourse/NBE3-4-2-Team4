package com.NBE3_4_2_Team4.member.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners({AuditingEntityListener.class})
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private OAuth2Provider oAuth2Provider;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String phoneNumber;

    @Setter
    private String nickname;

    @CreatedDate
    private LocalDateTime createdAt;

    public Member(Long id, String nickname, String roleName, String oAuth2ProviderName){
        this.id = id;
        this.nickname = nickname;
        this.role = Role.getRoleByName(roleName);
        this.oAuth2Provider = Member.OAuth2Provider.getOAuth2ProviderByName(oAuth2ProviderName);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", role.name())));
        authorities.add(new SimpleGrantedAuthority(oAuth2Provider.name()));

        return authorities;
    }

    public enum Role{
        ADMIN("ADMIN"),
        USER("USER");

        private final String value;

        Role(String value){
            this.value = value;
        }

        public static Role getRoleByName(String name){
            for(Role role : values()){
                if(role.value.equalsIgnoreCase(name)){
                    return role;
                }
            }
            throw new IllegalArgumentException("Role not found");
        }
    }

    public enum OAuth2Provider{
        NONE("NONE"),
        KAKAO("KAKAO");

        private final String value;

        OAuth2Provider(String value){
            this.value = value;
        }

        public static OAuth2Provider getOAuth2ProviderByName(String name){
            for(OAuth2Provider provider : values()){
                if(provider.value.equalsIgnoreCase(name)){
                    return provider;
                }
            }
            throw new IllegalArgumentException("OAuth2Provider not found");
        }
    }
}
