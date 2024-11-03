package com.xxl.boot.admin.service.impl;

import com.xxl.boot.admin.mapper.MessageMapper;
import com.xxl.boot.admin.model.adaptor.XxlBootMesssageAdaptor;
import com.xxl.boot.admin.model.dto.LoginUserDTO;
import com.xxl.boot.admin.model.dto.XxlBootMessageDTO;
import com.xxl.boot.admin.model.entity.XxlBootMessage;
import com.xxl.boot.admin.service.MessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.xxl.tool.response.Response;
import com.xxl.tool.response.ResponseBuilder;
import com.xxl.tool.response.PageModel;

/**
* XxlBootMessage Service Impl
*
* Created by xuxueli on '2024-11-03 11:03:29'.
*/
@Service
public class MessageServiceImpl implements MessageService {

	@Resource
	private MessageMapper messageMapper;

	/**
    * 新增
    */
	@Override
	public Response<String> insert(XxlBootMessage xxlBootMessage, LoginUserDTO loginUser) {

		// valid
		if (xxlBootMessage == null) {
			return new ResponseBuilder<String>().fail("必要参数缺失").build();
        }
		xxlBootMessage.setSender(loginUser.getUsername());

		messageMapper.insert(xxlBootMessage);
		return new ResponseBuilder<String>().success().build();
	}

	/**
	* 删除
	*/
	@Override
	public Response<String> delete(List<Integer> ids) {
		int ret = messageMapper.delete(ids);
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* 更新
	*/
	@Override
	public Response<String> update(XxlBootMessage xxlBootMessage) {
		int ret = messageMapper.update(xxlBootMessage);
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* Load查询
	*/
	@Override
	public Response<XxlBootMessage> load(int id) {
		XxlBootMessage record = messageMapper.load(id);
		return new ResponseBuilder<XxlBootMessage>().success(record).build();
	}

	/**
	* 分页查询
	*/
	@Override
	public PageModel<XxlBootMessageDTO> pageList(int status, String title, int offset, int pagesize) {

		List<XxlBootMessage> pageList = messageMapper.pageList(status, title, offset, pagesize);
		int totalCount = messageMapper.pageListCount(status, title, offset, pagesize);

		// adaptor
		List<XxlBootMessageDTO> dtoList = XxlBootMesssageAdaptor.adaptor(pageList);

		// result
		PageModel<XxlBootMessageDTO> pageModel = new PageModel<XxlBootMessageDTO>();
		pageModel.setPageData(dtoList);
		pageModel.setTotalCount(totalCount);

		return pageModel;
	}

}
