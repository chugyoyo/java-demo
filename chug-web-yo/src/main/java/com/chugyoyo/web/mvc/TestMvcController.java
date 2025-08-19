package com.chugyoyo.web.mvc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = "test-mvc")
@Controller
public class TestMvcController {

    /**
     * <a href="http://localhost:8081/test-mvc/testModelAndView">
     *     visit with 浏览器，http://localhost:8081/test-mvc/testModelAndView </a>
     */
    @GetMapping("/testModelAndView")
    public ModelAndView testModelAndView() {
        // 写 new ModelAndView("/test-mvc-model") 会变成 /templates//test-mvc-model.html 找不到
        ModelAndView mv = new ModelAndView("test-mvc-model");
        mv.addObject("user", new UserDTO(1, "李四"));
        return mv;
    }
}

@Getter
@AllArgsConstructor
class UserDTO {
    private Integer id;
    private String name;
}
