package com.mes.exc.server.controller.test;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.exc.server.controller.BaseController;
import com.mes.exc.server.service.po.OutResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.serviceimpl.dao.exc.EXCCallTaskBPMDAO;
import com.mes.exc.server.utils.RetCode;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2020-4-2 16:57:38
 * @LastEditTime 2020-4-2 16:57:41
 */
@RestController
@RequestMapping("/api/Test")
public class TestController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(TestController.class);

	/**
	 * 接口测试
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/Test")
	public Object Test(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			testAndon(wLoginUser);

			ServiceResult<Integer> wServiceResult = new ServiceResult<Integer>();
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	private void testAndon(BMSEmployee wLoginUser) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			String wResult = EXCCallTaskBPMDAO.getInstance().GetNewCode(wLoginUser, wErrorCode);

			System.out.println(wResult);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}
}
