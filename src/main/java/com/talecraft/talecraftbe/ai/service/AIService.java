package com.talecraft.talecraftbe.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talecraft.talecraftbe.ai.dto.data.AIOptions;
import com.talecraft.talecraftbe.ai.dto.request.AddAIRequestDTO;
import com.talecraft.talecraftbe.ai.dto.request.FindAIRequestDTO;
import com.talecraft.talecraftbe.ai.dto.response.AddAIResponseDTO;
import com.talecraft.talecraftbe.ai.dto.response.ChatMessageResponseDTO;
import com.talecraft.talecraftbe.ai.dto.response.FindAIResponseDTO;
import com.talecraft.talecraftbe.ai.exception.AIRequestFailException;
import com.talecraft.talecraftbe.ai.model.entity.ChatList;
import com.talecraft.talecraftbe.ai.model.entity.ChatMessage;
import com.talecraft.talecraftbe.ai.repository.ChatListRepository;
import com.talecraft.talecraftbe.ai.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AIService {
    @Value("${ai.url}")
    private String alURL;

    private final RestTemplate restTemplate;

    private final ChatMessageRepository chatMessageRepository;
    private final ChatListRepository chatListRepository;

    public AIService(RestTemplate restTemplate, ChatMessageRepository chatMessageRepository, ChatListRepository chatListRepository) {
        this.restTemplate = restTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.chatListRepository = chatListRepository;
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

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setQuestionMessage(requestAiDTO.getQuestion());
        chatMessage.setResponseMessage(responseAIDTO.getResponse());

        chatMessageRepository.save(chatMessage);
    }

    public FindAIResponseDTO getChatList(FindAIRequestDTO requestDTO) {
        if(requestDTO.getChatListId() != null) {
            List<ChatMessage> findAll = chatMessageRepository.findAllByChatList_ChatListId(requestDTO.getChatListId());
            List<ChatMessageResponseDTO> responseDTOList = new ArrayList<>();
            if(!findAll.isEmpty()) {
                responseDTOList = findAll.stream().map(ChatMessageResponseDTO::new).toList();
            }
            return new FindAIResponseDTO(responseDTOList);
        } else {
            ChatList findChatList = chatListRepository.findById(requestDTO.getNovelChapterId()).orElseThrow(
                    () -> new NoSuchElementException("챗 목록을 찾지 못했습니다." +
                            "\n찾은 ID: " + requestDTO.getChatListId())

            );
            List<ChatMessageResponseDTO> responseDTOList = findChatList.getChatMessageList().stream().map(ChatMessageResponseDTO::new).toList();
            return new FindAIResponseDTO(responseDTOList);
        }
    }
}
