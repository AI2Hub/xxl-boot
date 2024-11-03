package com.xxl.deep.admin.controller.system;

import com.xxl.deep.admin.annotation.Permission;
import com.xxl.deep.admin.constant.enums.MessageCategoryEnum;
import com.xxl.deep.admin.constant.enums.MessageStatusEnum;
import com.xxl.deep.admin.model.dto.LoginUserDTO;
import com.xxl.deep.admin.model.dto.XxlDeepMessageDTO;
import com.xxl.deep.admin.model.entity.XxlDeepMessage;
import com.xxl.deep.admin.service.MessageService;
import com.xxl.deep.admin.service.impl.LoginService;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;
import com.xxl.tool.response.ResponseBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * XxlDeepMessage Controller
 *
 * Created by xuxueli on '2024-11-03 11:03:29'.
 */
@Controller
@RequestMapping("/system/message")
public class MessageController {

    @Resource
    private MessageService messageService;
    @Resource
    private LoginService loginService;

    /**
     * 页面
     */
    @RequestMapping
    @Permission
    public String index(Model model) {

        model.addAttribute("MessageCategoryEnum", MessageCategoryEnum.values());
        model.addAttribute("MessageStatusEnum", MessageStatusEnum.values());

        return "system/message";
    }

    /**
     * 分页查询
     */
    @RequestMapping("/pageList")
    @ResponseBody
    @Permission
    public Response<PageModel<XxlDeepMessageDTO>> pageList(int status, String title,
                                                           @RequestParam(required = false, defaultValue = "0") int start,
                                                           @RequestParam(required = false, defaultValue = "10") int length) {
        PageModel<XxlDeepMessageDTO> pageModel = messageService.pageList(status, title, start, length);
        return new ResponseBuilder<PageModel<XxlDeepMessageDTO>>().success(pageModel).build();
    }

    /**
     * Load查询
     */
    @RequestMapping("/load")
    @ResponseBody
    @Permission
    public Response<XxlDeepMessage> load(int id){
        return messageService.load(id);
    }

    /**
     * 新增
     */
    @RequestMapping("/insert")
    @ResponseBody
    @Permission
    public Response<String> insert(XxlDeepMessage xxlDeepMessage, HttpServletRequest request){
        LoginUserDTO loginUser = loginService.getLoginUser(request);
        return messageService.insert(xxlDeepMessage, loginUser);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @ResponseBody
    @Permission
    public Response<String> delete(@RequestParam("ids[]") List<Integer> ids){
        return messageService.delete(ids);
    }

    /**
     * 更新
     */
    @RequestMapping("/update")
    @ResponseBody
    @Permission
    public Response<String> update(XxlDeepMessage xxlDeepMessage){
        return messageService.update(xxlDeepMessage);
    }

}
