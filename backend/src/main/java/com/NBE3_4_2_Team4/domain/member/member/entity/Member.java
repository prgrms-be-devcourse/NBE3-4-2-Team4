package com.NBE3_4_2_Team4.domain.member.member.entity;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Cash;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point;
import com.NBE3_4_2_Team4.domain.member.bankAccount.entity.BankAccount;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners({AuditingEntityListener.class})
@ToString
public class Member {
    @Override
    public boolean equals(Object o) {
        if (o instanceof Member member) {
            return Objects.equals(member.getId(), this.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    //id getter을 코틀린에서 사용할수 있도록 public 으로 바꿨습니다
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    public Long id;

    @Column(nullable = false)
    public Role role;

    @Column(nullable = false)
    private OAuth2Provider oAuth2Provider;

    //username getter을 코틀린에서 사용할수 있도록 public 으로 바꿨습니다
    @Column(nullable = false, unique = true)
    @Setter(AccessLevel.NONE)
    public String username;

    @Column(nullable = false)
    private String password;

    private String realName;

    private String emailAddress;

    @Builder.Default
    private boolean emailVerified = false;

    @Column(nullable = false, unique = true)
    public String nickname;

    @CreatedDate
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Embedded
    @Builder.Default
    public Point point = new Point();

    @Embedded
    @Builder.Default
    public Cash cash = new Cash();

    @OneToMany(mappedBy = "author")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<Answer> answers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<BankAccount> bankAccounts = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private OAuth2RefreshToken oauth2RefreshToken = null;

    public LocalDate lastAttendanceDate;

    public boolean isFirstLoginToday(){
        LocalDate today = LocalDate.now();
        return lastAttendanceDate == null || today.isBefore(lastAttendanceDate);
    }

    public Member(Long id, String username, String nickname, String roleName, String oAuth2ProviderName, String emailAddress,  boolean emailVerified){
        this.id = id;
        this.username = username;
        this.password = "";
        this.nickname = nickname;
        this.role = Role.getRoleByName(roleName);
        this.oAuth2Provider = Member.OAuth2Provider.getOAuth2ProviderByName(oAuth2ProviderName);
        this.emailAddress = emailAddress;
        this.emailVerified = emailVerified;
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
        KAKAO("KAKAO"),
        NAVER("NAVER"),
        GOOGLE("GOOGLE");
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
