package com.talecraft.talecraftbe.global.config;

import com.talecraft.talecraftbe.user.entity.User;
import com.talecraft.talecraftbe.user.entity.UserAuthority;
import com.talecraft.talecraftbe.user.repository.UserRepository;
import com.talecraft.talecraftbe.user.repository.UserAuthorityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, 
                           UserAuthorityRepository userAuthorityRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // user_authorities 테이블에 권한 데이터 생성
        initializeUserAuthorities();
        
        // 관리자 계정이 이미 존재하는지 확인
        if (!userRepository.existsById("admin")) {
            User adminUser = new User();
            adminUser.setId("admin");
            adminUser.setEmail("ADMIN@EXAMPLE.COM");
            adminUser.setUserName("관리자");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setAuthorityId(3L); // 관리자 권한
            
            userRepository.save(adminUser);
            System.out.println("관리자 계정이 생성되었습니다.");
        } else {
            System.out.println("관리자 계정이 이미 존재합니다.");
        }
    }
    
    private void initializeUserAuthorities() {
        // 일반 사용자 권한 (id=1)
        if (!userAuthorityRepository.existsById(1L)) {
            UserAuthority generalAuthority = new UserAuthority();
            generalAuthority.setId(1L);
            generalAuthority.setGeneral(true);
            generalAuthority.setPremium(false);
            generalAuthority.setAdmin(false);
            userAuthorityRepository.save(generalAuthority);
            System.out.println("일반 사용자 권한이 생성되었습니다.");
        }
        
        // 프리미엄 사용자 권한 (id=2)
        if (!userAuthorityRepository.existsById(2L)) {
            UserAuthority premiumAuthority = new UserAuthority();
            premiumAuthority.setId(2L);
            premiumAuthority.setGeneral(false);
            premiumAuthority.setPremium(true);
            premiumAuthority.setAdmin(false);
            userAuthorityRepository.save(premiumAuthority);
            System.out.println("프리미엄 사용자 권한이 생성되었습니다.");
        }
        
        // 관리자 권한 (id=3)
        if (!userAuthorityRepository.existsById(3L)) {
            UserAuthority adminAuthority = new UserAuthority();
            adminAuthority.setId(3L);
            adminAuthority.setGeneral(false);
            adminAuthority.setPremium(false);
            adminAuthority.setAdmin(true);
            userAuthorityRepository.save(adminAuthority);
            System.out.println("관리자 권한이 생성되었습니다.");
        }
    }
} 