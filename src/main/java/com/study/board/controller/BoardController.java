package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("/board/write") // localhost:8080/board/write
    public String boardWriteForm() {
        return "boardwrite";
    }

    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception {
        if (!board.getTitle().isEmpty() && !board.getContent().isEmpty()) {
            if (file.getOriginalFilename() == "") {
                boardService.write(board, null);
            } else {
                boardService.write(board, file);
            }
            model.addAttribute("messege", "글 작성이 완료되었습니다!");
        } else {
            model.addAttribute("messege", "글 작성에 실패했습니다 ㅠ");
        }

        model.addAttribute("serchUrl", "/board/list");

        return "messege";
    }

    @GetMapping("/board/list")
    public String boardList(Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword) {

        Page<Board> list = null;

        if (searchKeyword == null) {
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword, pageable);
        }



        int nowPage = pageable.getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);


        return "boardList";
    }

    @GetMapping("/board/view") // localhost:8080/board/view?id=1
    public String boardView(Model model, Integer id) {
        model.addAttribute("board", boardService.boardView(id));

        return "boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(int id) {
        boardService.boardDelete(id);

        return "redirect:/board/list";
    }

    @GetMapping("/board/likes")
    public String boardLikes(int id) {
        boardService.boardLikes(id);

        return "redirect:/board/list";
    }

    @GetMapping("/board/modify/{id}")
    public String baordModify(@PathVariable("id") int id, Model model) {
        model.addAttribute("board", boardService.boardView(id));

        return "boardmodify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") int id, Board board, Model model) throws Exception {
        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        boardService.write(boardTemp, null);


        model.addAttribute("messege", "글 수정이 완료되었습니다!");


        model.addAttribute("serchUrl", "/board/list");

        return "messege";
    }
}
