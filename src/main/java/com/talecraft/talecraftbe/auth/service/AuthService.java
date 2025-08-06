package com.talecraft.talecraftbe.auth.service;

import com.talecraft.talecraftbe.auth.dto.LoginRequest;
import com.talecraft.talecraftbe.auth.dto.SignupRequest;
import com.talecraft.talecraftbe.auth.dto.UpdateUserRequest;
import com.talecraft.talecraftbe.auth.dto.FindUserIdRequest;
import com.talecraft.talecraftbe.auth.dto.FindPasswordRequest;
import com.talecraft.talecraftbe.auth.dto.UserDetailResponse;
import com.talecraft.talecraftbe.user.entity.User;
import com.talecraft.talecraftbe.user.repository.UserRepository;
import com.talecraft.talecraftbe.novel.dto.response.ResponseGetNovelListDto;
import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import com.talecraft.talecraftbe.novel.repository.NovelRepository;
import com.talecraft.talecraftbe.novel.service.NovelService;
import com.talecraft.talecraftbe.auth.config.JwtProvider;
import com.talecraft.talecraftbe.verification.service.EmailVerificationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import com.talecraft.talecraftbe.novel.dto.response.ResponseGetNovelDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtProvider jwtProvider;
    private final EmailVerificationService emailVerificationService;
    private final JavaMailSender mailSender;
    private final NovelRepository novelRepository;
    private final NovelService novelService;

    public AuthService(UserRepository userRepo,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authManager,
                       JwtProvider jwtProvider,
                       EmailVerificationService emailVerificationService,
                       JavaMailSender mailSender,
                       NovelRepository novelRepository,
                       NovelService novelService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtProvider = jwtProvider;
        this.emailVerificationService = emailVerificationService;
        this.mailSender = mailSender;
        this.novelRepository = novelRepository;
        this.novelService = novelService;
    }

    /**
     * 회원가입: 이메일 인증 확인 후 USERS 테이블에 저장하고 SIGNED_UP = 1로 변경
     */
    public void signup(SignupRequest req) {
        // 아이디 중복 검사
        if (userRepo.existsById(req.userId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        
        // 이메일 중복 검사
        if (userRepo.existsByEmail(req.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        
        // 이메일 인증이 완료되었는지 확인
        if (!emailVerificationService.isEmailVerified(req.email())) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다. 먼저 이메일 인증을 완료해주세요.");
        }
        
        // 이미 회원가입이 완료되었는지 확인
        if (emailVerificationService.isSignedUp(req.email())) {
            throw new IllegalArgumentException("이미 회원가입이 완료된 이메일입니다.");
        }
        
        // USERS 테이블에 회원 정보 저장 (authority_id = 1)
        User user = new User();
        user.setId(req.userId());
        user.setUserName(req.userName());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setAuthorityId(1L); // AUTHORITIES 테이블의 ID=1 (일반 사용자)
        userRepo.save(user);
        
        // 회원가입 완료 처리 (SIGNED_UP = 1)
        emailVerificationService.markAsSignedUp(req.email());
    }

    /**
     * 로그인: 아이디/비밀번호 인증, JWT 생성 후 쿠키에 담아 응답
     */
    public String login(LoginRequest req, HttpServletResponse response) {
        logger.info("Starting login process for userId: {}", req.userId());
        
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(req.userId(), req.password());
        logger.info("Created authentication token");
        
        Authentication auth = authManager.authenticate(authToken);
        logger.info("Authentication successful");
        
        SecurityContextHolder.getContext().setAuthentication(auth);
        logger.info("Security context set");

        String jwt = jwtProvider.generateToken(auth);
        logger.info("JWT token generated: {}", jwt.substring(0, Math.min(jwt.length(), 20)) + "...");
        
        // JWT 토큰을 쿠키로 설정
        Cookie cookie = new Cookie("JwtToken", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTP 환경에서 쿠키 전송을 위해 false로 설정
        cookie.setPath("/");
        response.addCookie(cookie);
        logger.info("JWT cookie set with value: {}", jwt.substring(0, Math.min(jwt.length(), 20)) + "...");
        logger.info("Cookie details - Name: {}, Path: {}, HttpOnly: {}, Secure: {}", 
                   cookie.getName(), cookie.getPath(), cookie.isHttpOnly(), cookie.getSecure());
        
        return jwt;
    }

    /**
     * 로그아웃: JWT 쿠키 제거 및 SecurityContext 초기화
     */
    public void logout(HttpServletResponse response) {
        // 현재 로그인된 사용자 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        logger.info("Logout for user: {}", authentication.getName());
        
        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
        
        // JWT 쿠키 제거
        Cookie cookie = new Cookie("JwtToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTP 환경에서 쿠키 전송을 위해 false로 설정
        cookie.setPath("/");
        // 도메인 설정 제거 (브라우저가 자동으로 현재 도메인에 설정)
        // cookie.setDomain("localhost"); // 로컬 개발 환경을 위한 도메인 설정
        cookie.setMaxAge(0); // 쿠키 즉시 만료
        response.addCookie(cookie);
        
        logger.info("Logout completed successfully");
    }

    /**
     * 현재 로그인된 사용자 정보 조회
     */
    public Map<String, String> getCurrentUserInfo() {
        // 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String currentUserId = userDetails.getUsername();
        
        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        return Map.of(
            "userId", currentUser.getId(),
            "userName", currentUser.getUserName(),
            "email", currentUser.getEmail(),
            "authorityId", String.valueOf(currentUser.getAuthorityId())
        );
    }

    /**
     * 프로필 수정: 현재 로그인된 사용자의 정보 수정 또는 관리자가 다른 사용자 정보 수정
     */
    public void updateProfile(UpdateUserRequest req) {
        // 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String currentUserId = userDetails.getUsername();
        
        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 변경할 사용자 결정 (관리자인 경우 다른 사용자 정보 변경 가능)
        User targetUser = currentUser;
        if (currentUser.getAuthorityId() == 3L && req.targetUserId() != null && !req.targetUserId().isEmpty()) {
            // 관리자가 다른 사용자 정보를 변경하는 경우
            targetUser = userRepo.findById(req.targetUserId())
                    .orElseThrow(() -> new IllegalArgumentException("변경할 사용자를 찾을 수 없습니다."));
        }
        
        // 현재 비밀번호 확인 (자신의 정보를 변경하는 경우에만)
        if (targetUser.getId().equals(currentUserId) && req.currentPassword() != null && !req.currentPassword().isEmpty()) {
            if (!passwordEncoder.matches(req.currentPassword(), currentUser.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
        }
        
        // 이메일 중복 검사 (다른 사용자가 사용 중인지 확인)
        if (req.email() != null && !req.email().isEmpty() && !req.email().equals(targetUser.getEmail())) {
            if (userRepo.existsByEmail(req.email())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }
        
        // 사용자 정보 업데이트
        if (req.userName() != null && !req.userName().isEmpty()) {
            targetUser.setUserName(req.userName());
        }
        
        if (req.email() != null && !req.email().isEmpty()) {
            targetUser.setEmail(req.email());
        }
        
        if (req.newPassword() != null && !req.newPassword().isEmpty()) {
            targetUser.setPassword(passwordEncoder.encode(req.newPassword()));
        }
        
        userRepo.save(targetUser);
    }

    /**
     * 아이디 찾기: 이메일로 사용자를 찾아 아이디 반환
     */
    public String findUserId(FindUserIdRequest req) {
        // 이메일로 사용자 찾기
        User user = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 등록된 계정을 찾을 수 없습니다."));
        
        return user.getId();
    }

    /**
     * 비밀번호 찾기: 이메일로 임시 비밀번호 발송
     */
    public void findPassword(FindPasswordRequest req) {
        // 이메일과 아이디로 사용자 찾기
        User user = userRepo.findByEmailAndId(req.email(), req.userId())
                .orElseThrow(() -> new IllegalArgumentException("해당 정보로 등록된 계정을 찾을 수 없습니다."));
        
        // 임시 비밀번호 생성 (8자리 영문+숫자)
        String tempPassword = generateTempPassword();
        
        // 비밀번호 업데이트
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepo.save(user);
        
        // 이메일 발송
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(req.email());
        mail.setSubject("[TaleCraft] 임시 비밀번호 안내");
        mail.setText("안녕하세요!\n\n" +
                "TaleCraft 임시 비밀번호를 안내드립니다.\n\n" +
                "임시 비밀번호: " + tempPassword + "\n\n" +
                "보안을 위해 로그인 후 반드시 비밀번호를 변경해주세요.\n" +
                "본인이 요청하지 않은 경우 이 메일을 무시하세요.\n\n" +
                "감사합니다.\n" +
                "TaleCraft 팀");
        mailSender.send(mail);
    }

    /**
     * 임시 비밀번호 생성 (8자리 영문+숫자)
     */
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }

    /**
     * 개별 사용자 정보 조회 (관리자용)
     */
    public UserDetailResponse getUserDetail(String userId) {
        // 현재 로그인된 사용자가 관리자인지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String currentUserId = userDetails.getUsername();
        
        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("현재 사용자를 찾을 수 없습니다."));
        
        // 관리자 권한 확인 (authorityId가 3이어야 함)
        if (currentUser.getAuthorityId() != 3L) {
            throw new IllegalArgumentException("관리자 권한이 필요합니다.");
        }
        
        // 조회할 사용자 정보 가져오기
        User targetUser = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("조회할 사용자를 찾을 수 없습니다."));
        
        return UserDetailResponse.fromUser(targetUser);
    }

    /**
     * 전체 사용자 목록 조회 (관리자용)
     */
    public List<UserDetailResponse> getAllUsers() {
        // 현재 로그인된 사용자가 관리자인지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String currentUserId = userDetails.getUsername();
        
        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("현재 사용자를 찾을 수 없습니다."));
        
        // 관리자 권한 확인 (authorityId가 3이어야 함)
        if (currentUser.getAuthorityId() != 3L) {
            throw new IllegalArgumentException("관리자 권한이 필요합니다.");
        }
        
        // 전체 사용자 목록 조회 (관리자 제외)
        List<User> users = userRepo.findByAuthorityIdNot(3L);
        return users.stream()
                .map(UserDetailResponse::fromUser)
                .toList();
    }

    // 관리자용 소설 조회 (차단된 소설 포함)
    public ResponseGetNovelListDto getNovelListForAdmin(String keyword, String type) {
        try {
            List<NovelEntity> novelEntityList;

            // 검색 조건 분기
            if (type == null || keyword == null || keyword.isBlank()) {
                novelEntityList = novelRepository.findAll();
            } else {
                novelEntityList = switch (type) {
                    case "title" -> novelRepository.findAllByTitle(keyword);
                    case "userName" -> novelRepository.findAllByUserUserName(keyword);
                    case "userId" -> novelRepository.findAllByUserId(keyword);
                    default -> throw new IllegalArgumentException("유효하지 않은 검색 타입입니다: " + type);
                };
            }

            // 차단된 소설도 포함 (필터링하지 않음)
            List<ResponseGetNovelDto> responseGetNovelDtoList = novelEntityList.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            ResponseGetNovelListDto response = new ResponseGetNovelListDto();
            response.setNovelList(responseGetNovelDtoList);
            response.setTotalElements(novelRepository.countByIsDeleted(false));
            return response;

        } catch (RuntimeException e) {
            throw new RuntimeException("소설 검색 중 오류 발생", e);
        }
    }

    // 소설 차단/해제 (관리자용)
    public ResponseEntity<?> toggleNovelBan(long novelId) {
        try {
            logger.info("toggleNovelBan 호출됨 - novelId: {}", novelId);
            
            // 현재 인증된 사용자 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.warn("인증되지 않은 사용자입니다.");
                return ResponseEntity.status(403).body(Map.of("error", "관리자 권한이 필요합니다."));
            }
            
            User user = (User) authentication.getPrincipal();
            if (user == null) {
                logger.warn("사용자가 null입니다.");
                return ResponseEntity.status(403).body(Map.of("error", "관리자 권한이 필요합니다."));
            }
            
            if (user.getAuthorityId() == null) {
                logger.warn("사용자 권한 ID가 null입니다. 사용자: {}", user.getId());
                return ResponseEntity.status(403).body(Map.of("error", "관리자 권한이 필요합니다."));
            }
            
            logger.info("사용자 권한 ID: {}, 관리자 권한(3)과 비교: {}", user.getAuthorityId(), user.getAuthorityId() == 3L);
            
            if (user.getAuthorityId() != 3L) {
                logger.warn("관리자 권한이 아닙니다. 사용자: {}, 권한: {}", user.getId(), user.getAuthorityId());
                return ResponseEntity.status(403).body(Map.of("error", "관리자 권한이 필요합니다."));
            }

            NovelEntity novelEntity = novelRepository.findByNovelId(novelId);
            if (novelEntity == null) {
                logger.warn("소설을 찾을 수 없습니다. novelId: {}", novelId);
                return ResponseEntity.status(404).body(Map.of("error", "소설을 찾을 수 없습니다."));
            }

            // 현재 차단 상태를 반전
            boolean currentBanStatus = novelEntity.isBanned();
            boolean newBanStatus = !currentBanStatus;
            logger.info("소설 차단 상태 변경 - novelId: {}, 현재: {}, 새로운: {}", novelId, currentBanStatus, newBanStatus);
            
            novelEntity.updateIsBanned(newBanStatus);
            novelRepository.save(novelEntity);
            
            // 저장 후 실제 데이터베이스에서 다시 조회하여 확인
            NovelEntity savedNovel = novelRepository.findByNovelId(novelId);
            logger.info("데이터베이스 저장 후 실제 isBanned 값: {}", savedNovel.isBanned());

            String message = newBanStatus ? "소설이 차단되었습니다." : "소설 차단이 해제되었습니다.";
            logger.info("소설 차단/해제 성공 - novelId: {}, 메시지: {}", novelId, message);
            
            return ResponseEntity.ok(Map.of(
                "message", message,
                "isBanned", newBanStatus,
                "novelId", novelId
            ));
        } catch (Exception e) {
            logger.error("소설 차단/해제 중 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of("error", "소설 차단/해제에 실패했습니다."));
        }
    }

    private ResponseGetNovelDto convertToDto(NovelEntity novelEntity) {
        ResponseGetNovelDto responseGetNovelDto = new ResponseGetNovelDto();
        responseGetNovelDto.setNovelId(novelEntity.getNovelId());
        setAuthor(novelEntity, responseGetNovelDto);
        responseGetNovelDto.setTitle(novelEntity.getTitle());
        responseGetNovelDto.setTitleImage(novelEntity.getTitleImage());
        responseGetNovelDto.setSummary(novelEntity.getSummary());
        responseGetNovelDto.setAvailability(novelEntity.getAvailability());
        responseGetNovelDto.setBanned(novelEntity.isBanned());
        
        return responseGetNovelDto;
    }

    private void setAuthor(NovelEntity novelEntity, ResponseGetNovelDto response) {
        if(novelEntity.getUser()!=null){
            response.setAuthor(novelEntity.getUser().getUserName());
        }else{
            logger.info("user is null");
            response.setAuthor("No Author");
        }
    }
}

