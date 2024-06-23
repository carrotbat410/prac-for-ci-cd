package carrotbat410.lol.controller;

import carrotbat410.lol.dto.board.BoardDTO;
import carrotbat410.lol.dto.board.WriteBoardRequestDTO;
import carrotbat410.lol.dto.result.SuccessResult;
import carrotbat410.lol.service.BoardService;
import carrotbat410.lol.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("/my/boards")
    public SuccessResult<Page<BoardDTO>> getMyBoards(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserIdFromAuthentication();

        Page<BoardDTO> boardsPage = boardService.getMyBoards(userId, pageable);
        return new SuccessResult<>("ok", boardsPage, boardsPage.getTotalElements());//TODO getTotalElements count query 최적화하기
    }

    @GetMapping("/boards")
    public SuccessResult<Page<BoardDTO>> getAllBoards(Pageable pageable) {
        Page<BoardDTO> boardsPage = boardService.getAllBoards(pageable);
        return new SuccessResult<>("ok", boardsPage, boardsPage.getTotalElements());//TODO getTotalElements count query 최적화하기
    }

    @PostMapping("/board")
    public SuccessResult writeBoard(@RequestBody @Validated WriteBoardRequestDTO request) {
        Long userId = SecurityUtils.getCurrentUserIdFromAuthentication();
        boardService.writeBoard(userId, request);
        return new SuccessResult<>("ok");
    }

}
