package co.kr.userservice.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "email")
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "signup_at")
    private Date signupAt;

    @Column(name = "address")
    private String address;

    @Column(name = "isVerify")
    private boolean isVerify = false;

    @Column(name = "mobile_No")
    private String mobileNo;

    @PrePersist
    protected void onCreate() {
        signupAt = new Date();
    }
}
