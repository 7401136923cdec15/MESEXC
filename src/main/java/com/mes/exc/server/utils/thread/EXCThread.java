package com.mes.exc.server.utils.thread;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mes.exc.server.service.EXCService;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.utils.DesUtil;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.serviceimpl.EXCServiceImpl;
import com.mes.exc.server.utils.SessionContants;

@Component
public class EXCThread implements DisposableBean {
	private static final Logger logger = LoggerFactory.getLogger(EXCThread.class);
	@Autowired
	EXCService wEXCService = new EXCServiceImpl();
	private static EXCThread Instance;

	@PostConstruct
	public void init() {
		Instance = this;
		Instance.AdminUser = this.AdminUser;
		Instance.wEXCService = this.wEXCService;

		Run();
		// 初使化时将已静态化的testService实例化
	}

	public EXCThread() {
		super();
		AdminUser.ID = -100;
		AdminUser.LoginName = DesUtil.encrypt("SHRISMCIS", SessionContants.appSecret);
		AdminUser.Password = DesUtil.encrypt("shrismcis", SessionContants.appSecret);
		AdminUser.CompanyID = 0;
	}

	private BMSEmployee AdminUser = new BMSEmployee();

	public EXCThread(BMSEmployee wLoginUser) {
		super();
		AdminUser = wLoginUser;
	}

	boolean mIsStart = false;

	private void Run() {
		try {
			if (mIsStart)
				return;
			mIsStart = true;
			logger.info("EXC Start!!");
			new Thread(() -> {
				while (mIsStart) {
					try {
						// 睡眠
						Thread.sleep(10000);
						// 自动超时上报、超班转发
						Instance.wEXCService.EXC_AutoOverTimeReportAndOverShiftForward(Instance.AdminUser);
						// 流程引擎版超时上报
						Instance.wEXCService.EXC_OverTimeReportBPM(Instance.AdminUser);
					} catch (Exception ex) {
						logger.error(ex.toString());
					}

				}
			}).start();
		} catch (Exception ex) {
			logger.error(StringUtils.Format("exc start failed error:{0}", ex.toString()));
		}
	}

	@Override
	public void destroy() throws Exception {

	}

}
