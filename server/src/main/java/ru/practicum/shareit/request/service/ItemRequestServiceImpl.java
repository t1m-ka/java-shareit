package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.PageParamsMaker.makePageable;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addItemRequest(Long requestorId, ItemRequestDto itemRequestDto) {
        User requestor = findUserOrThrowException(requestorId);
        ItemRequest itemRequest = new ItemRequest(
                itemRequestDto.getDescription(),
                requestor,
                LocalDateTime.now()
        );
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoWithAnswers> getUserItemRequestList(Long userId) {
        findUserOrThrowException(userId);

        List<ItemRequest> itemRequestList = itemRequestRepository
                .findAllByRequestorId(userId, Sort.by(Sort.Direction.DESC, "created"));

        return itemRequestList.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDtoWithAnswers(
                        itemRequest,
                        findItemDtoListByRequestId(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    private List<ItemDto> findItemDtoListByRequestId(long requestId) {
        return itemRepository.findAllByItemRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoWithAnswers> getOtherUsersItemRequestList(Long userId, Integer from, Integer size) {
        findUserOrThrowException(userId);
        Pageable page = makePageable(from, size);

        return itemRequestRepository.findAllByRequestorIdNot(userId, page).stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDtoWithAnswers(
                        itemRequest,
                        findItemDtoListByRequestId(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoWithAnswers getItemRequest(Long userId, Long requestId) {
        findUserOrThrowException(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new EntityNotFoundException("Запрос с id=" + userId + " не найден"));

        return ItemRequestMapper.toItemRequestDtoWithAnswers(itemRequest, findItemDtoListByRequestId(requestId));
    }

    private User findUserOrThrowException(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}
