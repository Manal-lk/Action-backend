package com.xelops.actionplan.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xelops.actionplan.config.TestConfig;
import com.xelops.actionplan.dto.comment.CommentDto;
import com.xelops.actionplan.dto.comment.CreateCommentDto;
import com.xelops.actionplan.dto.comment.UpdateCommentDto;
import com.xelops.actionplan.enumeration.UserRoleEnum;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentResource.class)
@ExtendWith(SpringExtension.class)
@Import(TestConfig.class)
@WithMockUser(roles = UserRoleEnum.Fields.SIMPLE_USER)
class CommentResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createComment_success() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto("Test comment");
        CommentDto commentDto = new CommentDto();
        when(commentService.createComment(any(CreateCommentDto.class), anyLong())).thenReturn(commentDto);

        mockMvc.perform(post("/actions/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createComment_validationError() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto(""); // Empty message to trigger validation error

        mockMvc.perform(post("/actions/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createComment_actionNotFound() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto("Test comment");
        when(commentService.createComment(any(CreateCommentDto.class), anyLong())).thenThrow(new NotFoundException("Action not found"));

        mockMvc.perform(post("/actions/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_success() throws Exception {
        UpdateCommentDto updateCommentDto = new UpdateCommentDto("Updated comment");
        CommentDto commentDto = new CommentDto();
        when(commentService.updateComment(any(UpdateCommentDto.class), anyLong(), anyLong())).thenReturn(commentDto);

        mockMvc.perform(put("/actions/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateComment_validationError() throws Exception {
        UpdateCommentDto updateCommentDto = new UpdateCommentDto(""); // Empty message to trigger validation error

        mockMvc.perform(put("/actions/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateComment_commentNotFound() throws Exception {
        UpdateCommentDto updateCommentDto = new UpdateCommentDto("Updated comment");
        when(commentService.updateComment(any(UpdateCommentDto.class), anyLong(), anyLong())).thenThrow(new NotFoundException("Comment not found"));

        mockMvc.perform(put("/actions/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_userNotCreator() throws Exception {
        UpdateCommentDto updateCommentDto = new UpdateCommentDto("Updated comment");
        when(commentService.updateComment(any(UpdateCommentDto.class), anyLong(), anyLong())).thenThrow(new FunctionalException("User cannot edit this comment"));

        mockMvc.perform(put("/actions/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteComment_success() throws Exception {
        doNothing().when(commentService).deleteComment(anyLong(), anyLong());

        mockMvc.perform(delete("/actions/1/comments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteComment_commentNotFound() throws Exception {
        doNothing().when(commentService).deleteComment(anyLong(), anyLong());
        doThrow(new NotFoundException("Comment not found")).when(commentService).deleteComment(anyLong(), anyLong());

        mockMvc.perform(delete("/actions/1/comments/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteComment_userNotCreator() throws Exception {
        doNothing().when(commentService).deleteComment(anyLong(), anyLong());
        doThrow(new FunctionalException("User cannot delete this comment")).when(commentService).deleteComment(anyLong(), anyLong());

        mockMvc.perform(delete("/actions/1/comments/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCommentsByActionId_success() throws Exception {
        when(commentService.getCommentsByActionId(anyLong())).thenReturn(List.of(new CommentDto()));

        mockMvc.perform(get("/actions/1/comments"))
                .andExpect(status().isOk());
    }
}
