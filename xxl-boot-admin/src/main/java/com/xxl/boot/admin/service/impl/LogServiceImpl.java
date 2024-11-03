package com.xxl.boot.admin.service.impl;

import com.xxl.boot.admin.mapper.LogMapper;
import com.xxl.boot.admin.model.adaptor.XxlBootLogAdaptor;
import com.xxl.boot.admin.model.dto.XxlBootLogDTO;
import com.xxl.boot.admin.model.entity.XxlBootLog;
import com.xxl.boot.admin.service.LogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import com.xxl.tool.response.Response;
import com.xxl.tool.response.ResponseBuilder;
import com.xxl.tool.response.PageModel;

/**
* XxlBootLog Service Impl
*
* Created by xuxueli on '2024-10-27 12:19:06'.
*/
@Service
public class LogServiceImpl implements LogService {

	@Resource
	private LogMapper logMapper;

	/**
    * 新增
    */
	@Override
	public Response<String> insert(XxlBootLog xxlBootLog) {

		// valid
		if (xxlBootLog == null) {
			return new ResponseBuilder<String>().fail("必要参数缺失").build();
        }

		logMapper.insert(xxlBootLog);
		return new ResponseBuilder<String>().success().build();
	}

	/**
	* 删除
	*/
	@Override
	public Response<String> delete(List<Integer> ids) {
		int ret = logMapper.delete(ids);
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* 更新
	*/
	@Override
	public Response<String> update(XxlBootLog xxlBootLog) {
		int ret = logMapper.update(xxlBootLog);
		return ret>0? new ResponseBuilder<String>().success().build()
					: new ResponseBuilder<String>().fail().build() ;
	}

	/**
	* Load查询
	*/
	@Override
	public Response<XxlBootLog> load(int id) {
		XxlBootLog record = logMapper.load(id);
		return new ResponseBuilder<XxlBootLog>().success(record).build();
	}

	/**
	* 分页查询
	*/
	@Override
	public PageModel<XxlBootLogDTO> pageList(int type, String module, String title, int offset, int pagesize) {

		List<XxlBootLog> pageList = logMapper.pageList(offset, pagesize);
		int totalCount = logMapper.pageListCount(offset, pagesize);

		List<XxlBootLogDTO> pageListDTO = XxlBootLogAdaptor.adaptor(pageList);

		// result
		PageModel<XxlBootLogDTO> pageModel = new PageModel<XxlBootLogDTO>();
		pageModel.setPageData(pageListDTO);
		pageModel.setTotalCount(totalCount);

		return pageModel;
	}

}
