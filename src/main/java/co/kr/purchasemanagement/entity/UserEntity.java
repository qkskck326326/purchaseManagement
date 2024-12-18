package co.kr.purchasemanagement.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.Date;

@Entity
@Table(name = "user")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "signup_at")
    private Date signupAt;

    private String address;

    @Column(name = "mobile_No")
    private String mobileNo;

    @Column(name = "profile_image")
    private String profileImage;
}
