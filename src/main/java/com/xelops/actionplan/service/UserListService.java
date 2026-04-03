package com.xelops.actionplan.service;

import com.xelops.actionplan.dto.UserFilterRequestDto;
import com.xelops.actionplan.dto.UserSimplifiedDto;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.mapper.UserMapper;
import com.xelops.actionplan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserListService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserHelperService userHelperService;

    /**
     * Retourne tous les utilisateurs de l'organisation du user connecté.
     * Applique les filtres search et roles si fournis.
     */
    public List<UserSimplifiedDto> filterUsers(UserFilterRequestDto filterRequest) throws NotFoundException {
        log.info("Start service filterUsers | filterRequest: {}", filterRequest);

        Long organizationId = userHelperService.getOrganizationIdFromUser();

        // Récupère tous les users de l'organisation
        var users = userRepository.findByOrganizationId(organizationId);

        // Filtre par search (fullname ou username, insensible à la casse)
        if (filterRequest != null && filterRequest.getSearch() != null && !filterRequest.getSearch().isBlank()) {
            String search = filterRequest.getSearch().toLowerCase();
            users = users.stream()
                    .filter(u -> (u.getFullname() != null && u.getFullname().toLowerCase().contains(search))
                            || (u.getUsername() != null && u.getUsername().toLowerCase().contains(search)))
                    .toList();
        }

        // Filtre par roles si fournis
        if (filterRequest != null && filterRequest.getRoles() != null && !filterRequest.getRoles().isEmpty()) {
            users = users.stream()
                    .filter(u -> u.getRole() != null && filterRequest.getRoles().contains(u.getRole().name()))
                    .toList();
        }

        var result = users.stream()
                .map(userMapper::toUserSimplified)
                .toList();

        log.info("End service filterUsers | found {} users", result.size());
        return result;
    }
}