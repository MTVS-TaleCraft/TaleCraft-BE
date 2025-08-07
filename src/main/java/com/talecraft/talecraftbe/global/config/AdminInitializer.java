package com.talecraft.talecraftbe.global.config;

import com.talecraft.talecraftbe.user.entity.User;
import com.talecraft.talecraftbe.user.entity.UserAuthority;
import com.talecraft.talecraftbe.user.repository.UserRepository;
import com.talecraft.talecraftbe.user.repository.UserAuthorityRepository;
import com.talecraft.talecraftbe.tag.model.entity.Tag;
import com.talecraft.talecraftbe.tag.repository.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final TagRepository tagRepository;

    public AdminInitializer(UserRepository userRepository, 
                           UserAuthorityRepository userAuthorityRepository,
                           PasswordEncoder passwordEncoder,
                           TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.tagRepository = tagRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // user_authorities 테이블에 권한 데이터 생성
        initializeUserAuthorities();
        
        // 기본 태그들 초기화
        initializeDefaultTags();
        
        // 관리자 계정이 이미 존재하는지 확인
        if (!userRepository.existsById("admin")) {
            User adminUser = new User();
            adminUser.setId("admin");
            adminUser.setEmail("ADMIN@EXAMPLE.COM");
            adminUser.setUserName("관리자");
            adminUser.setPassword(passwordEncoder.encode("(8QG7g4uh(5A"));
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
    
    private void initializeDefaultTags() {
        // TagService의 기본 태그 목록 사용
        List<String> defaultTags = Arrays.asList(
            "SF", "호러", "일상", "역사", "스포츠", "음악", "요리", "여행",
            "동물", "자연", "우주", "마법", "전쟁", "정치", "드라마", "로맨스",
            "스릴러", "액션", "코미디", "판타지"
        );
        
        int createdCount = 0;
        for (String tagName : defaultTags) {
            // 태그가 이미 존재하는지 확인
            if (!tagRepository.existsByTagName(tagName)) {
                Tag tag = new Tag();
                tag.setTagName(tagName);
                tagRepository.save(tag);
                createdCount++;
                System.out.println("기본 태그 생성: " + tagName);
            }
        }
        
        if (createdCount > 0) {
            System.out.println("총 " + createdCount + "개의 기본 태그가 생성되었습니다.");
        } else {
            System.out.println("모든 기본 태그가 이미 존재합니다.");
        }
    }
} 