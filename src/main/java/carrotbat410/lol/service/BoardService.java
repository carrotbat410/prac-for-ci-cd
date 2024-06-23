package carrotbat410.lol.service;

import carrotbat410.lol.dto.board.BoardDTO;
import carrotbat410.lol.dto.board.WriteBoardRequestDTO;
import carrotbat410.lol.entity.Board;
import carrotbat410.lol.entity.User;
import carrotbat410.lol.exhandler.exception.AccessDeniedException;
import carrotbat410.lol.repository.BoardRepository;
import carrotbat410.lol.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    UserRepository userRepository;

    public Page<BoardDTO> getMyBoards(Long userId, Pageable pageable) {
        Page<Board> boardsPage = boardRepository.findByUserId(userId, pageable);

        return boardsPage.map(board -> new BoardDTO(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getUser().getId(),
                board.getUser().getUsername()
        ));
    }

    public Page<BoardDTO> getAllBoards(Pageable pageable) {
        Page<Board> boardsPage = boardRepository.findAll(pageable);

        return boardsPage.map(board -> new BoardDTO(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getUser().getId(),
                board.getUser().getUsername()
        ));
    }

    public void writeBoard(Long userId, WriteBoardRequestDTO request) {
        //TODO 찾을 필요가 있을까? userRepository.getReferenceById(userId) 메서드?? 찾아보기
        User user = userRepository.findById(userId).orElseThrow(()-> new AccessDeniedException("존재하지 않는 유저입니다. 재 로그인후 다시 요청해주세요."));
        Board newBoard = new Board(null, request.getTitle(), request.getContent(), request.getBoardCategory(), user);
        boardRepository.save(newBoard);
    }
}
