package com.xelops.actionplan.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xelops.actionplan.config.TestConfig;
import com.xelops.actionplan.dto.BoardCreationUpdateDto;
import com.xelops.actionplan.dto.BoardDto;
import com.xelops.actionplan.dto.WorkspaceDto;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.service.BoardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardResource.class)
@ExtendWith(SpringExtension.class)
@Import(TestConfig.class)
@WithMockUser(roles = UserRoleEnum.Fields.SIMPLE_USER)
class BoardResourceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Test
    void shouldCreateBoardSuccessfully() throws Exception {
        // Arrange
        final var boardCreation = new BoardCreationUpdateDto("name", "Test Board", null, null, 1L, List.of());
        final var image = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "".getBytes());
        final var objectMapper = new ObjectMapper();
        final var boardCreationJson = objectMapper.writeValueAsString(boardCreation);
        final var body = new MockMultipartFile("boardCreation", "", MediaType.APPLICATION_JSON_VALUE, boardCreationJson.getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/boards")
                        .file(body)
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenBoardCreationIsInvalid() throws Exception {
        // Arrange
        final var image = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image-content".getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/boards")
                        .file("boardCreation", "".getBytes())
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenFunctionalExceptionOccurs() throws Exception {
        // Arrange
        final var boardCreation = new BoardCreationUpdateDto("name", "Duplicate Board", null, null, null, List.of());
        final var image = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image-content".getBytes());
        final var objectMapper = new ObjectMapper();
        final var boardCreationJson = objectMapper.writeValueAsString(boardCreation);
        final var body = new MockMultipartFile("boardCreation", "", MediaType.APPLICATION_JSON_VALUE, boardCreationJson.getBytes());

        doThrow(new FunctionalException("Board name is duplicate"))
                .when(boardService).create(boardCreation, image);

        // Act & Assert
        mockMvc.perform(multipart("/boards")
                        .file(body)
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateBoardSuccessfully() throws Exception {
        // Arrange
        final var boardUpdate = new BoardCreationUpdateDto("name", "Updated Board", null, null, 1L, List.of());
        final var image = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "".getBytes());
        final var objectMapper = new ObjectMapper();
        final var boardUpdateJson = objectMapper.writeValueAsString(boardUpdate);
        final var body = new MockMultipartFile("boardUpdate", "", MediaType.APPLICATION_JSON_VALUE, boardUpdateJson.getBytes());
        final var boardId = 1L;

        doNothing().when(boardService).update(boardId, boardUpdate, image);

        // Act & Assert
        mockMvc.perform(multipart("/boards/{boardId}", boardId)
                        .file(body)
                        .file(image)
                        .with(request -> {
                            request.setMethod(HttpMethod.PATCH.name());
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenBoardUpdateIsInvalid() throws Exception {
        // Arrange
        final var image = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "".getBytes());
        final var boardId = 1L;

        // Act & Assert
        mockMvc.perform(multipart("/boards/{boardId}", boardId)
                        .file("boardUpdate", "".getBytes())
                        .file(image)
                        .with(request -> {
                            request.setMethod(HttpMethod.PATCH.name());
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenFunctionalExceptionOccursOnUpdate() throws Exception {
        // Arrange
        final var boardUpdate = new BoardCreationUpdateDto("name", "Duplicate Board", null, null, null, List.of());
        final var image = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "".getBytes());
        final var objectMapper = new ObjectMapper();
        final var boardUpdateJson = objectMapper.writeValueAsString(boardUpdate);
        final var body = new MockMultipartFile("boardUpdate", "", MediaType.APPLICATION_JSON_VALUE, boardUpdateJson.getBytes());
        final var boardId = 1L;

        doThrow(new FunctionalException("Board name is duplicate"))
                .when(boardService).update(boardId, boardUpdate, image);

        // Act & Assert
        mockMvc.perform(multipart("/boards/{boardId}", boardId)
                        .file(body)
                        .file(image)
                        .with(request -> {
                            request.setMethod(HttpMethod.PATCH.name());
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetUserBoardsSuccessfully() throws Exception {
        // Arrange
        final var page = 0;
        final var size = 50;
        final var boardDtoPage = new PageImpl<>(List.of(new BoardDto(1L, "Board 1", "fds", mock(WorkspaceDto.class), 1L, LocalDate.now(), 3, "image", List.of())));
        when(boardService.getUserBoards(null, page, size)).thenReturn(boardDtoPage);

        // Act & Assert
        mockMvc.perform(get("/boards")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
