package com.talecraft.talecraftbe.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talecraft.talecraftbe.ai.dto.data.AIOptions;
import com.talecraft.talecraftbe.ai.dto.request.AddAIRequestDTO;
import com.talecraft.talecraftbe.ai.dto.response.AddAIResponseDTO;
import com.talecraft.talecraftbe.ai.dto.response.ChatListResponseDTO;
import com.talecraft.talecraftbe.ai.dto.response.ChatMessageResponseDTO;
import com.talecraft.talecraftbe.ai.dto.response.FindAIResponseDTO;
import com.talecraft.talecraftbe.ai.exception.AIRequestFailException;
import com.talecraft.talecraftbe.ai.model.entity.ChatList;
import com.talecraft.talecraftbe.ai.model.entity.ChatMessage;
import com.talecraft.talecraftbe.ai.repository.ChatListRepository;
import com.talecraft.talecraftbe.ai.repository.ChatMessageRepository;
import com.talecraft.talecraftbe.novel.episode.model.entity.EpisodeEntity;
import com.talecraft.talecraftbe.novel.episode.repository.EpisodeRepository;
import com.talecraft.talecraftbe.novel.episode.service.EpisodeService;
import com.talecraft.talecraftbe.novel.repository.NovelRepository;
import com.talecraft.talecraftbe.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class AIService {
    @Value("${ai.url}")
    private String alURL;

    private final RestTemplate restTemplate;

    private final ChatMessageRepository chatMessageRepository;
    private final ChatListRepository chatListRepository;
    private final EpisodeRepository episodeRepository;

    public AIService(RestTemplate restTemplate,
                     ChatMessageRepository chatMessageRepository,
                     ChatListRepository chatListRepository,
                     EpisodeRepository episodeRepository) {
        this.restTemplate = restTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.chatListRepository = chatListRepository;
        this.episodeRepository = episodeRepository;
    }

    public AddAIResponseDTO requestAI(AddAIRequestDTO requestDTO) {
        try {
            String url = AIOptions.getAIURL(requestDTO.getOption(), alURL);
            if (requestDTO.getOption() == AIOptions.STORY_EXTENSION)
                return extendStory(requestDTO, url);
            ResponseEntity<String> getResponseAI = restTemplate.postForEntity(url, requestDTO, String.class);
            String json = getResponseAI.getBody();

            ObjectMapper mapper = new ObjectMapper();
            AddAIResponseDTO responseAIDTO = mapper.readValue(json, AddAIResponseDTO.class);

            return responseAIDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new AIRequestFailException("AI 통신 실패");
        }
    }

    private AddAIResponseDTO extendStory(AddAIRequestDTO requestDTO, String url) {
        // 사용자가 입력한 초기 소설 내용 (질문 필드 활용)
        String currentStory = requestDTO.getQuestion();
        // 사용자가 요청한 최소 글자 수 (전체 소설의 목표 길이)
        Integer desiredLength = requestDTO.getExtensionLength();

        // 입력값 유효성 검사
        if (desiredLength == null || desiredLength < 1000 || desiredLength > 19000) {
            throw new IllegalArgumentException("Invalid extensionLength. Must be between 1,000 and 20,000 characters.");
        }
        if (currentStory == null || currentStory.trim().isEmpty()) {
            throw new IllegalArgumentException("Initial story (question) cannot be empty for story extension.");
        }

        int generatedContentLength = currentStory.length(); // 현재까지 생성된 글자 수
        int iteration = 0; // 반복 횟수
        final int MAX_ITERATIONS = 20; // 무한 루프 방지를 위한 최대 반복 횟수

        ObjectMapper mapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper 인스턴스

        try {
            // 목표 길이에 도달하거나 최대 반복 횟수를 초과할 때까지 반복
            while (generatedContentLength < desiredLength && iteration < MAX_ITERATIONS) {
                // 각 반복마다 AI 모델에 보낼 새로운 요청 DTO를 생성합니다.
                // 이는 이전 요청의 상태를 유지하면서 현재 시점의 컨텍스트를 전달하기 위함입니다.
                AddAIRequestDTO iterationRequestDTO = new AddAIRequestDTO();
                iterationRequestDTO.setEpisodeId(requestDTO.getEpisodeId());
                iterationRequestDTO.setChatListId(requestDTO.getChatListId());
                iterationRequestDTO.setUseChatList(requestDTO.isUseChatList());
                iterationRequestDTO.setOption(requestDTO.getOption()); // 여전히 STORY_EXTENSION 옵션
                // 파이썬 AI의 `/api/extension` 엔드포인트는 `extensionLength`를 초기 유효성 검사에만 사용하며,
                // 실제 생성 길이에는 영향을 미치지 않습니다. 전체 목표 길이를 전달합니다.
                iterationRequestDTO.setExtensionLength(desiredLength);
                iterationRequestDTO.setQuestion(currentStory);

                // AI 모델에게 현재까지 생성된 소설 내용과 함께 '이어서 작성하라'는 지시를 전달합니다.
                // 파이썬 AI 서버의 `/api/extension` 엔드포인트는 이 'question' 필드를 소설의 컨텍스트로 이해하고 확장해야 합니다.
                // 파이썬 AI의 system_instruction이 "주어진 소설 내용을 응용하여 이어서 작성해줘."로 변경되었으므로,
                // 여기에 필요한 모든 컨텍스트와 지시를 담아 보냅니다.
//                iterationRequestDTO.setQuestion(currentStory + "\n\n이전 소설 내용을 이어서 작성해주세요. 주인공의 심리 묘사, 주변 풍경 묘사, 사건의 배경이나 흐름을 더 풍부하게 묘사하거나 새로운 사건을 추가하는 방식을 활용하여 이야기를 계속 진행해줘.단, 이전 소설 내용과 이어서 작성한 내용을 합쳐서 2만자를 넘으면 안되.");

                // 참고: beforeQuestionList, beforeResponseList, image 필드는 소설 확장 반복 호출에서는 직접적으로 사용되지 않습니다.
                // 만약 AI 엔드포인트가 채팅 기록을 필요로 한다면, 해당 엔드포인트에서 처리해야 합니다.

                // AI 모델에 POST 요청을 보냅니다.
                ResponseEntity<String> getResponseAI = restTemplate.postForEntity(url, iterationRequestDTO, String.class);
                String json = getResponseAI.getBody(); // AI 응답 본문 (JSON 문자열)

                // AI 응답 JSON을 AddAIResponseDTO 객체로 변환합니다.
                AddAIResponseDTO partResponseAIDTO = mapper.readValue(json, AddAIResponseDTO.class);

                // AI 응답이 유효하고 성공 상태이며, 내용이 비어있지 않은 경우에만 소설을 업데이트합니다.
                if (partResponseAIDTO != null && partResponseAIDTO.isStatus() && partResponseAIDTO.getResponse() != null) {
                    String newText = partResponseAIDTO.getResponse();
                    currentStory += newText; // 새로 생성된 텍스트를 현재 소설에 추가
                    generatedContentLength = currentStory.length(); // 총 글자 수 업데이트
                    log.info("Iteration {}: Generated {} characters. Total: {}", iteration + 1, newText.length(), generatedContentLength);
                } else {
                    // 유효하지 않은 응답이거나 status가 false인 경우 경고 로그를 남기고 반복을 중단합니다.
                    log.warn("Iteration {}: AI did not return valid response or status was false. Response: {}", iteration + 1, json);
                    // 이 경우, 부분적으로 생성된 내용이라도 반환하여 사용자에게 보여줄 수 있습니다.
                    break;
                }
                iteration++; // 반복 횟수 증가
            }

            // 최종적으로 완성된 소설 내용을 담은 응답 DTO를 생성하여 반환합니다.
            AddAIResponseDTO finalResponse = new AddAIResponseDTO();
            finalResponse.setStatus(true); // 성공 상태로 설정
            finalResponse.setResponse(currentStory); // 완성된 소설 내용 설정
            return finalResponse;

        } catch (Exception e) {
            // 소설 확장 로직 중 발생한 모든 예외를 AIRequestFailException으로 래핑하여 던집니다.
            log.error("소설 확장 중 오류 발생: {}", e.getMessage(), e);
            throw new AIRequestFailException("소설 확장 실패 : " + e);
        }
    }

    @Transactional
    public void addChatMessage(AddAIRequestDTO requestAiDTO, AddAIResponseDTO responseAIDTO) {
        ChatList chatList = chatListRepository.findById(requestAiDTO.getChatListId()).orElseThrow(
                () -> new NoSuchElementException("챗 ID를 찾을 수 없습니다!")
        );
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatList(chatList);
        chatMessage.setQuestionMessage(requestAiDTO.getQuestion());
        chatMessage.setResponseMessage(responseAIDTO.getResponse());

        chatMessageRepository.save(chatMessage);
    }

    public FindAIResponseDTO getChatList(User user, Long episodeId, Long chatListId) {
        if(chatListId != null) {

            List<ChatMessage> findAll = chatMessageRepository.findAllByChatList_ChatListId(chatListId);
            List<ChatMessageResponseDTO> responseDTOList = new ArrayList<>();
            if(!findAll.isEmpty()) {
                responseDTOList = findAll.stream().map(ChatMessageResponseDTO::new).toList();
            }
            ChatListResponseDTO chatListResponseDTO = new ChatListResponseDTO(chatListId, responseDTOList);
            return new FindAIResponseDTO(List.of(chatListResponseDTO));
        } else {
            List<ChatList> findChatList = chatListRepository.findAllByEpisode_EpisodesId(episodeId);

            List<ChatListResponseDTO> chatListResponseDTOList = findChatList.stream()
                    .map(chatList -> {
                        List<ChatMessageResponseDTO> chatMessages = chatList.getChatMessageList().stream()
                                .map(ChatMessageResponseDTO::new)
                                .toList();
                        return new ChatListResponseDTO(chatList.getChatListId(), chatMessages);
                    })
                    .toList();

            return new FindAIResponseDTO(chatListResponseDTOList);
        }
    }

    public Long addChatList(AddAIRequestDTO requestDTO) {
        Optional<ChatList> findChatList = chatListRepository.findByEpisode_EpisodesId(requestDTO.getEpisodeId());
        if(findChatList.isEmpty()) {
            EpisodeEntity episodeEntity = episodeRepository.findById(requestDTO.getEpisodeId()).orElseThrow(
                    () -> new NoSuchElementException("존재하지 않는 에피소드ID 입니다!")
            );
            ChatList chatList = new ChatList();
            chatList.setEpisode(episodeEntity);
            chatList.setCreatedAt(LocalDateTime.now());
            ChatList save = chatListRepository.save(chatList);
            log.info("새 ChatList 생성! : {}", save);

            return save.getChatListId();
        } else
            return null;
    }

    public void checkAccess(User user, Long episodeId) {
        EpisodeEntity episodeEntity = episodeRepository.findById(episodeId).orElseThrow(
                () -> new NoSuchElementException("에피소드ID를 찾을 수 없습니다!")
        );

        if(!episodeEntity.getNovel().getUser().equals(user)) {
            if(!user.getAuthorities().contains("ROLE_ADMIN"))
                throw new AccessDeniedException("권한이 없습니다!");
        }
    }
}
