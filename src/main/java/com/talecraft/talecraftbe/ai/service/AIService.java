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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

    public FindAIResponseDTO getChatList(Long episodeId, Long chatListId) {
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
}
