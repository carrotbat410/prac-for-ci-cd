package carrotbat410.lol.controller;

import carrotbat410.lol.controller.customMockUser.WithMockCustomUser;
import carrotbat410.lol.dto.board.BoardCategory;
import carrotbat410.lol.dto.board.BoardDTO;
import carrotbat410.lol.dto.board.WriteBoardRequestDTO;
import carrotbat410.lol.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebMvcTest(controllers = BoardController.class)
@WithMockCustomUser
class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardService boardService;

    @Test
    @DisplayName("나의 게시글 목록을 조회할 수 있다.")
    void getMyBoards() throws Exception {
        // given
        BoardDTO boardDTO1 = new BoardDTO(1L, "title1", "content1", 1L, "user1");
        BoardDTO boardDTO2 = new BoardDTO(2L, "title2", "content2", 2L, "user2");
        List<BoardDTO> content = List.of(boardDTO1, boardDTO2);
        PageRequest pageRequest = PageRequest.of(0, 30);
        PageImpl<BoardDTO> boardsPage = new PageImpl<>(content, pageRequest, content.size());

        // stubbing
        when(boardService.getMyBoards(any(), any())).thenReturn(boardsPage);

        // when // then
        mockMvc.perform(get("/my/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(content.size()));
    }

    @Test
    @DisplayName("게시된 글 목록을 조회할 수 있다.")
    void getAllBoards() throws Exception {
        // given
        BoardDTO boardDTO1 = new BoardDTO(1L, "title1", "content1", 1L, "user1");
        BoardDTO boardDTO2 = new BoardDTO(2L, "title2", "content2", 2L, "user2");
        BoardDTO boardDTO3 = new BoardDTO(3L, "title3", "content3", 3L, "user3");
        List<BoardDTO> content = List.of(boardDTO1, boardDTO2, boardDTO3);
        PageRequest pageRequest = PageRequest.of(0, 30);
        PageImpl<BoardDTO> boardsPage = new PageImpl<>(content, pageRequest, content.size());

        // stubbing
        when(boardService.getAllBoards(any())).thenReturn(boardsPage);

        // when // then
        mockMvc.perform(get("/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(3));
    }

    @Test
    @DisplayName("제목없이 글을 작성하면 예외를 던진다.")
    void writeBoardWithoutTitle() throws Exception {
        // given
        WriteBoardRequestDTO request1 = new WriteBoardRequestDTO("", "content1", BoardCategory.RECRUIT);
        WriteBoardRequestDTO request2 = new WriteBoardRequestDTO(null, "content1", BoardCategory.RECRUIT);

        // stubbing
        doNothing().when(boardService).writeBoard(any(), any());

        // when // then
        mockMvc.perform(post("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("게시글의 제목을 입력해주세요."))
                .andExpect(jsonPath("$.fieldName").value("title"));

        mockMvc.perform(post("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("게시글의 제목을 입력해주세요."))
                .andExpect(jsonPath("$.fieldName").value("title"));
    }

    @Test
    @DisplayName("내용없이 글을 작성하면 예외를 던진다.")
    void writeBoardWithoutContent() throws Exception {
        // given
        WriteBoardRequestDTO request1 = new WriteBoardRequestDTO("title1", "", BoardCategory.RECRUIT);
        WriteBoardRequestDTO request2 = new WriteBoardRequestDTO("title2", null, BoardCategory.RECRUIT);

        // stubbing
        doNothing().when(boardService).writeBoard(any(), any());

        // when // then
        mockMvc.perform(post("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("게시글의 내용을 입력해주세요."))
                .andExpect(jsonPath("$.fieldName").value("content"));

        mockMvc.perform(post("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("게시글의 내용을 입력해주세요."))
                .andExpect(jsonPath("$.fieldName").value("content"));
    }

    @Test
    @DisplayName("게시글을 올릴 수 있다.")
    void writeBoard() throws Exception {
        // given
        WriteBoardRequestDTO request = new WriteBoardRequestDTO("title1", "content1", BoardCategory.RECRUIT);

        // stubbing
        doNothing().when(boardService).writeBoard(any(), any());

        // when // then
        mockMvc.perform(post("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"));
    }
}