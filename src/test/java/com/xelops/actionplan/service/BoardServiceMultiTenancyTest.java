package com.xelops.actionplan.service;

import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.domain.Board;
import com.xelops.actionplan.domain.Organization;
import com.xelops.actionplan.domain.Workspace;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.repository.BoardRepository;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Test class to verify organization-based multi-tenancy isolation
 */
@ExtendWith(MockitoExtension.class)
class BoardServiceMultiTenancyTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserHelperService userHelperService;

    @Mock
    private Messages messages;

    @InjectMocks
    private BoardService boardService;

}
